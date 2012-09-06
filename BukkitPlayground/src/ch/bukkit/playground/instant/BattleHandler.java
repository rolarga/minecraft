package ch.bukkit.playground.instant;

import ch.bukkit.playground.instant.model.*;
import ch.bukkit.playground.instant.tasks.BroadcastTask;
import ch.bukkit.playground.instant.tasks.MessageTask;
import ch.bukkit.playground.instant.tasks.SpawnTask;
import ch.bukkit.playground.interfaces.thirdparty.EconomyApi;
import ch.bukkit.playground.util.DateHelper;
import ch.bukkit.playground.util.LocationHelper;
import ch.bukkit.playground.util.PlayerUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.logging.Logger;

public class BattleHandler {

    private BattleConfiguration battleConfiguration;
    private BattleData battleData;
    private String name;

    private Timer battleTimer;
    private static Logger logger = Logger.getLogger("BattleHandler");


    public BattleHandler(String name, BattleConfiguration battleConfiguration, BattleData battleData) {
        this.battleConfiguration = battleConfiguration;
        this.battleData = battleData;
        this.name = name;
    }

    public BattleHandler(String name) {
        this.name = name;
    }

    public void start() {
        if (battleData.getEndDate() != null && new Date().getTime() < (battleData.getEndDate().getTime() + 300000)) {
            logger.info("Cannot yet start an instant battle.");
            return;
        }

        logger.info("Starting instant battle.");

        // create new plain timer
        battleTimer = new Timer();

        Date tFirstMessage = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(battleConfiguration.getOffset()));
        Date tSecondMessage = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(battleConfiguration.getOffset() / 2));
        Date t = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(battleConfiguration.getDuration()));
        Date tPlus1Minute = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(battleConfiguration.getDuration() + 1));

        new BroadcastTask(ChatColor.BLUE + "A new " + battleConfiguration.getBattleType().getDisplayName() + " instant battle is open for registration. Press /instant join " + name + " to join the next battle. Next round at " + DateHelper.format(t)).run();

        battleData.addTask(new BroadcastTask(battleData.getRegisteredPlayers(), ChatColor.YELLOW + "A new " + battleConfiguration.getBattleType().getDisplayName() + " instant battle starts in 5 minutes, %players% players are registerd - Join now!"), tFirstMessage);
        battleData.addTask(new BroadcastTask(battleData.getRegisteredPlayers(), ChatColor.YELLOW + "A new " + battleConfiguration.getBattleType().getDisplayName() + " instant battle starts in 1 minutes, %players% players are registerd - Join now!."), tSecondMessage);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // get active players
                battleData.getActivePlayers().clear();

                // move online players
                int currentGroup = 0;
                for (Player player : battleData.getRegisteredPlayers()) {
                    if (player.isOnline()) {
                        Location loc = player.getLocation();
                        player.teleport(battleConfiguration.getPosStart());
                        battleData.addActivePlayer(player, loc);

                        // make player ready for the fight
                        if (!player.isOp()) {
                            player.setFlying(false);
                            player.setAllowFlight(false);
                            player.setGameMode(GameMode.SURVIVAL);
                            player.setHealth(player.getMaxHealth());
                            player.setFoodLevel(20);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You were not added to the instant battle as you were offline at that time.");
                    }
                }
                battleData.getRegisteredPlayers().clear();
                battleData.setTotalActivePlayers(battleData.getActivePlayers().size());
                battleData.setGroups(PlayerUtil.getEqualDistributedGroupByLevel(battleConfiguration.getGroupAmount(), battleData.getActivePlayers().keySet()));
                InstantConfig.saveBattleHandler(BattleHandler.this);

                new BroadcastTask(battleData.getActivePlayers().keySet(), ChatColor.YELLOW + "A new instant battle starts now with %players% players!").run();
                new MessageTask(battleData.getActivePlayers().keySet(), ChatColor.RED + "Get ready for the fight, you have 1 minute to prepare yourself. There are %players% players with you fighting - enjoy!").run();
            }
        };
        battleData.addTask(timerTask, t);

        Date time = tPlus1Minute;
        double totalRounds = battleConfiguration.getTotalRounds();
        for (final Level level : battleConfiguration.getLevels()) {
            double multiplicator = level.getRoundQuantity() / totalRounds;
            int millisPerRound = (int) Math.max(DateHelper.getMillisForMinutes(battleConfiguration.getDuration()) * multiplicator, 1);

            // set the battle active when first spawn starts
            battleData.addTask(new TimerTask() {
                @Override
                public void run() {
                    battleData.setActive(true);
                }
            }, time);

            // add spawns and welcome messages go each level/round
            battleData.addTask(new MessageTask(battleData.getActivePlayers().keySet(), ChatColor.YELLOW + level.getWelcomeMessage()), time);
            for (final Round round : level.getRounds()) {
                for (Map.Entry<String, Integer> mob2Quantity : round.getMobs().entrySet()) {
                    battleData.addTask(new SpawnTask(battleConfiguration, battleData, mob2Quantity.getKey(), mob2Quantity.getValue()), time);
                }
                time = new Date(time.getTime() + millisPerRound);

                // at the end of each round, there are rewards for each remaining player
                battleData.addTask(new TimerTask() {
                    @Override
                    public void run() {
                        giveRewards(round);
                    }
                }, time);

                battleData.setEndDate(time);
            }
        }

        // finishBattle at the end
        battleData.addTask(new TimerTask() {
            @Override
            public void run() {
                finishBattle();
            }
        }, battleData.getEndDate());

        for (Map.Entry<TimerTask, Date> timerTaskDateEntry : battleData.getTasks().entrySet()) {
            battleTimer.schedule(timerTaskDateEntry.getKey(), timerTaskDateEntry.getValue());
        }

        // save the finished battle
        InstantConfig.saveBattleHandler(this);
    }

    public void stop() {
        if (battleTimer != null) battleTimer.cancel();
        cleanupBattle();

        battleData.getRegisteredPlayers().clear();
        clearActivePlayersAndTeleportBack();

        logger.info("Timers are stopped, battle is over.");

        InstantConfig.saveBattleHandler(this);
    }

    // distribute rewards
    // delete spawned mobs
    // delete dropped items
    // delete dropped exp
    public void finishBattle() {
        boolean allMobsDead = true;

        for (Entity entity : battleData.getSpawnedMobs()) {
            if (entity.isValid() && !entity.isDead()) {
                allMobsDead = false;
            }
        }

        // we found a mob which is not dead, so the players didnt win the battle
        if (allMobsDead) {
            for (Player player : battleData.getActivePlayers().keySet()) {
                if (!player.isDead()) {
                    player.sendMessage(ChatColor.GOLD + "You are one of the winners, congratulations!");
                } else {
                    player.sendMessage(ChatColor.GRAY + "Don't try to cheat - LOOSER!");
                }
            }
        }

        // teleport them back
        clearActivePlayersAndTeleportBack();

        // clear model
        if (battleConfiguration.getWorld() != null) {
            for (Entity entity : battleConfiguration.getWorld().getEntities()) {
                if ((entity.getType() == EntityType.DROPPED_ITEM || entity.getType() == EntityType.EXPERIENCE_ORB) &&
                        LocationHelper.isInSquare(battleConfiguration.getPos1(), battleConfiguration.getPos2(), entity.getLocation())) {
                    entity.remove();
                }
            }
        }

        cleanupBattle();

        InstantConfig.saveBattleHandler(this);

        if (battleConfiguration.isAutostart()) {
            start();
        }
    }

    private void giveRewards(Round round) {
        for (Map.Entry<Player, Location> activePlayer : battleData.getActivePlayers().entrySet()) {
            if (!activePlayer.getKey().isDead()) {
                List<ItemStack> itemStacks = new LinkedList<ItemStack>();
                for (Reward reward : round.getRewards()) {
                    if (reward.getId() > 0) {
                        itemStacks.add(new ItemStack(reward.getId(), reward.getQuantity()));
                    }
                    EconomyApi.add(activePlayer.getKey(), reward.getMoney());
                }
                activePlayer.getKey().getInventory().addItem(itemStacks.toArray(new ItemStack[itemStacks.size()]));
            }
        }
    }

    private void cleanupBattle() {
        for (TimerTask timerTask : battleData.getTasks().keySet()) {
            timerTask.cancel();
        }

        for (Entity entity : battleData.getSpawnedMobs()) {
            if (entity.isValid()) {
                entity.remove();
            }
        }
        battleData.setEndDate(null);
    }

    public void clearActivePlayersAndTeleportBack() {
        // add active and spectators to be ported back
        Map<Player, Location> playerLocationMap = new HashMap<Player, Location>(battleData.getActivePlayers());
        playerLocationMap.putAll(battleData.getOriginSpectatorLocations());

        battleData.getActivePlayers().clear();
        battleData.getOriginSpectatorLocations().clear();

        for (Map.Entry<Player, Location> playerLocationEntry : playerLocationMap.entrySet()) {
            playerLocationEntry.getKey().teleport(playerLocationEntry.getValue());
        }
    }

    public String getName() {
        return name;
    }

    public BattleConfiguration getBattleConfiguration() {
        return battleConfiguration;
    }

    public BattleData getBattleData() {
        return battleData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BattleHandler)) return false;

        BattleHandler that = (BattleHandler) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

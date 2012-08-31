package ch.bukkit.playground.instant;

import ch.bukkit.playground.instant.model.*;
import ch.bukkit.playground.instant.tasks.BroadcastTask;
import ch.bukkit.playground.instant.tasks.MessageTask;
import ch.bukkit.playground.instant.tasks.SpawnTask;
import ch.bukkit.playground.util.DateHelper;
import ch.bukkit.playground.util.LocationHelper;
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

    private ArenaConfiguration arenaConfiguration;
    private ArenaData arenaData;
    private String name;

    private Timer arenaTimer;
    private static Logger logger = Logger.getLogger("BattleHandler");


    public BattleHandler(String name, ArenaConfiguration arenaConfiguration, ArenaData arenaData) {
        this.arenaConfiguration = arenaConfiguration;
        this.arenaData = arenaData;
        this.name = name;
    }

    public BattleHandler(String name) {
        this.name = name;
    }

    public void start() {
        if (arenaData.getEndDate() != null && new Date().getTime() < (arenaData.getEndDate().getTime() + 300000)) {
            logger.info("Cannot yet start an instant battle.");
            return;
        }

        logger.info("Starting instant battle.");

        // create new plain timer
        arenaTimer = new Timer();

        Date tFirstMessage = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(arenaConfiguration.getOffset()));
        Date tSecondMessage = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(arenaConfiguration.getOffset() / 2));
        Date t = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(arenaConfiguration.getDuration()));
        Date tPlus1Minute = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(arenaConfiguration.getDuration() + 1));

        new BroadcastTask(ChatColor.BLUE + "A new " + arenaConfiguration.getBattleType().getDisplayName() + " instant battle is open for registration. Press /instant join " + name + " to join the next battle. Next round at " + DateHelper.format(t)).run();

        arenaData.addTask(new BroadcastTask(arenaData.getRegisteredPlayers(), ChatColor.YELLOW + "A new " + arenaConfiguration.getBattleType().getDisplayName() + " instant battle starts in 5 minutes, %players% players are registerd - Join now!"), tFirstMessage);
        arenaData.addTask(new BroadcastTask(arenaData.getRegisteredPlayers(), ChatColor.YELLOW + "A new " + arenaConfiguration.getBattleType().getDisplayName() + " instant battle starts in 1 minutes, %players% players are registerd - Join now!."), tSecondMessage);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // get active players
                arenaData.getActivePlayers().clear();

                // move online players
                int currentGroup = 0;
                for (Player player : arenaData.getRegisteredPlayersSortedByLevel()) {
                    if (player.isOnline()) {
                        Location loc = player.getLocation();
                        player.teleport(arenaConfiguration.getPosStart());
                        arenaData.addActivePlayer(player, loc);
                        arenaData.addPlayerToGroup(0, player);
                        currentGroup = (currentGroup + 1) % arenaConfiguration.getGroups();

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
                arenaData.getRegisteredPlayers().clear();
                arenaData.setTotalActivePlayers(arenaData.getActivePlayers().size());
                InstantConfig.saveBattleHandler(BattleHandler.this);

                new BroadcastTask(arenaData.getActivePlayers().keySet(), ChatColor.YELLOW + "A new instant battle starts now with %players% players!").run();
                new MessageTask(arenaData.getActivePlayers().keySet(), ChatColor.RED + "Get ready for the fight, you have 1 minute to prepare yourself. There are %players% players with you fighting - enjoy!").run();
            }
        };
        arenaData.addTask(timerTask, t);

        Date time = tPlus1Minute;
        double totalRounds = arenaConfiguration.getTotalRounds();
        for (final Level level : arenaConfiguration.getLevels()) {
            double multiplicator = level.getRoundQuantity() / totalRounds;
            int millisPerRound = (int) Math.max(DateHelper.getMillisForMinutes(arenaConfiguration.getDuration()) * multiplicator, 1);

            // set the arena active when first spawn starts
            arenaData.addTask(new TimerTask() {
                @Override
                public void run() {
                    arenaData.setActive(true);
                }
            }, time);

            // add spawns and welcome messages go each level/round
            arenaData.addTask(new MessageTask(arenaData.getActivePlayers().keySet(), ChatColor.YELLOW + level.getWelcomeMessage()), time);
            for (final Round round : level.getRounds()) {
                for (Map.Entry<Class<? extends Entity>, Integer> mob2Quantity : round.getMobs().entrySet()) {
                    arenaData.addTask(new SpawnTask(arenaConfiguration, arenaData, mob2Quantity.getKey(), mob2Quantity.getValue()), time);
                }
                time = new Date(time.getTime() + millisPerRound);

                // at the end of each round, there are rewards for each remaining player
                arenaData.addTask(new TimerTask() {
                    @Override
                    public void run() {
                        giveRewards(round);
                    }
                }, time);
            }
        }

        // finishArena at the end
        arenaData.addTask(new TimerTask() {
            @Override
            public void run() {
                finishArena();
            }
        }, arenaData.getEndDate());

        for (Map.Entry<TimerTask, Date> timerTaskDateEntry : arenaData.getTasks().entrySet()) {
            arenaTimer.schedule(timerTaskDateEntry.getKey(), timerTaskDateEntry.getValue());
        }

        // save the finished arena
        InstantConfig.saveBattleHandler(this);
    }

    public void stop() {
        arenaTimer.cancel();
        cleanupArena();

        arenaData.getRegisteredPlayers().clear();
        clearActivePlayersAndTeleportBack();

        logger.info("Timers are stopped, battle is over.");

        InstantConfig.saveBattleHandler(this);

    }

    // distribute rewards
    // delete spawned mobs
    // delete dropped items
    // delete dropped exp
    public void finishArena() {
        boolean allMobsDead = true;

        for (Entity entity : arenaData.getSpawnedMobs()) {
            if (entity.isValid() && !entity.isDead()) {
                allMobsDead = false;
            }
        }

        // we found a mob which is not dead, so the players didnt win the battle
        if (allMobsDead) {
            for (Player player : arenaData.getActivePlayers().keySet()) {
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
        for (Entity entity : arenaConfiguration.getPos1().getWorld().getEntities()) {
            if ((entity.getType() == EntityType.DROPPED_ITEM || entity.getType() == EntityType.EXPERIENCE_ORB) &&
                    LocationHelper.isInSquare(arenaConfiguration.getPos1(), arenaConfiguration.getPos2(), entity.getLocation())) {
                entity.remove();
            }
        }

        cleanupArena();

        InstantConfig.saveBattleHandler(this);

        start();
    }

    private void giveRewards(Round round) {
        for (Map.Entry<Player, Location> activePlayer : arenaData.getActivePlayers().entrySet()) {
            if (!activePlayer.getKey().isDead()) {
                List<ItemStack> itemStacks = new LinkedList<ItemStack>();
                for (Reward reward : round.getRewards()) {
                    itemStacks.add(new ItemStack(reward.getId(), reward.getQuantity()));
                }
                activePlayer.getKey().getInventory().addItem(itemStacks.toArray(new ItemStack[itemStacks.size()]));
            }
        }
    }

    private void cleanupArena() {
        for (TimerTask timerTask : arenaData.getTasks().keySet()) {
            timerTask.cancel();
        }

        for (Entity entity : arenaData.getSpawnedMobs()) {
            if (entity.isValid()) {
                entity.remove();
            }
        }
        arenaData.setEndDate(null);
    }

    public void clearActivePlayersAndTeleportBack() {
        // add active and spectators to be ported back
        Map<Player, Location> playerLocationMap = new HashMap<Player, Location>(arenaData.getActivePlayers());
        playerLocationMap.putAll(arenaData.getOriginSpectatorLocations());

        arenaData.getActivePlayers().clear();
        arenaData.getOriginSpectatorLocations().clear();

        for (Map.Entry<Player, Location> playerLocationEntry : playerLocationMap.entrySet()) {
            playerLocationEntry.getKey().teleport(playerLocationEntry.getValue());
        }
    }

    public String getName() {
        return name;
    }

    public ArenaConfiguration getArenaConfiguration() {
        return arenaConfiguration;
    }

    public ArenaData getArenaData() {
        return arenaData;
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

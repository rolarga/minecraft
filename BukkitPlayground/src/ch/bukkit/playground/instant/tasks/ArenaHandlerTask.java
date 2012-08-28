package ch.bukkit.playground.instant.tasks;

import ch.bukkit.playground.instant.InstantConfig;
import ch.bukkit.playground.instant.model.ArenaConfiguration;
import ch.bukkit.playground.instant.model.ArenaData;
import ch.bukkit.playground.util.DateHelper;
import ch.bukkit.playground.util.LocationHelper;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.*;
import java.util.logging.Logger;

public class ArenaHandlerTask extends TimerTask {

    private ArenaConfiguration arenaConfiguration;
    private ArenaData arenaData;
    private String name;

    private Timer arenaTimer = new Timer();
    private static Logger logger = Logger.getLogger("ArenaHandlerTask");


    public ArenaHandlerTask(String name, ArenaConfiguration arenaConfiguration, ArenaData arenaData) {
        this.arenaConfiguration = arenaConfiguration;
        this.arenaData = arenaData;
        this.name = name;
    }

    public ArenaHandlerTask(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        if (arenaConfiguration.getEndDate() != null && new Date().getTime() < (arenaConfiguration.getEndDate().getTime() + 300000)) {
            logger.info("Cannot yet start an instant battle.");
            return;
        }

        logger.info("Starting instant battle.");

        Date tFirstMessage = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(arenaConfiguration.getOffset()));
        Date tSecondMessage = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(arenaConfiguration.getOffset()/2));
        Date t = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(arenaConfiguration.getDuration()));
        Date tPlus1Minute = new Date(System.currentTimeMillis() + DateHelper.getMillisForMinutes(arenaConfiguration.getDuration() + 1));

        new BroadcastTask(ChatColor.BLUE + "A new instant battle is open for registration. Press /instant join " + name + " to join the next battle. Next round at " + DateHelper.format(t)).run();

        arenaData.addTask(new BroadcastTask(arenaData.getRegisteredPlayers(), ChatColor.YELLOW + "A new instant battle starts in 5 minutes, %registered_players% players are registerd - Join now!"), tFirstMessage);
        arenaData.addTask(new BroadcastTask(arenaData.getRegisteredPlayers(),ChatColor.YELLOW + "A new instant battle starts in 1 minutes, %registered_players% players are registerd - Join now!."), tSecondMessage);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // get active players
                arenaData.getActivePlayers().clear();

                // move online players
                for (Player player : arenaData.getRegisteredPlayers()) {
                    if (player.isOnline()) {
                        Location loc = player.getLocation();
                        player.teleport(arenaConfiguration.getPosStart());
                        arenaData.addActivePlayer(player, loc);

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
                InstantConfig.saveArenaHandlerTask(ArenaHandlerTask.this);

                new BroadcastTask(arenaData.getActivePlayers().keySet(),ChatColor.YELLOW + "A new instant battle starts now with %players% players!").run();
                new MessageTask(arenaData.getActivePlayers().keySet(), ChatColor.RED + "Get ready for the fight, you have 1 minute to prepare yourself. There are %players% players with you fighting - enjoy!").run();
            }
        };
        arenaData.addTask(timerTask, t);

        // first round takes 1/6 for 10 times
        long millisDurationRoundType1 = Math.max(DateHelper.getMillisForMinutes(arenaConfiguration.getDuration()) * 2 / 7, 1);
        // first round takes 2/6 for 10 times
        long millisDurationRoundType2 = Math.max(DateHelper.getMillisForMinutes(arenaConfiguration.getDuration()) * 4 / 7, 1);
        // first round takes 3/6 for 10 times
        long millisDurationRoundType3 = Math.max(DateHelper.getMillisForMinutes(arenaConfiguration.getDuration())     / 7, 1);

        Date time = tPlus1Minute;
        for (int i = 1; i <= 10; i++) {
            arenaData.addTask(new SpawnTask(arenaConfiguration, arenaData, Zombie.class, i), time);
            arenaData.addTask(new SpawnTask(arenaConfiguration, arenaData, Golem.class,  i), time);
            arenaData.addTask(new SpawnTask(arenaConfiguration, arenaData, Spider.class, i), time);
            time = new Date(time.getTime() + millisDurationRoundType1);
        }

        for (int i = 1; i <= 10; i++) {
            arenaData.addTask(new SpawnTask(arenaConfiguration, arenaData, CaveSpider.class, i), time);
            arenaData.addTask(new SpawnTask(arenaConfiguration, arenaData, Blaze.class,  i), time);
            arenaData.addTask(new SpawnTask(arenaConfiguration, arenaData, MagmaCube.class, i), time);
            time = new Date(time.getTime() + millisDurationRoundType2);
        }

        arenaData.addTask(new SpawnTask(arenaConfiguration, arenaData, Giant.class,  1), time);
        arenaData.addTask(new SpawnTask(arenaConfiguration, arenaData, Ghast.class, 10), time);
        arenaData.addTask(new MessageTask(arenaData.getActivePlayers().keySet(), ChatColor.YELLOW + "You reached the bossfight - seems you guys are pretty good, arent you? have fun!"), time);
        time = new Date(time.getTime() + millisDurationRoundType3);
        arenaConfiguration.setEndDate(time);

        TimerTask timerEndTask = new TimerTask() {
            @Override
            public void run() {
                cleanup();
            }
        };
        // cleanup at the end
        arenaData.addTask(timerEndTask, arenaConfiguration.getEndDate());

        for (Map.Entry<TimerTask, Date> timerTaskDateEntry : arenaData.getTasks().entrySet()) {
            arenaTimer.schedule(timerTaskDateEntry.getKey(), timerTaskDateEntry.getValue());
        }
        InstantConfig.saveArenaHandlerTask(this);
    }

    @Override
    public boolean cancel() {
        arenaTimer.cancel();
        cleanupArena();

        arenaData.getRegisteredPlayers().clear();
        clearActivePlayersAndTeleportBack();

        logger.info("Timers are stopped, battle is over.");

        InstantConfig.saveArenaHandlerTask(this);

        return super.cancel();
    }

    // distribute rewards
    // delete spawned mobs
    // delete dropped items
    // delete dropped exp
    public void cleanup() {
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

        InstantConfig.saveArenaHandlerTask(this);
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
        arenaConfiguration.setEndDate(null);
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
        if (!(o instanceof ArenaHandlerTask)) return false;

        ArenaHandlerTask that = (ArenaHandlerTask) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}

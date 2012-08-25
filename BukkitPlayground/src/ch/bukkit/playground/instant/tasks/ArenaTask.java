package ch.bukkit.playground.instant.tasks;

import ch.bukkit.playground.instant.arena.Arena;
import ch.bukkit.playground.util.DateFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.*;
import java.util.logging.Logger;

public class ArenaTask extends TimerTask {

    private Arena arena;
    private Timer arenaTimer = new Timer();
    private static Logger logger = Logger.getLogger("ArenaTask");
    private Map<TimerTask, Date> tasks = new HashMap<TimerTask, Date>();

    public ArenaTask(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        if (arena.getEndDate() != null && new Date().getTime() < (arena.getEndDate().getTime() + 300000)) {
            logger.info("Cannot yet start an instant battle.");
            return;
        }

        logger.info("Starting instant battle.");

        Date tMinus5 = new Date(System.currentTimeMillis() + (arena.getTime() - 5) * 60 * 1000);
        Date tMinus1 = new Date(System.currentTimeMillis() + (arena.getTime() - 1) * 60 * 1000);
        Date t = new Date(System.currentTimeMillis() + (arena.getTime()) * 60 * 1000);

        Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "A new instant battle is open for registration. Press /instant join " + arena.getName() + " to join the next battle. Next round at " + DateFormatter.format(t));

        tasks.put(new AllMessageTask(arena, ChatColor.YELLOW + "A new instant battle starts in 5 minutes, %registered_players% players are registerd - Join now!"), tMinus5);
        tasks.put(new AllMessageTask(arena, ChatColor.YELLOW + "A new instant battle starts in 1 minutes, %registered_players% players are registerd - Join now!."), tMinus1);
        tasks.put(new AllMessageTask(arena, ChatColor.YELLOW + "A new instant battle starts now with %active_players% players!"), t);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // get active players
                arena.getActivePlayers().clear();

                // move online players
                for (Player player : arena.getRegisteredPlayers().values()) {
                    if (player.isOnline()) {
                        Location loc = player.getLocation();
                        player.teleport(arena.getPosStart());
                        arena.addActivePlayer(player, loc);

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
                arena.getRegisteredPlayers().clear();
            }
        };
        // make sure it appears after the message is sent
        tasks.put(timerTask, t);
        tasks.put(new MessageTask(arena.getActivePlayers().keySet(), ChatColor.RED + "Get ready for the fight, you have 1 minute to prepare yourself. There are %players% players with you fighting - enjoy!"), t);

        int timeshift = 0;
        for (int i = 1; i <= 21; i++) {
            if (i <= 10) {
                timeshift += 60000;

                Date time = new Date(t.getTime() + timeshift);
                tasks.put(new SpawnTask(arena, Zombie.class, i), time);
                tasks.put(new SpawnTask(arena, Creeper.class, i), time);
                tasks.put(new SpawnTask(arena, Spider.class, i), time);

                tasks.put(new MessageTask(arena.getActivePlayers().keySet(), ChatColor.YELLOW + "You got 1 minute to kill all mobs and get ready for the next wave - have fun!"), time);
            } else if (i <= 20) {
                timeshift += 120000;

                Date time = new Date(t.getTime() + timeshift);
                tasks.put(new SpawnTask(arena, CaveSpider.class, i), time);
                tasks.put(new SpawnTask(arena, Blaze.class, i), time);
                tasks.put(new SpawnTask(arena, MagmaCube.class, i), time);

                tasks.put(new MessageTask(arena.getActivePlayers().keySet(), ChatColor.YELLOW + "You got 2 minute to kill all mobs and get ready for the next wave - have fun!"), time);
            } else {
                timeshift += 300000;

                Date time = new Date(t.getTime() + timeshift);
                tasks.put(new SpawnTask(arena, Giant.class, 3), time);
                tasks.put(new SpawnTask(arena, Ghast.class, i), time);

                tasks.put(new MessageTask(arena.getActivePlayers().keySet(), ChatColor.YELLOW + "You reached the bossfight - seems you guys are pretty good, arent you? have fun!"), time);
                arena.setEndDate(time);
            }
        }

        TimerTask timerEndTask = new TimerTask() {
            @Override
            public void run() {
                cleanup();
            }
        };
        // cleanup at the end
        tasks.put(timerEndTask, arena.getEndDate());

        for (Map.Entry<TimerTask, Date> timerTaskDateEntry : tasks.entrySet()) {
            arenaTimer.schedule(timerTaskDateEntry.getKey(), timerTaskDateEntry.getValue());
        }
    }

    @Override
    public boolean cancel() {
        arenaTimer.cancel();
        cleanupArena();

        arena.getRegisteredPlayers().clear();
        clearActivePlayersAndTeleportBack();

        logger.info("Timers are stopped, battle is over.");

        return super.cancel();
    }

    // distribute rewards
    // delete spawned mobs
    // delete dropped items
    // delete dropped exp
    public void cleanup() {
        boolean allMobsDead = true;

        for (Entity entity : arena.getSpawnedMobs()) {
            if (entity.isValid() && !entity.isDead()) {
                allMobsDead = false;
            }
        }

        // we found a mob which is not dead, so the players didnt win the battle
        if (allMobsDead) {
            for (Player player : arena.getActivePlayers().keySet()) {
                if (!player.isDead()) {
                    player.sendMessage(ChatColor.GOLD + "You are one of the winners, congratulations!");
                } else {
                    player.sendMessage(ChatColor.GRAY + "Don't try to cheat - LOOSER!");
                }
            }
        }

        // teleport them back
        clearActivePlayersAndTeleportBack();

        // clear arena
        for (Entity entity : arena.getPos1().getWorld().getEntities()) {
            if ((entity.getType() == EntityType.DROPPED_ITEM || entity.getType() == EntityType.EXPERIENCE_ORB) && arena.isInArena(entity.getLocation())) {
                entity.remove();
            }
        }

        cleanupArena();
    }

    private void cleanupArena() {
        for (TimerTask timerTask : tasks.keySet()) {
            timerTask.cancel();
        }

        for (Entity entity : arena.getSpawnedMobs()) {
            if (entity.isValid()) {
                entity.remove();
            }
        }
        arena.setEndDate(null);
    }

    public void clearActivePlayersAndTeleportBack() {
        // add active and spectators to be ported back
        Map<Player, Location> playerLocationMap = new HashMap<Player, Location>(arena.getActivePlayers());
        playerLocationMap.putAll(arena.getSpectators());

        arena.getActivePlayers().clear();
        arena.getSpectators().clear();

        for (Map.Entry<Player, Location> playerLocationEntry : playerLocationMap.entrySet()) {
            playerLocationEntry.getKey().teleport(playerLocationEntry.getValue());
        }
    }

    public Arena getArena() {
        return arena;
    }
}

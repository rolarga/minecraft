package ch.bukkit.playground.instant.tasks;

import ch.bukkit.playground.instant.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ArenaTask extends TimerTask {

    private Arena arena;
    private Timer arenaTimer = new Timer();

    public ArenaTask(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void run() {
        if(new Date().getTime() < (arena.getEndDate().getTime() + 300000)) {
            return;
        }

        Bukkit.getServer().broadcastMessage("A new instant battle is open for registration. Press /instant join " + arena.getName() + " to join the next battle. Next round at " + arena.getTimeString());

        Date tMinus5 = new Date(System.currentTimeMillis() + (arena.getTime() - 5) * 60 * 1000);
        Date tMinus1 = new Date(System.currentTimeMillis() + (arena.getTime() - 1) * 60 * 1000);
        Date t = new Date(System.currentTimeMillis() + (arena.getTime()) * 60 * 1000);
        Date tEnd = new Date(System.currentTimeMillis() + (arena.getTime() + 36) * 60 * 1000);

        arenaTimer.schedule(new MessageTask(arena.getPos1().getWorld().getPlayers(), "A new instant battle starts in 5 minutes, %players% players are registerd - Join now!"), tMinus5);
        arenaTimer.schedule(new MessageTask(arena.getPos1().getWorld().getPlayers(), "A new instant battle starts in 1 minutes, %players% players are registerd - Join now!."), tMinus1);
        arenaTimer.schedule(new MessageTask(arena.getPos1().getWorld().getPlayers(), "A new instant battle starts now with %players% players!"), t);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // get active players
                arena.getActivePlayers().clear();

                // move online players
                for (Player player : arena.getRegisteredPlayers().values()) {
                    if(player.isOnline()) {
                        arena.addActivePlayer(player);
                        player.teleport(arena.getPosStart());
                    } else {
                        player.sendMessage("You were not added to the instant battle as you were offline at that time.");
                    }
                }
                arena.getRegisteredPlayers().clear();
            }
        };
        arenaTimer.schedule(timerTask, t);
        arenaTimer.schedule(new MessageTask(arena.getActivePlayers().keySet(), "Get ready for the fight, you have 1 minute to prepare yourself. There are %players% players with you fighting - enjoy!"), t);

        int timeshift = 0;
        for(int i = 1; i <= 21; i++) {
            if(i <= 10) {
                timeshift += 60000;

                Date time = new Date(t.getTime() + timeshift);
                arenaTimer.schedule(new SpawnTask(arena, Zombie.class, i), time);
                arenaTimer.schedule(new SpawnTask(arena, Creeper.class, i), time);
                arenaTimer.schedule(new SpawnTask(arena, Spider.class, i), time);

                arenaTimer.schedule(new MessageTask(arena.getActivePlayers().keySet(), "You got 1 minute to kill all mobs and get ready for the next wave - have fun!"), time);
            } else if(i <= 20) {
                timeshift += 120000;

                Date time = new Date(t.getTime() + timeshift);
                arenaTimer.schedule(new SpawnTask(arena, CaveSpider.class, i), time);
                arenaTimer.schedule(new SpawnTask(arena, Blaze.class, i), time);
                arenaTimer.schedule(new SpawnTask(arena, MagmaCube.class, i), time);

                arenaTimer.schedule(new MessageTask(arena.getActivePlayers().keySet(), "You got 2 minute to kill all mobs and get ready for the next wave - have fun!"), time);
            } else {
                timeshift += 300000;

                Date time = new Date(t.getTime() + timeshift);
                arenaTimer.schedule(new SpawnTask(arena, Giant.class, 3), time);
                arenaTimer.schedule(new SpawnTask(arena, Ghast.class, i), time);

                arenaTimer.schedule(new MessageTask(arena.getActivePlayers().keySet(), "you reached the bossfight - seems you guys are pretty good, arent you? have fun!"), time);
                arena.setEndDate(time);
            }
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                for (Player player : arena.getActivePlayers().keySet()) {
                    if(!player.isDead()) {
                        player.sendMessage("You are one of the winners, congratulations!");
                    }
                }
                arena.getActivePlayers().clear();

            }
        };
        arenaTimer.schedule(timerTask, tEnd);
    }

    public void stop() {
        arenaTimer.cancel();
        for (Entity entity : arena.getSpawnedMobs()) {
            if(entity.isValid()) {
                entity.remove();
            }
        }
        arena.getActivePlayers().clear();
        arena.getRegisteredPlayers().clear();
    }
}

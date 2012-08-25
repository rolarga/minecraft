package ch.bukkit.playground.instant;

import ch.bukkit.playground.instant.arena.Arena;
import ch.bukkit.playground.instant.listener.ArenaListener;
import ch.bukkit.playground.instant.tasks.ArenaTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Date;
import java.util.Timer;

public class InstantHandler {

    Arena arena = new Arena();
    Timer timer = new Timer("arenas");
    ArenaTask arenaTask;

    public InstantHandler() {
        new ArenaListener(arena);

        FileConfiguration config = YamlConfiguration.loadConfiguration(new File("./plugins/BukkitPlayground/arenas.yml"));
        config.get("arenas", Arena.class);
    }

    public void registerPlayer(Player player) {
        if(arena.addRegisteredPlayer(player)) {
            player.sendMessage("You joined the instant.");
        } else {
            player.sendMessage("You are blocked for this action.");
        }
    }

    public void unregisterPlayer(Player player) {
        if(arena.unregisterPlayer(player) != null) {
            player.sendMessage("You leaved the instant.");
        } else {
            player.sendMessage("You were not registered.");
        }
    }

    public void handleArena(String name, String arg1, String arg2, Player player) {
        if("pos1".equals(arg1)) arena.setPos1(player.getLocation());
        if("pos2".equals(arg1)) arena.setPos2(player.getLocation());
        if("posstart".equals(arg1)) arena.setPosStart(player.getLocation());
        if("posspec".equals(arg1)) arena.setPosSpectator(player.getLocation());
        if("height".equals(arg1)) arena.setHeight(Integer.parseInt(arg2));

        if("starttimer".equals(arg1)) {
            arena.setTime(Integer.parseInt(arg2));
            arena.setDate(new Date(System.currentTimeMillis() + Integer.parseInt(arg2)));
            arenaTask = new ArenaTask(arena);
            int timeInMillis = (1 + arena.getTime()) * 60 * 1000;
            timer.schedule(arenaTask, 0, timeInMillis);
        }
        if("forcestop".equals(arg1)) {
            arenaTask.stop();
        }

        player.sendMessage("Arena " + name + " got command: " + arg1 + " with argument " + arg2 + " from player " + player.getName());
    }
}

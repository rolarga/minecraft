package ch.bukkit.playground.instant;

import ch.bukkit.playground.instant.arena.Arena;
import ch.bukkit.playground.instant.listener.ArenaListener;
import ch.bukkit.playground.instant.tasks.ArenaTask;
import ch.bukkit.playground.util.DateFormatter;
import com.mysql.jdbc.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.logging.Logger;

public class InstantHandler {

    Arena arena = new Arena();
    Timer timer = new Timer("arenas");
    private static Logger logger = Logger.getLogger("InstantHandler");
    ArenaTask arenaTask;

    public InstantHandler() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File("./plugins/BukkitPlayground/arenas.yml"));
    }

    public void registerPlayer(Player player) {
        if(arena.addRegisteredPlayer(player)) {
            player.sendMessage(ChatColor.GREEN + "You joined the instant.");
        } else {
            player.sendMessage(ChatColor.RED + "You are blocked for this action.");
        }
    }

    public void unregisterPlayer(Player player) {
        if(arena.unregisterPlayer(player) != null) {
            player.sendMessage(ChatColor.GREEN + "You leaved the instant.");
        } else {
            player.sendMessage(ChatColor.RED + "You were not registered.");
        }
    }

    public void specJoin(Player player) {
        arena.addSpecator(player, player.getLocation());
        player.teleport(arena.getPosSpectator());
        player.sendMessage(ChatColor.GREEN + "You joined the spectator lounge.");
    }

    public void specLeave(Player player) {
        Location loc = arena.getSpectators().remove(player);
        if(loc != null) {
            player.teleport(arena.getPosSpectator());
            player.sendMessage(ChatColor.GREEN + "You left the spectator lounge.");
        } else {
            player.sendMessage(ChatColor.RED + "You were not a spectator.");
        }
    }

    public void handleArena(String name, String arg1, String arg2, Player player) {
        arena.setName(name);
        if("pos1".equals(arg1)) {
            arena.setPos1(player.getLocation());
            logger.info("Location1: " + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ());
        }
        if("pos2".equals(arg1)) {
            arena.setPos2(player.getLocation());
            logger.info("Location2: " + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ());
        }
        if("posstart".equals(arg1)) arena.setPosStart(player.getLocation());
        if("posspec".equals(arg1)) arena.setPosSpectator(player.getLocation());

        if("starttimer".equals(arg1)) {
            if(StringUtils.isNullOrEmpty(arg2)) {
                player.sendMessage(ChatColor.YELLOW + "Please specify a number when the game should start: '/instantop starttime 6' will start the game in 6 minutes");
            }
            startArena(arg2);
        }

        if("forcestop".equals(arg1) && arenaTask != null) {
            arenaTask.cancel();
        }

        if("restart".equals(arg1) && arenaTask != null) {
            arenaTask.cancel();
            startArena(arg2);
        }

        if("stat".equals(arg1)) {
            player.sendMessage("Name: " + arena.getName());
            for (String s : arena.getRegisteredPlayers().keySet()) {
                player.sendMessage("Registered Player: " + s);
            }
            for (Player p : arena.getActivePlayers().keySet()) {
                player.sendMessage("Active Player: " + p.getName());
            }
            player.sendMessage("End Date: " + DateFormatter.format(arena.getEndDate()));
            player.sendMessage("Earliest Next round: " + DateFormatter.format(new Date(arena.getEndDate().getTime() + 300000)));
        }

        if("kick".equals(arg1)) {
            Player target = Bukkit.getPlayer(arg2);
            if(target != null) {
                arena.unregisterPlayer(target);
                Location loc = arena.getActivePlayers().remove(target);
                if(loc != null) {
                    target.teleport(loc);
                }
                player.sendMessage(ChatColor.GRAY + "Kicked player " + target.getName());
                target.sendMessage(ChatColor.RED + "You were kicked from current instant battle!");
            }
        }

        if("ban".equals(arg1)) {
            Player target = Bukkit.getPlayer(arg2);
            if(target != null) {
                arena.addBlockedPlayer(arg2, "Banned by admin/mod: " + player.getName());
                arena.unregisterPlayer(target);
                Location loc = arena.getActivePlayers().remove(target);
                if(loc != null) {
                    target.teleport(loc);
                }
                player.sendMessage(ChatColor.GRAY + "Blocked player " + target.getName());
                target.sendMessage(ChatColor.RED + "You are blocked now for any further instant battles!");
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "Player '" + target.getName() + "' was blocked for any further instant battle by administrator.");
            }
        }

        if("addspawn".equals(arg1)) {
            arena.addSpawn(player.getLocation());
        }

        if("clearspawn".equals(arg1)) {
            arena.getSpanws().clear();
        }

        player.sendMessage(ChatColor.YELLOW + name + " command " + arg1 + (StringUtils.isNullOrEmpty(arg2) ? "" : " argument " + arg2) + " player " + player.getName());
    }

    private void startArena(String arg2) {
        arena.setTime(Integer.parseInt(arg2));
        arenaTask = new ArenaTask(arena);
        int timeInMillis = (1 + arena.getTime()) * 60 * 1000;
        timer.schedule(arenaTask, 0, timeInMillis);
        new ArenaListener(arenaTask);
    }

    public void disable() throws IOException {
        if(arenaTask != null) {
            arenaTask.cancel();
        }

        File file = new File("./plugins/BukkitPlayground/arenas.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Arena> arenas = new LinkedList<Arena>();
        arenas.add(arena);
        arenas.add(new Arena(arena));
        config.set("arenas", arenas);

        config.save(file);
    }
}

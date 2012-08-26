package ch.bukkit.playground.instant;

import ch.bukkit.playground.instant.arena.Arena;
import ch.bukkit.playground.instant.config.InstantConfigHandler;
import ch.bukkit.playground.instant.listener.ArenaListener;
import ch.bukkit.playground.instant.tasks.ArenaTask;
import ch.bukkit.playground.util.DateFormatter;
import ch.bukkit.playground.util.Msg;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.logging.Logger;

public class InstantHandler {

    Arena arena = new Arena();
    Timer timer = new Timer("arenas");
    private static Logger logger = Logger.getLogger("InstantHandler");
    ArenaTask arenaTask;
    ArenaListener arenaListener = new ArenaListener();

    public InstantHandler() {
        List<Arena> arenas = InstantConfigHandler.loadAllArenas();
        if(CollectionUtils.isNotEmpty(arenas)) {
            arena = arenas.get(0);
        }
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

    public void handlePlayerCommands(String name, String arg1, String arg2, Player player) {
        String playerName = player != null ? player.getName() : "CONSOLE";
        arena.setName(name);

        if(player != null && "pos1".equals(arg1)) {
            arena.setPos1(player.getLocation());
            logger.info("Location1: " + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ());
        }
        if(player != null &&"pos2".equals(arg1)) {
            arena.setPos2(player.getLocation());
            logger.info("Location2: " + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ());
        }
        if(player != null &&"posstart".equals(arg1)) arena.setPosStart(player.getLocation());
        if(player != null &&"posspec".equals(arg1)) arena.setPosSpectator(player.getLocation());

        if("starttimer".equals(arg1)) {
            if(StringUtils.isNullOrEmpty(arg2)) {
                Msg.sendMsg(player, ChatColor.YELLOW + "Please specify a number when the game should start: '/instantop starttime 6' will start the game in 6 minutes");
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
            Msg.sendMsg(player, "Name: " + arena.getName());
            for (String s : arena.getRegisteredPlayers().keySet()) {
                Msg.sendMsg(player, "Registered Player: " + s);
            }
            for (Player p : arena.getActivePlayers().keySet()) {
                Msg.sendMsg(player, "Active Player: " + p.getName());
            }
            Msg.sendMsg(player, "End Date: " + DateFormatter.format(arena.getEndDate()));
            if(arena.getEndDate() != null) {
                Msg.sendMsg(player, "Earliest Next round: " + DateFormatter.format(new Date(arena.getEndDate().getTime() + 300000)));
            }
        }

        if("kick".equals(arg1)) {
            Player target = Bukkit.getPlayer(arg2);
            if(target != null) {
                arena.unregisterPlayer(target);
                Location loc = arena.getActivePlayers().remove(target);
                if(loc != null) {
                    target.teleport(loc);
                }
                Msg.sendMsg(player, ChatColor.GRAY + "Kicked player " + target.getName());
                Msg.sendMsg(player, ChatColor.RED + "You were kicked from current instant battle!");
            }
        }

        if("ban".equals(arg1)) {
            Player target = Bukkit.getPlayer(arg2);
            if(target != null) {
                arena.addBlockedPlayer(arg2, "Banned by admin/mod: " + playerName);
                arena.unregisterPlayer(target);
                Location loc = arena.getActivePlayers().remove(target);
                if(loc != null) {
                    target.teleport(loc);
                }
                Msg.sendMsg(player, ChatColor.GRAY + "Blocked player " + target.getName());
                target.sendMessage(ChatColor.RED + "You are blocked now for any further instant battles!");
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "Player '" + target.getName() + "' was blocked for any further instant battle by administrator.");
            }
        }

        if(player != null &&"addspawn".equals(arg1)) {
            arena.addSpawn(player.getLocation());
        }

        if("clearspawn".equals(arg1)) {
            arena.getSpanws().clear();
        }

        InstantConfigHandler.saveArena(arena);
        Msg.sendMsg(player, ChatColor.YELLOW + name + " command " + arg1 + (StringUtils.isNullOrEmpty(arg2) ? "" : " argument " + arg2) + " player " + playerName);
    }

    private void startArena(String arg2) {
        arena.setTime(Integer.parseInt(arg2));
        arenaTask = new ArenaTask(arena);
        int timeInMillis = (1 + arena.getTime()) * 60 * 1000;
        timer.schedule(arenaTask, 0, timeInMillis);
        arenaListener.initialize(arenaTask);
        InstantConfigHandler.saveArena(arena);
    }

    public void disable() throws IOException {
        if(arenaTask != null) {
            arenaTask.cancel();
        }

        InstantConfigHandler.saveArena(arena);
    }
}

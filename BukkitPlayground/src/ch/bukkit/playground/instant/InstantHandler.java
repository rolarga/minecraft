package ch.bukkit.playground.instant;

import ch.bukkit.playground.instant.eventhandlers.*;
import ch.bukkit.playground.instant.model.ArenaConfiguration;
import ch.bukkit.playground.instant.model.ArenaData;
import ch.bukkit.playground.instant.tasks.ArenaHandlerTask;
import ch.bukkit.playground.util.DateHelper;
import ch.bukkit.playground.util.Msg;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.logging.Logger;

public class InstantHandler {

    private static Logger logger = Logger.getLogger("InstantHandler");

    Timer timer = new Timer("arenas");
    HashMap<String, ArenaHandlerTask> arenaHandlerTasks;
    InstantListener instantListener;
    Map<Class<? extends PlayerEvent>, PlayerEventHandler> playerEventHandlers = new HashMap<Class<? extends PlayerEvent>, PlayerEventHandler>();
    Map<Class<? extends EntityEvent>, EntityEventHandler> entityEventHandlers = new HashMap<Class<? extends EntityEvent>, EntityEventHandler>();

    public InstantHandler() {
        instantListener = new InstantListener(this);

        // Load arena handlers
        arenaHandlerTasks = InstantConfig.loadAllArenaHandlerTasks();

        // Initialize player event handlers
        playerEventHandlers.put(PlayerMoveEvent.class, new PlayerMoveEventHandler());
        playerEventHandlers.put(PlayerQuitEvent.class, new PlayerQuitEventHandler());
        playerEventHandlers.put(PlayerRespawnEvent.class, new PlayerRespawnEventHandler());
        playerEventHandlers.put(PlayerTeleportEvent.class, new PlayerTeleportEventHandler());
        playerEventHandlers.put(PlayerToggleFlightEvent.class, new PlayerToggleFlightEventHandler());

        // Initialize entity event handlers
        entityEventHandlers.put(EntityDeathEvent.class, new EntityDeathEventHandler());
    }

    public void handlePlayerCommands(String name, String arg1, Player player) {
        if ("list".equals(arg1)) {
            Msg.sendMsg(player, ChatColor.YELLOW + "Available Arenas: " + getArenaNames());
            return;
        }

        ArenaHandlerTask arenaHandlerTask = arenaHandlerTasks.get(name);

        if (arenaHandlerTask == null) {
            String arenaNames = getArenaNames();
            Msg.sendMsg(player, ChatColor.RED + "Arena was not found. Available arenas are: " + arenaNames);
            return;
        }

        if ("join".equals(arg1)) {
            if (arenaHandlerTask.getArenaData().addRegisteredPlayer(player)) {
                Msg.sendMsg(player, ChatColor.GREEN + "You joined the instant registration list for arena: " + arenaHandlerTask.getName() + ".");
            } else {
                Msg.sendMsg(player, ChatColor.RED + "You are blocked for this action.");
            }
        } else if ("leave".equals(arg1)) {
            if (arenaHandlerTask.getArenaData().unregisterPlayer(player) != null) {
                Msg.sendMsg(player, ChatColor.GREEN + "You leaved the instant.");
            } else {
                Msg.sendMsg(player, ChatColor.RED + "You were not registered.");
            }
        } else if ("spec".equals(arg1)) {
            arenaHandlerTask.getArenaData().addSpecator(player, player.getLocation());
            player.teleport(arenaHandlerTask.getArenaConfiguration().getPosSpectator());
            Msg.sendMsg(player, ChatColor.GREEN + "You joined the spectator lounge of arena: " + arenaHandlerTask.getName() + ".");
        } else if ("unspec".equals(arg1)) {
            Location loc = arenaHandlerTask.getArenaData().getOriginSpectatorLocations().remove(player);
            if (loc != null) {
                player.teleport(arenaHandlerTask.getArenaConfiguration().getPosSpectator());
                Msg.sendMsg(player, ChatColor.GREEN + "You left the spectator lounge of arena " + arenaHandlerTask.getName() + ".");
            } else {
                Msg.sendMsg(player, ChatColor.RED + "You were not a spectator.");
            }
        }
    }

    private String getArenaNames() {
        String arenaNames = "";
        for (String s : arenaHandlerTasks.keySet()) {
            if (!StringUtils.isNullOrEmpty(arenaNames)) {
                arenaNames += ", ";
            }
            arenaNames += s;
        }
        return arenaNames;
    }

    public void handleOpCommands(String arenaName, String arg1, String arg2, Player player) {
        String playerName = player != null ? player.getName() : "CONSOLE";

        ArenaHandlerTask arenaHandlerTask = arenaHandlerTasks.get(arenaName);
        // if the arena doesnt exist, create it
        if (arenaHandlerTask == null) {
            arenaHandlerTask = new ArenaHandlerTask(arenaName, new ArenaConfiguration(), new ArenaData());
            arenaHandlerTasks.put(arenaName, arenaHandlerTask);
        }

        if (player != null && "pos1".equals(arg1))
            arenaHandlerTask.getArenaConfiguration().setPos1(player.getLocation());
        if (player != null && "pos2".equals(arg1))
            arenaHandlerTask.getArenaConfiguration().setPos2(player.getLocation());
        if (player != null && "posstart".equals(arg1))
            arenaHandlerTask.getArenaConfiguration().setPosStart(player.getLocation());
        if (player != null && "posspec".equals(arg1))
            arenaHandlerTask.getArenaConfiguration().setPosSpectator(player.getLocation());

        if ("offset".equals(arg1)) {
            if (StringUtils.isNullOrEmpty(arg2) && !NumberUtils.isDigits(arg2)) {
                Msg.sendMsg(player, ChatColor.YELLOW + "Please specify a number when the battle notifications should be sent out: '/instantop offset 5' will send first message 5 minutes before the battle starts.");
            }
            arenaHandlerTask.getArenaConfiguration().setOffset(Integer.parseInt(arg2));
        }

        if ("duration".equals(arg1)) {
            if (StringUtils.isNullOrEmpty(arg2) && !NumberUtils.isDigits(arg2)) {
                Msg.sendMsg(player, ChatColor.YELLOW + "Please specify a number how long the battle should last: '/instantop duration 30' will let the battle be 30 minutes long.");
            }
            arenaHandlerTask.getArenaConfiguration().setDuration(Integer.parseInt(arg2));
        }

        if ("stop".equals(arg1)) {
            arenaHandlerTask.cancel();
        }

        if ("start".equals(arg1)) {
            // make sure the arena is not already running
            arenaHandlerTask.cancel();
            // start the arena
            timer.schedule(arenaHandlerTask, 0, DateHelper.getMillisForMinutes(arenaHandlerTask.getArenaConfiguration().getCompleteRunDuration()));
            InstantConfig.saveArenaHandlerTask(arenaHandlerTask);
        }

        if ("stat".equals(arg1)) {
            Msg.sendMsg(player, "Name: " + arenaName);
            for (Player p : arenaHandlerTask.getArenaData().getRegisteredPlayers()) {
                Msg.sendMsg(player, "Registered Player: " + p.getName());
            }
            for (Player p : arenaHandlerTask.getArenaData().getActivePlayers().keySet()) {
                Msg.sendMsg(player, "Active Player: " + p.getName());
            }
            Msg.sendMsg(player, "End Date: " + DateHelper.format(arenaHandlerTask.getArenaConfiguration().getEndDate()));
            if (arenaHandlerTask.getArenaConfiguration().getEndDate() != null) {
                Msg.sendMsg(player, "Earliest Next round: " + DateHelper.format(new Date(arenaHandlerTask.getArenaConfiguration().getEndDate().getTime() + 300000)));
            }
        }

        if ("kick".equals(arg1)) {
            Player target = Bukkit.getPlayer(arg2);
            if (target != null) {
                arenaHandlerTask.getArenaData().unregisterPlayer(target);
                Location loc = arenaHandlerTask.getArenaData().getActivePlayers().remove(target);
                if (loc != null) {
                    target.teleport(loc);
                }
                Msg.sendMsg(player, ChatColor.GRAY + "Kicked player " + target.getName());
                Msg.sendMsg(player, ChatColor.RED + "You were kicked from current instant battle!");
            }
        }

        if ("ban".equals(arg1)) {
            Player target = Bukkit.getPlayer(arg2);
            if (target != null) {
                arenaHandlerTask.getArenaData().addBlockedPlayer(arg2, "Banned by admin/mod: " + playerName);
                arenaHandlerTask.getArenaData().unregisterPlayer(target);
                Location loc = arenaHandlerTask.getArenaData().getActivePlayers().remove(target);
                if (loc != null) {
                    target.teleport(loc);
                }
                Msg.sendMsg(player, ChatColor.GRAY + "Blocked player " + target.getName());
                target.sendMessage(ChatColor.RED + "You are blocked now for any further instant battles!");
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "Player '" + target.getName() + "' was blocked for any further instant battle by administrator.");
            }
        }

        if (player != null && "addspawn".equals(arg1)) {
            arenaHandlerTask.getArenaConfiguration().addSpawn(player.getLocation());
        }

        if ("clearspawn".equals(arg1)) {
            arenaHandlerTask.getArenaConfiguration().getSpanws().clear();
        }

        InstantConfig.saveArenaHandlerTask(arenaHandlerTask);
        Msg.sendMsg(player, ChatColor.YELLOW + "Arena '" + arenaName + "' command " + arg1 + (StringUtils.isNullOrEmpty(arg2) ? "" : " argument " + arg2) + " by " + playerName);
    }

    public void handlePlayerEvents(PlayerEvent playerEvent) {
        PlayerEventHandler eventHandler = playerEventHandlers.get(playerEvent.getClass());

        // can handle the event
        if (eventHandler != null) {
            for (ArenaHandlerTask arenaHandlerTask : arenaHandlerTasks.values()) {
                if (arenaHandlerTask.getArenaData().getActivePlayers().containsKey(playerEvent.getPlayer())) {
                    eventHandler.processEvent(playerEvent, arenaHandlerTask);
                }
            }
        }
    }

    public void handleEntityEvents(EntityEvent entityEvent) {
        EntityEventHandler eventHandler = entityEventHandlers.get(entityEvent.getClass());

        // can handle the event
        if (eventHandler != null && entityEvent.getEntity().getClass().isAssignableFrom(Player.class)) {
            Player player = (Player) entityEvent.getEntity();
            for (ArenaHandlerTask arenaHandlerTask : arenaHandlerTasks.values()) {
                if (arenaHandlerTask.getArenaData().getActivePlayers().containsKey(player)) {
                    eventHandler.processEvent(entityEvent, arenaHandlerTask, player);
                }
            }
        }

    }

    public void disable() throws IOException {
        for (ArenaHandlerTask arenaHandlerTask : arenaHandlerTasks.values()) {
            arenaHandlerTask.cancel();
            InstantConfig.saveArenaHandlerTask(arenaHandlerTask);
        }
    }
}

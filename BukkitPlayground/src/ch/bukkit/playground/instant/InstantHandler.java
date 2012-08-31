package ch.bukkit.playground.instant;

import ch.bukkit.playground.instant.eventhandlers.*;
import ch.bukkit.playground.instant.model.ArenaConfiguration;
import ch.bukkit.playground.instant.model.ArenaData;
import ch.bukkit.playground.util.DateHelper;
import ch.bukkit.playground.util.Msg;
import com.mysql.jdbc.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class InstantHandler {

    private static Logger logger = Logger.getLogger("InstantHandler");

    HashMap<String, BattleHandler> battleHandlers;
    InstantListener instantListener;
    Map<Class<? extends PlayerEvent>, PlayerEventHandler> playerEventHandlers = new HashMap<Class<? extends PlayerEvent>, PlayerEventHandler>();
    Map<Class<? extends EntityEvent>, EntityEventHandler> entityEventHandlers = new HashMap<Class<? extends EntityEvent>, EntityEventHandler>();

    public InstantHandler() {
        instantListener = new InstantListener(this);

        // Load arena handlers
        battleHandlers = InstantConfig.loadBattleHandlers();

        // Initialize player event handlers
        playerEventHandlers.put(PlayerMoveEvent.class, new PlayerMoveEventHandler());
        playerEventHandlers.put(PlayerQuitEvent.class, new PlayerQuitEventHandler());
        playerEventHandlers.put(PlayerRespawnEvent.class, new PlayerRespawnEventHandler());
        playerEventHandlers.put(PlayerTeleportEvent.class, new PlayerTeleportEventHandler());
        playerEventHandlers.put(PlayerToggleFlightEvent.class, new PlayerToggleFlightEventHandler());

        // Initialize entity event handlers
        entityEventHandlers.put(EntityDeathEvent.class, new EntityDeathEventHandler());
        entityEventHandlers.put(EntityDamageByEntityEvent.class, new EntityDamageByEntityEventHandler());
    }

    public void start() {
        // start all valid configured battles
        for (BattleHandler battleHandler : battleHandlers.values()) {
            if (battleHandler.getArenaConfiguration().isValid()) {
                battleHandler.start();
            }
        }
    }

    public void handlePlayerCommands(String name, String arg1, Player player) {
        if ("list".equals(arg1)) {
            Msg.sendMsg(player, ChatColor.YELLOW + "Available Arenas: " + getArenaNames());
            return;
        }

        BattleHandler battleHandler = battleHandlers.get(name);

        if (battleHandler == null) {
            String arenaNames = getArenaNames();
            Msg.sendMsg(player, ChatColor.RED + "Arena was not found. Available arenas are: " + arenaNames);
            return;
        }

        if ("join".equals(arg1)) {
            if (battleHandler.getArenaData().addRegisteredPlayer(player)) {
                Msg.sendMsg(player, ChatColor.GREEN + "You joined the instant registration list for arena: " + battleHandler.getName() + ".");
            } else {
                Msg.sendMsg(player, ChatColor.RED + "You are blocked for this action.");
            }
        } else if ("leave".equals(arg1)) {
            if (battleHandler.getArenaData().unregisterPlayer(player) != null) {
                Msg.sendMsg(player, ChatColor.GREEN + "You leaved the instant.");
            } else {
                Msg.sendMsg(player, ChatColor.RED + "You were not registered.");
            }
        } else if ("spec".equals(arg1)) {
            battleHandler.getArenaData().addSpecator(player, player.getLocation());
            player.teleport(battleHandler.getArenaConfiguration().getPosSpectator());
            Msg.sendMsg(player, ChatColor.GREEN + "You joined the spectator lounge of arena: " + battleHandler.getName() + ".");
        } else if ("unspec".equals(arg1)) {
            Location loc = battleHandler.getArenaData().getOriginSpectatorLocations().remove(player);
            if (loc != null) {
                player.teleport(battleHandler.getArenaConfiguration().getPosSpectator());
                Msg.sendMsg(player, ChatColor.GREEN + "You left the spectator lounge of arena " + battleHandler.getName() + ".");
            } else {
                Msg.sendMsg(player, ChatColor.RED + "You were not a spectator.");
            }
        }
    }

    public void handleOpCommands(String arenaName, String arg1, String arg2, Player player) {
        String playerName = player != null ? player.getName() : "CONSOLE";

        BattleHandler battleHandler = battleHandlers.get(arenaName);
        // if the arena doesnt exist, create it
        if (battleHandler == null) {
            battleHandler = new BattleHandler(arenaName, new ArenaConfiguration(), new ArenaData());
            battleHandlers.put(arenaName, battleHandler);
        }

        if (player != null && "pos1".equals(arg1))
            battleHandler.getArenaConfiguration().setPos1(player.getLocation());
        if (player != null && "pos2".equals(arg1))
            battleHandler.getArenaConfiguration().setPos2(player.getLocation());
        if (player != null && "posstart".equals(arg1))
            battleHandler.getArenaConfiguration().setPosStart(player.getLocation());
        if (player != null && "posspec".equals(arg1))
            battleHandler.getArenaConfiguration().setPosSpectator(player.getLocation());

        if ("offset".equals(arg1)) {
            if (StringUtils.isNullOrEmpty(arg2) && !NumberUtils.isDigits(arg2)) {
                Msg.sendMsg(player, ChatColor.YELLOW + "Please specify a number when the battle notifications should be sent out: '/instantop offset 5' will send first message 5 minutes before the battle starts.");
            }
            battleHandler.getArenaConfiguration().setOffset(Integer.parseInt(arg2));
        }

        if ("duration".equals(arg1)) {
            if (StringUtils.isNullOrEmpty(arg2) && !NumberUtils.isDigits(arg2)) {
                Msg.sendMsg(player, ChatColor.YELLOW + "Please specify a number how long the battle should last: '/instantop duration 30' will let the battle be 30 minutes long.");
            }
            battleHandler.getArenaConfiguration().setDuration(Integer.parseInt(arg2));
        }

        if ("stop".equals(arg1)) {
            battleHandler.stop();
        }

        if ("start".equals(arg1)) {
            // make sure the arena is not already running
            battleHandler.stop();
            battleHandler.start();
            // start the arena
            InstantConfig.saveBattleHandler(battleHandler);
        }

        if ("stat".equals(arg1)) {
            Msg.sendMsg(player, "Name: " + arenaName);
            for (Player p : battleHandler.getArenaData().getRegisteredPlayers()) {
                Msg.sendMsg(player, "Registered Player: " + p.getName());
            }
            for (Player p : battleHandler.getArenaData().getActivePlayers().keySet()) {
                Msg.sendMsg(player, "Active Player: " + p.getName());
            }
            Msg.sendMsg(player, "End Date: " + DateHelper.format(battleHandler.getArenaData().getEndDate()));
            if (battleHandler.getArenaData().getEndDate() != null) {
                Msg.sendMsg(player, "Earliest Next round: " + DateHelper.format(new Date(battleHandler.getArenaData().getEndDate().getTime() + 300000)));
            }
        }

        if ("kick".equals(arg1)) {
            Player target = Bukkit.getPlayer(arg2);
            if (target != null) {
                battleHandler.getArenaData().unregisterPlayer(target);
                Location loc = battleHandler.getArenaData().getActivePlayers().remove(target);
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
                battleHandler.getArenaData().addBlockedPlayer(arg2, "Banned by admin/mod: " + playerName);
                battleHandler.getArenaData().unregisterPlayer(target);
                Location loc = battleHandler.getArenaData().getActivePlayers().remove(target);
                if (loc != null) {
                    target.teleport(loc);
                }
                Msg.sendMsg(player, ChatColor.GRAY + "Blocked player " + target.getName());
                target.sendMessage(ChatColor.RED + "You are blocked now for any further instant battles!");
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "Player '" + target.getName() + "' was blocked for any further instant battle by administrator.");
            }
        }

        if (player != null && "addspawn".equals(arg1)) {
            battleHandler.getArenaConfiguration().addSpawn(player.getLocation());
        }

        if ("clearspawn".equals(arg1)) {
            battleHandler.getArenaConfiguration().getSpanws().clear();
        }

        InstantConfig.saveBattleHandler(battleHandler);
        Msg.sendMsg(player, ChatColor.YELLOW + "Arena '" + arenaName + "' command " + arg1 + (StringUtils.isNullOrEmpty(arg2) ? "" : " argument " + arg2) + " by " + playerName);
    }

    @SuppressWarnings("unchecked")
    public void handlePlayerEvents(PlayerEvent playerEvent) {
        PlayerEventHandler eventHandler = playerEventHandlers.get(playerEvent.getClass());

        // can handle the event
        if (eventHandler != null) {
            for (BattleHandler battleHandler : battleHandlers.values()) {
                if (battleHandler.getArenaData().getActivePlayers().containsKey(playerEvent.getPlayer())) {
                    eventHandler.processEvent(playerEvent, battleHandler);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void handleEntityEvents(EntityEvent entityEvent) {
        EntityEventHandler eventHandler = entityEventHandlers.get(entityEvent.getClass());

        // can handle the event
        if (eventHandler != null && entityEvent.getEntity().getClass().isAssignableFrom(Player.class)) {
            Player player = (Player) entityEvent.getEntity();
            for (BattleHandler battleHandler : battleHandlers.values()) {
                if (battleHandler.getArenaData().getActivePlayers().containsKey(player)) {
                    eventHandler.processEvent(entityEvent, battleHandler, player);
                }
            }
        }

    }

    public void disable() {
        for (BattleHandler battleHandler : battleHandlers.values()) {
            battleHandler.stop();
            InstantConfig.saveBattleHandler(battleHandler);
        }
    }

    // Helper methods

    private String getArenaNames() {
        String arenaNames = "";
        for (String s : battleHandlers.keySet()) {
            if (!StringUtils.isNullOrEmpty(arenaNames)) {
                arenaNames += ", ";
            }
            arenaNames += s;
        }
        return arenaNames;
    }
}

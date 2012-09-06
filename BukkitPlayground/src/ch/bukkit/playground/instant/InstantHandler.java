package ch.bukkit.playground.instant;

import ch.bukkit.playground.instant.eventhandlers.*;
import ch.bukkit.playground.instant.model.BattleConfiguration;
import ch.bukkit.playground.instant.model.BattleData;
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

    protected HashMap<String, BattleHandler> battleHandlers;
    protected InstantListener instantListener;
    protected Map<Class<? extends PlayerEvent>, PlayerEventHandler> playerEventHandlers = new HashMap<Class<? extends PlayerEvent>, PlayerEventHandler>();
    protected Map<Class<? extends EntityEvent>, EntityEventHandler> entityEventHandlers = new HashMap<Class<? extends EntityEvent>, EntityEventHandler>();

    public InstantHandler() {
        instantListener = new InstantListener(this);

        // Load battle handlers
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
            if (battleHandler.getBattleConfiguration().checkValidity()) {
                battleHandler.start();
            }
        }
    }

    public void handlePlayerCommands(String name, String arg1, Player player) {
        if ("list".equals(arg1)) {
            Msg.sendMsg(player, ChatColor.YELLOW + "Available Battles: " + getBattleNames());
            return;
        }

        BattleHandler battleHandler = battleHandlers.get(name);

        if (battleHandler == null) {
            Msg.sendMsg(player, ChatColor.RED + "Battle was not found. Available battles are: " + getBattleNames());
            return;
        }

        if ("join".equals(arg1) && player != null) {
            if (battleHandler.getBattleData().addRegisteredPlayer(player)) {
                Msg.sendMsg(player, ChatColor.GREEN + "You joined the instant registration list for battle: " + battleHandler.getName() + ".");
            } else {
                Msg.sendMsg(player, ChatColor.RED + "You are blocked for this action.");
            }
        } else if ("leave".equals(arg1) && player != null) {
            if (battleHandler.getBattleData().unregisterPlayer(player) != null) {
                Msg.sendMsg(player, ChatColor.GREEN + "You leaved the instant.");
            } else {
                Msg.sendMsg(player, ChatColor.RED + "You were not registered.");
            }
        } else if ("spec".equals(arg1) && player != null) {
            battleHandler.getBattleData().addSpecator(player, player.getLocation());
            player.teleport(battleHandler.getBattleConfiguration().getPosSpectator());
            Msg.sendMsg(player, ChatColor.GREEN + "You joined the spectator lounge of battle: " + battleHandler.getName() + ".");
        } else if ("unspec".equals(arg1) && player != null) {
            Location loc = battleHandler.getBattleData().getOriginSpectatorLocations().remove(player);
            if (loc != null) {
                player.teleport(battleHandler.getBattleConfiguration().getPosSpectator());
                Msg.sendMsg(player, ChatColor.GREEN + "You left the spectator lounge of battle " + battleHandler.getName() + ".");
            } else {
                Msg.sendMsg(player, ChatColor.RED + "You were not a spectator.");
            }
        } else if ("back".equals(arg1) && player != null) {
            Location location = battleHandler.getBattleData().getOriginSpectatorLocations().get(player);
            // in case he was a spectator - he can go back
            if (location != null) {
                player.teleport(location);
            }
        } else {
            Msg.sendMsg(player, ChatColor.BLUE + "Unknown command: /instant " + arg1 + " " + name);
        }
    }

    public void handleOpCommands(String batlleName, String arg1, String arg2, Player player) {
        String playerName = player != null ? player.getName() : "CONSOLE";

        BattleHandler battleHandler = battleHandlers.get(batlleName);
        // if the battle doesnt exist, create it
        if (battleHandler == null) {
            battleHandler = new BattleHandler(batlleName, new BattleConfiguration(), new BattleData());
            battleHandlers.put(batlleName, battleHandler);
        }

        if (player != null && "pos1".equals(arg1))
            battleHandler.getBattleConfiguration().setPos1(player.getLocation());
        if (player != null && "pos2".equals(arg1))
            battleHandler.getBattleConfiguration().setPos2(player.getLocation());
        if (player != null && "posstart".equals(arg1))
            battleHandler.getBattleConfiguration().setPosStart(player.getLocation());
        if (player != null && "posspec".equals(arg1))
            battleHandler.getBattleConfiguration().setPosSpectator(player.getLocation());

        if ("offset".equals(arg1)) {
            if (StringUtils.isNullOrEmpty(arg2) && !NumberUtils.isDigits(arg2)) {
                Msg.sendMsg(player, ChatColor.YELLOW + "Please specify a number when the battle notifications should be sent out: '/instantop offset 5' will send first message 5 minutes before the battle starts.");
            }
            battleHandler.getBattleConfiguration().setOffset(Integer.parseInt(arg2));
        }

        if ("duration".equals(arg1)) {
            if (StringUtils.isNullOrEmpty(arg2) && !NumberUtils.isDigits(arg2)) {
                Msg.sendMsg(player, ChatColor.YELLOW + "Please specify a number how long the battle should last: '/instantop duration 30' will let the battle be 30 minutes long.");
            }
            battleHandler.getBattleConfiguration().setDuration(Integer.parseInt(arg2));
        }

        if ("stop".equals(arg1)) {
            battleHandler.stop();
        }

        if ("start".equals(arg1)) {
            // make sure the battle is not already running
            battleHandler.stop();
            battleHandler.start();
            // start the battle
            InstantConfig.saveBattleHandler(battleHandler);
        }

        if ("stat".equals(arg1)) {
            Msg.sendMsg(player, "Name: " + batlleName);
            for (Player p : battleHandler.getBattleData().getRegisteredPlayers()) {
                Msg.sendMsg(player, "Registered Player: " + p.getName());
            }
            for (Player p : battleHandler.getBattleData().getActivePlayers().keySet()) {
                Msg.sendMsg(player, "Active Player: " + p.getName());
            }
            Msg.sendMsg(player, "End Date: " + DateHelper.format(battleHandler.getBattleData().getEndDate()));
            if (battleHandler.getBattleData().getEndDate() != null) {
                Msg.sendMsg(player, "Earliest Next round: " + DateHelper.format(new Date(battleHandler.getBattleData().getEndDate().getTime() + 300000)));
            }
        }

        if ("kick".equals(arg1)) {
            Player target = Bukkit.getPlayerExact(arg2);
            if (target != null) {
                battleHandler.getBattleData().unregisterPlayer(target);
                Location loc = battleHandler.getBattleData().getActivePlayers().remove(target);
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
                battleHandler.getBattleData().addBlockedPlayer(arg2, "Banned by admin/mod: " + playerName);
                battleHandler.getBattleData().unregisterPlayer(target);
                Location loc = battleHandler.getBattleData().getActivePlayers().remove(target);
                if (loc != null) {
                    target.teleport(loc);
                }
                Msg.sendMsg(player, ChatColor.GRAY + "Blocked player " + target.getName());
                target.sendMessage(ChatColor.RED + "You are blocked now for any further instant battles!");
                Bukkit.getServer().broadcastMessage(ChatColor.RED + "Player '" + target.getName() + "' was blocked for any further instant battle by administrator.");
            }
        }

        if (player != null && "addspawn".equals(arg1)) {
            battleHandler.getBattleConfiguration().addSpawn(player.getLocation());
        }

        if ("clearspawn".equals(arg1)) {
            battleHandler.getBattleConfiguration().getSpanws().clear();
        }

        InstantConfig.saveBattleHandler(battleHandler);
        Msg.sendMsg(player, ChatColor.YELLOW + "Battle '" + batlleName + "' command " + arg1 + (StringUtils.isNullOrEmpty(arg2) ? "" : " argument " + arg2) + " by " + playerName);
    }

    @SuppressWarnings("unchecked")
    public void handlePlayerEvents(PlayerEvent playerEvent) {
        PlayerEventHandler eventHandler = playerEventHandlers.get(playerEvent.getClass());

        // can handle the event
        if (eventHandler != null) {
            for (BattleHandler battleHandler : battleHandlers.values()) {
                if (battleHandler.getBattleData().getActivePlayers().containsKey(playerEvent.getPlayer())) {
                    eventHandler.processEvent(playerEvent, battleHandler);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void handleEntityEvents(EntityEvent entityEvent) {
        if (entityEvent == null) return;
        if (entityEvent.getEntity() == null) return;

        EntityEventHandler eventHandler = entityEventHandlers.get(entityEvent.getClass());

        // can handle the event
        if (eventHandler != null && Player.class.isAssignableFrom(entityEvent.getEntity().getClass())) {
            Player player = (Player) entityEvent.getEntity();
            for (BattleHandler battleHandler : battleHandlers.values()) {
                if (battleHandler.getBattleData().getActivePlayers().containsKey(player)) {
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

    private String getBattleNames() {
        String batlleNames = "";
        for (String s : battleHandlers.keySet()) {
            if (!StringUtils.isNullOrEmpty(batlleNames)) {
                batlleNames += ", ";
            }
            batlleNames += s;
        }
        if (StringUtils.isNullOrEmpty(batlleNames)) {
            return "None";
        }
        return batlleNames;
    }

    public HashMap<String, BattleHandler> getBattleHandlers() {
        return battleHandlers;
    }
}

package ch.bukkit.playground.instant.listener;

import ch.bukkit.playground.Plugin;
import ch.bukkit.playground.instant.arena.Arena;
import ch.bukkit.playground.instant.tasks.ArenaTask;
import ch.bukkit.playground.instant.tasks.MessageTask;
import org.apache.commons.collections.MapUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;

import java.util.logging.Logger;

public class ArenaListener implements Listener {

    Logger logger = Logger.getLogger("ArenaListener");
    private ArenaTask arenaTask;
    private Arena arena;


    public ArenaListener() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin(Plugin.PLUGIN_NAME));
    }

    public void initialize(ArenaTask arenaTask) {
        this.arenaTask = arenaTask;
        this.arena = arenaTask.getArena();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerRespawn(PlayerRespawnEvent respawnEvent) {
        if (arena.getActivePlayers().containsKey(respawnEvent.getPlayer())) {
            Location loc = arena.getActivePlayers().remove(respawnEvent.getPlayer());

            if (arena.getPosSpectator() != null) {
                if(Plugin.DEBUG) logger.info("Player " + respawnEvent.getPlayer().getName() + " respawns at spectator.");

                // bring player to spectator lounge
                respawnEvent.setRespawnLocation(arena.getPosSpectator());
                arena.addSpecator(respawnEvent.getPlayer(), loc);
            } else {
                if(Plugin.DEBUG) logger.info("Player " + respawnEvent.getPlayer().getName() + " respawns at origin.");

                // default points to origin location of this player
                respawnEvent.setRespawnLocation(loc);
            }

            if (MapUtils.isEmpty(arena.getActivePlayers())) {
                if(Plugin.DEBUG) logger.info("Player " + respawnEvent.getPlayer().getName() + " was last one in battle.");

                arenaTask.cleanup();
            } else {
                if(Plugin.DEBUG) logger.info("Player " + respawnEvent.getPlayer().getName() + " died.");

                MessageTask messageTask = new MessageTask(arena.getActivePlayers().keySet(), respawnEvent.getPlayer().getName() + " died!");
                messageTask.run();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerMove(PlayerMoveEvent move) {
        // players are not supposed to move out the arena
        if (!move.isCancelled() && arena.getActivePlayers().containsKey(move.getPlayer())) {
            move.setCancelled(!arena.isInArena(move.getTo()));
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerTeleport(PlayerTeleportEvent teleport) {
        // players are not supposed to teleport out the arena
        if (!teleport.getPlayer().isOp() && !teleport.isCancelled() && arena.getActivePlayers().containsKey(teleport.getPlayer())) {
            teleport.setCancelled(!arena.isInArena(teleport.getTo()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerFlight(PlayerToggleFlightEvent flight) {
        // active players are not allowed to fly
        if (arena.getActivePlayers().containsKey(flight.getPlayer()) && !flight.getPlayer().isOp()) {
            flight.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDeathEvent(EntityDeathEvent death) {
        if (death.getEntity() instanceof Player) {
            Player player = (Player) death.getEntity();

            // vip players will not loose any items
            if (arena.getActivePlayers().containsKey(player)) {
                death.getDrops().clear();
                death.setDroppedExp(0);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent quit) {
        // remove player from arena and teleport to start point
        Location loc = arena.getActivePlayers().remove(quit.getPlayer());
        if (loc != null) {
            quit.getPlayer().teleport(loc);

            if (MapUtils.isEmpty(arena.getActivePlayers())) {
                arenaTask.cleanup();
            } else {
                MessageTask messageTask = new MessageTask(arena.getActivePlayers().keySet(), quit.getPlayer().getName() + " logged out - did he cheat on you?");
                messageTask.run();
            }

            if(quit.getPlayer().getHealth() <= 4) {
                arena.addBlockedPlayer(quit.getPlayer().getName(), "Player left because of low health.");
                quit.getPlayer().getInventory().clear();
            }
        }
    }
}


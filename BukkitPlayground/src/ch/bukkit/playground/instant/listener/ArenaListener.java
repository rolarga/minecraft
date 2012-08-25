package ch.bukkit.playground.instant.listener;

import ch.bukkit.playground.Plugin;
import ch.bukkit.playground.instant.arena.Arena;
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
    private Arena arena;


    public ArenaListener(Arena arena) {
        this.arena = arena;

        Bukkit.getServer().getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin(Plugin.PLUGIN_NAME));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerRespawn(PlayerRespawnEvent respawnEvent) {
        if(arena.getActivePlayers().containsKey(respawnEvent.getPlayer())) {
            Location loc = arena.getActivePlayers().remove(respawnEvent.getPlayer());
            logger.info("Flight event triggered by player: " + respawnEvent.getPlayer().getName());
            if(arena.getPosSpectator() != null) {
                // bring player to spectator lounge
                respawnEvent.setRespawnLocation(arena.getPosSpectator());
            } else {
                // default points to origin location of this player
                respawnEvent.setRespawnLocation(loc);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerMove(PlayerMoveEvent move) {
        // players are not supposed to move out the arena
        if(!move.isCancelled() && arena.getActivePlayers().containsKey(move.getPlayer())) {
            logger.info("Active Player '" + move.getPlayer().getName() + "' moves.");
            move.setCancelled(!arena.isInArena(move.getTo()));
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerTeleport(PlayerTeleportEvent teleport) {
        // players are not supposed to teleport out the arena
        if(!teleport.getPlayer().isOp() && !teleport.isCancelled() && arena.getActivePlayers().containsKey(teleport.getPlayer())) {
            teleport.setCancelled(!arena.isInArena(teleport.getTo()));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerFlight(PlayerToggleFlightEvent flight) {
        // active players are not allowed to fly
        if(arena.getActivePlayers().containsKey(flight.getPlayer()) && !flight.getPlayer().isOp()) {
            logger.info("Flight event triggered by player: " + flight.getPlayer().getName());
            flight.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDeathEvent(EntityDeathEvent death) {

        if(death.getEntity() instanceof Player) {
            Player player = (Player) death.getEntity();
            player.getWorld().strikeLightning(player.getLocation());

            // vip players will not loose any items
            if(arena.getActivePlayers().containsKey(player)) {
                death.getDrops().clear();
                death.setDroppedExp(0);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent quit) {
        // remove player from arena and teleport to start point
        Location loc = arena.getActivePlayers().remove(quit.getPlayer());
        quit.getPlayer().teleport(loc);
    }
}


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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

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
            arena.getActivePlayers().remove(respawnEvent.getPlayer());
            logger.info("Flight event triggered by player: " + respawnEvent.getPlayer().getName());
            if(arena.getPosSpectator() != null) {
                // bring player to spectator lounge
                respawnEvent.setRespawnLocation(arena.getPosSpectator());
            } else {
                // default points to origin location of this player
                respawnEvent.setRespawnLocation(arena.getActivePlayers().get(respawnEvent.getPlayer()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerMove(PlayerMoveEvent move) {
        // players are not supposed to move out the arena
        if(!move.isCancelled() && arena.getActivePlayers().containsKey(move.getPlayer())) {
            logger.info("Active Player '" + move.getPlayer().getName() + "' moves.");
            move.setCancelled(isPlayerInArena(move.getTo()));
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerMove(PlayerTeleportEvent teleport) {
        // players are not supposed to teleport out the arena
        if(!teleport.isCancelled() && arena.getActivePlayers().containsKey(teleport.getPlayer())) {
            teleport.setCancelled(isPlayerInArena(teleport.getTo()));
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
        // vip players will not loose any items
        if(death.getEntity() instanceof Player) {
            Player player = (Player) death.getEntity();
            if(arena.getActivePlayers().containsKey(player)) {
                death.getDrops().clear();
                death.setDroppedExp(0);
            }
            arena.getActivePlayers().remove(player);
        }
    }

    // Helper methods

    private boolean isPlayerInArena(Location to) {
        double arenaBottom = Math.min(arena.getPos1().getX(), arena.getPos2().getX());
        double arenaTop = Math.max(arena.getPos1().getX(), arena.getPos2().getX());
        double arenaLeft = Math.min(arena.getPos1().getY(), arena.getPos2().getY());
        double arenaRight = Math.max(arena.getPos1().getY(), arena.getPos2().getY());
        double arenaGround = Math.max(arena.getPos1().getZ(), arena.getPos2().getZ());
        double arenaHeight = arenaGround + arena.getHeight();
        double playerX = to.getX();
        double playerY = to.getY();
        double playerZ = to.getZ();

        logger.info("Move event of player catched X: " + playerX + " Y: " + playerY + " Z: " + playerZ);

        // return true if the player would go out of the arena by that movement
        return playerX < arenaBottom || playerX > arenaTop ||
                playerY < arenaLeft || playerY > arenaRight ||
                playerZ < arenaGround || playerZ > arenaHeight;
    }
}


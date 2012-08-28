package ch.bukkit.playground.instant;

import ch.bukkit.playground.Plugin;
import ch.bukkit.playground.instant.model.Arena;
import ch.bukkit.playground.instant.tasks.ArenaHandlerTask;
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

public class InstantListener implements Listener {

    private InstantHandler instantHandler;

    public InstantListener(InstantHandler instantHandler) {
        this.instantHandler = instantHandler;

        Bukkit.getServer().getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin(Plugin.PLUGIN_NAME));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerRespawn(PlayerRespawnEvent respawnEvent) {
        instantHandler.handlePlayerEvents(respawnEvent);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerMove(PlayerMoveEvent move) {
        instantHandler.handlePlayerEvents(move);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerTeleport(PlayerTeleportEvent teleport) {
        instantHandler.handlePlayerEvents(teleport);
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
        // remove player from model and teleport to start point
        Location loc = arena.getActivePlayers().remove(quit.getPlayer());
        if (loc != null) {
            quit.getPlayer().teleport(loc);

            if (MapUtils.isEmpty(arena.getActivePlayers())) {
                arenaHandlerTask.cleanup();
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


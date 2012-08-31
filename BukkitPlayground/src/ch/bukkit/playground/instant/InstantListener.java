package ch.bukkit.playground.instant;

import ch.bukkit.playground.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;

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
        instantHandler.handlePlayerEvents(flight);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDeathEvent(EntityDeathEvent death) {
        instantHandler.handleEntityEvents(death);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent quit) {
        instantHandler.handlePlayerEvents(quit);
    }
}


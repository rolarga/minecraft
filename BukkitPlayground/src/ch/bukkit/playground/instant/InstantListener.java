package ch.bukkit.playground.instant;

import ch.bukkit.playground.InstantBattlePlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.*;

public class InstantListener implements Listener {

    protected InstantHandler instantHandler;

    public InstantListener(InstantHandler instantHandler) {
        this.instantHandler = instantHandler;

        if (Bukkit.getServer() != null) {
            Bukkit.getServer().getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin(InstantBattlePlugin.PLUGIN_NAME));
        }
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
    public void entityDamageEvent(EntityDamageByEntityEvent damage) {
        instantHandler.handleEntityEvents(damage);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerQuit(PlayerQuitEvent quit) {
        instantHandler.handlePlayerEvents(quit);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityExplode(EntityExplodeEvent expl) {
        instantHandler.handleEntityEvents(expl);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerPreProcess(PlayerCommandPreprocessEvent expl) {
        if (!expl.getPlayer().isOp() && (expl.getMessage().toLowerCase().startsWith("/plugin") || expl.getMessage().toLowerCase().startsWith("/pl"))) {
            expl.setCancelled(true);
            expl.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
        }
    }
}


package ch.bukkit.playground.instant.eventhandlers;

import ch.bukkit.playground.instant.BattleHandler;
import ch.bukkit.playground.instant.model.ArenaConfiguration;
import ch.bukkit.playground.util.LocationHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.logging.Logger;

public class PlayerTeleportEventHandler implements PlayerEventHandler<PlayerTeleportEvent> {

    Logger logger = Logger.getLogger("PlayerTeleportEventHandler");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void processEvent(PlayerTeleportEvent teleport, BattleHandler battleHandler) {
        // players are not supposed to teleport out the model
        if (!teleport.getPlayer().isOp() && !teleport.isCancelled()) {
            ArenaConfiguration configuration = battleHandler.getArenaConfiguration();
            teleport.setCancelled(!LocationHelper.isInSquare(configuration.getPos1(), configuration.getPos2(), teleport.getTo()));
        }
    }
}

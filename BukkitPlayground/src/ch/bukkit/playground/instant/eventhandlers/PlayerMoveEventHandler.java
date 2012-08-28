package ch.bukkit.playground.instant.eventhandlers;

import ch.bukkit.playground.instant.model.ArenaConfiguration;
import ch.bukkit.playground.instant.tasks.ArenaHandlerTask;
import ch.bukkit.playground.util.LocationHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.logging.Logger;

public class PlayerMoveEventHandler implements PlayerEventHandler<PlayerMoveEvent> {

    Logger logger = Logger.getLogger("PlayerMoveEventHandler");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void processEvent(PlayerMoveEvent move, ArenaHandlerTask arenaHandlerTask) {
        // players are not supposed to move out the model
        if (!move.isCancelled()) {
            ArenaConfiguration configuration = arenaHandlerTask.getArenaConfiguration();
            move.setCancelled(!LocationHelper.isInSquare(configuration.getPos1(), configuration.getPos2(), move.getTo()));
        }
    }
}

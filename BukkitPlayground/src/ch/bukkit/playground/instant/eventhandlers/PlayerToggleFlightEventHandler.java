package ch.bukkit.playground.instant.eventhandlers;

import ch.bukkit.playground.instant.tasks.ArenaHandlerTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.logging.Logger;

public class PlayerToggleFlightEventHandler implements PlayerEventHandler<PlayerToggleFlightEvent> {

    Logger logger = Logger.getLogger("PlayerToggleFlightEventHandler");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void processEvent(PlayerToggleFlightEvent flight, ArenaHandlerTask arenaHandlerTask) {
        // active players are not allowed to fly
        if (!flight.getPlayer().isOp()) {
            flight.setCancelled(true);
        }
    }
}

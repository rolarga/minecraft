package ch.bukkit.playground.instant.eventhandlers.player;

import ch.bukkit.playground.instant.BattleHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.logging.Logger;

public class PlayerToggleFlightEventHandler implements PlayerEventHandler<PlayerToggleFlightEvent> {

    Logger logger = Logger.getLogger("PlayerToggleFlightEventHandler");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void processEvent(PlayerToggleFlightEvent flight, BattleHandler battleHandler) {
        // active players are not allowed to fly
        if (!flight.getPlayer().isOp()) {
            flight.setCancelled(true);
        }
    }
}

package ch.bukkit.playground.instant.eventhandlers;

import ch.bukkit.playground.instant.tasks.ArenaHandlerTask;
import ch.bukkit.playground.instant.tasks.MessageTask;
import org.apache.commons.collections.MapUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class PlayerQuitEventHandler implements PlayerEventHandler<PlayerQuitEvent> {

    Logger logger = Logger.getLogger("PlayerToggleFlightEventHandler");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void processEvent(PlayerQuitEvent quit, ArenaHandlerTask arenaHandlerTask) {
        // remove player from model and teleport to start point
        Location loc = arenaHandlerTask.getArenaData().getActivePlayers().remove(quit.getPlayer());
        if (loc != null) {
            quit.getPlayer().teleport(loc);

            if (MapUtils.isEmpty(arenaHandlerTask.getArenaData().getActivePlayers())) {
                arenaHandlerTask.cleanup();
            } else {
                MessageTask messageTask = new MessageTask(arenaHandlerTask.getArenaData().getActivePlayers().keySet(), quit.getPlayer().getName() + " logged out - did he cheat on you?");
                messageTask.run();
            }

            if (quit.getPlayer().getHealth() <= 4) {
                arenaHandlerTask.getArenaData().addBlockedPlayer(quit.getPlayer().getName(), "Player left because of low health.");
                quit.getPlayer().getInventory().clear();
            }
        }
    }
}

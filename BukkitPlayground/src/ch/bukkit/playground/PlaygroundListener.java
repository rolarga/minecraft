package ch.bukkit.playground;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.logging.Logger;

public class PlaygroundListener implements Listener {

    Logger logger = Logger.getLogger("PlaygroundListener");

    @EventHandler(priority = EventPriority.NORMAL)
    public void playerMove(PlayerMoveEvent move) {

    }
}
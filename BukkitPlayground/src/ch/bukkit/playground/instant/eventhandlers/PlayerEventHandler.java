package ch.bukkit.playground.instant.eventhandlers;

import ch.bukkit.playground.instant.tasks.ArenaHandlerTask;
import org.bukkit.event.player.PlayerEvent;

public interface PlayerEventHandler<T extends PlayerEvent> {
    public void processEvent(T event, ArenaHandlerTask arenaHandlerTask);
}

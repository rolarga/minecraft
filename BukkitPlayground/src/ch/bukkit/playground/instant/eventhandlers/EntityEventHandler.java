package ch.bukkit.playground.instant.eventhandlers;

import ch.bukkit.playground.instant.tasks.ArenaHandlerTask;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;

public interface EntityEventHandler<T extends EntityEvent> {
    public void processEvent(T event, ArenaHandlerTask arenaHandlerTask, Player player);
}

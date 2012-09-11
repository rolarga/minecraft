package ch.bukkit.playground.instant.eventhandlers.entity;

import ch.bukkit.playground.instant.BattleHandler;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityEvent;

public interface EntityEventHandler<T extends EntityEvent> {
    public void processEvent(T event, BattleHandler battleHandler, Entity entity);
}

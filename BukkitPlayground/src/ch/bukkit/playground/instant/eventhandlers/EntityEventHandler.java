package ch.bukkit.playground.instant.eventhandlers;

import ch.bukkit.playground.instant.BattleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;

public interface EntityEventHandler<T extends EntityEvent> {
    public void processEvent(T event, BattleHandler battleHandler, Player player);
}

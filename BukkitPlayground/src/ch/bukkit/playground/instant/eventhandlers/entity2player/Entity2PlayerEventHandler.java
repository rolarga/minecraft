package ch.bukkit.playground.instant.eventhandlers.entity2player;

import ch.bukkit.playground.instant.BattleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityEvent;

public interface Entity2PlayerEventHandler<T extends EntityEvent> {
    public void processEvent(T event, BattleHandler battleHandler, Player player);
}

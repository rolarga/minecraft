package ch.bukkit.playground.instant.eventhandlers;

import ch.bukkit.playground.instant.BattleHandler;
import org.bukkit.event.player.PlayerEvent;

public interface PlayerEventHandler<T extends PlayerEvent> {
    public void processEvent(T event, BattleHandler battleHandler);
}

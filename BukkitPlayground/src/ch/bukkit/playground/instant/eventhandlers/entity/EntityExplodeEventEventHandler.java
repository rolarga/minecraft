package ch.bukkit.playground.instant.eventhandlers.entity;

import ch.bukkit.playground.instant.BattleHandler;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

public class EntityExplodeEventEventHandler implements EntityEventHandler<EntityExplodeEvent> {

    @Override
    public void processEvent(EntityExplodeEvent event, BattleHandler battleHandler, Entity entity) {
        // in a battle no entity can explode
        event.setCancelled(true);
        event.blockList().clear();
        event.setYield(0f);
    }
}

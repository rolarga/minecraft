package ch.bukkit.playground.instant.eventhandlers;

import ch.bukkit.playground.instant.BattleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.logging.Logger;

public class EntityDeathEventHandler implements EntityEventHandler<EntityDeathEvent> {

    Logger logger = Logger.getLogger("EntityDeathEventHandler");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void processEvent(EntityDeathEvent death, BattleHandler battleHandler, Player player) {
        if (player.hasPermission("instant.vip")) {
            death.getDrops().clear();
            death.setDroppedExp(0);
        }
    }
}

package ch.bukkit.playground.instant.eventhandlers.entity2player;

import ch.bukkit.playground.InstantBattlePlugin;
import ch.bukkit.playground.instant.BattleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.logging.Logger;

public class EntityDeath2PlayerEventHandler implements Entity2PlayerEventHandler<EntityDeathEvent> {

    Logger logger = Logger.getLogger("EntityDeath2PlayerEventHandler");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void processEvent(EntityDeathEvent death, BattleHandler battleHandler, Player player) {
        if (player.hasPermission("instant.vip")) {
            death.getDrops().clear();
            death.setDroppedExp(0);
            if (InstantBattlePlugin.DEBUG) {
                logger.info("Saved inventory for vip player " + player.getName());
            }
        }
    }
}

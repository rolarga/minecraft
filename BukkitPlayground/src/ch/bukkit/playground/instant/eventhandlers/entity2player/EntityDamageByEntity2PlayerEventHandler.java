package ch.bukkit.playground.instant.eventhandlers.entity2player;

import ch.bukkit.playground.InstantBattlePlugin;
import ch.bukkit.playground.instant.BattleHandler;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.logging.Logger;

public class EntityDamageByEntity2PlayerEventHandler implements Entity2PlayerEventHandler<EntityDamageByEntityEvent> {

    private static Logger logger = Logger.getLogger("EntityDamageByEntity2PlayerEventHandler");

    @Override
    public void processEvent(EntityDamageByEntityEvent event, BattleHandler battleHandler, Player player) {
        if (battleHandler.getBattleData().isActive()) {
            Player damager = null;

            if (Player.class.isAssignableFrom(event.getDamager().getClass())) {
                damager = (Player) event.getDamager();
            } else if (Arrow.class.isAssignableFrom(event.getDamager().getClass())) {
                Arrow arrow = (Arrow) event.getDamager();
                if (Player.class.isAssignableFrom(arrow.getShooter().getClass())) {
                    damager = (Player) arrow.getShooter();
                }
            }

            if (player != null) {
                switch (battleHandler.getBattleConfiguration().getBattleType()) {
                    case COOP:
                        event.setCancelled(true);
                        event.setDamage(0);
                        if (InstantBattlePlugin.DEBUG) {
                            logger.info("Set damage for player " + player.getName() + " to 0");
                        }
                        break;
                    case GROUPPVP:
                        // check if the damager is in the same group as the player --> cancel event
                        for (List<Player> players : battleHandler.getBattleData().getGroups().values()) {
                            if (players.contains(player) && players.contains(damager)) {
                                event.setCancelled(true);
                                event.setDamage(0);
                            }
                        }
                        if (InstantBattlePlugin.DEBUG) {
                            logger.info("Set damage for player " + player.getName() + " to 0");
                        }
                        break;
                    default:
                        break;
                }
            }
        } else {
            event.setCancelled(true);
            event.setDamage(0);
            if (InstantBattlePlugin.DEBUG) {
                logger.info("Set damage for player " + player.getName() + " to 0");
            }
        }
    }
}

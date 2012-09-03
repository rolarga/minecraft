package ch.bukkit.playground.instant.eventhandlers;

import ch.bukkit.playground.instant.BattleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;

public class EntityDamageByEntityEventHandler implements EntityEventHandler<EntityDamageByEntityEvent> {

    @Override
    public void processEvent(EntityDamageByEntityEvent event, BattleHandler battleHandler, Player player) {
        if (battleHandler.getBattleData().isActive()) {
            if (Player.class.isAssignableFrom(event.getDamager().getClass())) {
                Player damager = (Player) event.getDamager();

                switch (battleHandler.getBattleConfiguration().getBattleType()) {
                    case COOP:
                        event.setCancelled(true);
                        event.setDamage(0);
                        break;
                    case GROUPPVP:
                        // check if the damager is in the same group as the player --> cancel event
                        for (List<Player> players : battleHandler.getBattleData().getGroups().values()) {
                            if (players.contains(player) && players.contains(damager)) {
                                event.setCancelled(true);
                                event.setDamage(0);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        } else {
            event.setCancelled(true);
            event.setDamage(0);
        }
    }
}

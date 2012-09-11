package ch.bukkit.playground.instant.eventhandlers.player;

import ch.bukkit.playground.instant.BattleHandler;
import ch.bukkit.playground.instant.tasks.MessageTask;
import org.apache.commons.collections.MapUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Logger;

public class PlayerQuitEventHandler implements PlayerEventHandler<PlayerQuitEvent> {

    Logger logger = Logger.getLogger("PlayerToggleFlightEventHandler");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void processEvent(PlayerQuitEvent quit, BattleHandler battleHandler) {
        // remove player from model and teleport to start point
        Location loc = battleHandler.getBattleData().getActivePlayers().remove(quit.getPlayer());
        if (loc != null) {
            quit.getPlayer().teleport(loc);

            if (MapUtils.isEmpty(battleHandler.getBattleData().getActivePlayers())) {
                battleHandler.finishBattle();
            } else {
                new MessageTask(battleHandler.getBattleData().getActivePlayers().keySet(), quit.getPlayer().getName() + " logged out - did he cheat on you?").run();
            }
        }

        // punish all idiots
        if (!quit.getPlayer().isOp() && quit.getPlayer().getHealth() <= 4) {
            battleHandler.getBattleData().addBlockedPlayer(quit.getPlayer().getName(), "Player left because of low health.");
            quit.getPlayer().getInventory().clear();
        }
    }
}

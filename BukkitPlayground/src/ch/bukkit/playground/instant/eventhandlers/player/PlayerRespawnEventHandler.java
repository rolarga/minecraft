package ch.bukkit.playground.instant.eventhandlers.player;


import ch.bukkit.playground.InstantBattlePlugin;
import ch.bukkit.playground.instant.BattleHandler;
import ch.bukkit.playground.instant.model.BattleConfiguration;
import ch.bukkit.playground.instant.model.BattleData;
import ch.bukkit.playground.instant.tasks.MessageTask;
import org.apache.commons.collections.MapUtils;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.logging.Logger;

public class PlayerRespawnEventHandler implements PlayerEventHandler<PlayerRespawnEvent> {

    Logger logger = Logger.getLogger("PlayerRespawnEventHandler");

    @EventHandler(priority = EventPriority.HIGHEST)
    public void processEvent(PlayerRespawnEvent event, BattleHandler battleHandler) {
        BattleConfiguration battleConfiguration = battleHandler.getBattleConfiguration();
        BattleData battleData = battleHandler.getBattleData();

        Location loc = battleData.getActivePlayers().remove(event.getPlayer());

        if (battleConfiguration.getPosSpectator() != null) {
            if (InstantBattlePlugin.DEBUG)
                logger.info("Player " + event.getPlayer().getName() + " respawns at spectator.");

            // bring player to spectator lounge
            event.setRespawnLocation(battleConfiguration.getPosSpectator());
            battleData.addSpecator(event.getPlayer(), loc);
        } else {
            if (InstantBattlePlugin.DEBUG)
                logger.info("Player " + event.getPlayer().getName() + " respawns at origin.");

            // default points to origin location of this player
            event.setRespawnLocation(loc);
        }

        if (MapUtils.isEmpty(battleData.getActivePlayers())) {
            if (InstantBattlePlugin.DEBUG)
                logger.info("Player " + event.getPlayer().getName() + " was last one in battle.");

            battleHandler.finishBattle();
        } else {
            if (InstantBattlePlugin.DEBUG) logger.info("Player " + event.getPlayer().getName() + " died.");

            MessageTask messageTask = new MessageTask(battleData.getActivePlayers().keySet(), event.getPlayer().getName() + " died!");
            messageTask.run();
        }
    }
}

package ch.bukkit.playground.util;

import ch.bukkit.playground.InstantBattlePlugin;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class Msg {

    private static Logger logger = Logger.getLogger("Msg");

    public static void sendMsg(Player player, String message) {
        if (player != null) {
            player.sendMessage(message);

            if (InstantBattlePlugin.DEBUG) {
                logger.info(message);
            }
        } else {
            logger.info(message);
        }
    }
}

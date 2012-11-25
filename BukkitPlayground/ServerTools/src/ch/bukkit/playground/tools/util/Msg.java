package ch.bukkit.playground.tools.util;

import ch.bukkit.playground.tools.ServerToolsPlugin;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class Msg {

    private static Logger logger = Logger.getLogger("Msg");

    public static void sendMsg(Player player, String message) {
        if (player != null) {
            player.sendMessage(message);

            if (ServerToolsPlugin.DEBUG) {
                logger.info(message);
            }
        } else {
            logger.info(message);
        }
    }
}

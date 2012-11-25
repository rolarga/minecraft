package ch.bukkit.playground.tools;

import ch.bukkit.playground.tools.util.Msg;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ServerToolsListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void commandBlockEvent(PlayerInteractEvent event) {

        if ((event.getItem() != null && event.getItem().getTypeId() == 137 || event.getClickedBlock() != null && event.getClickedBlock().getTypeId() == 137) &&
                !(event.getPlayer().isOp() || event.getPlayer().hasPermission("servertools.commandblock"))) {
            if (ServerToolsPlugin.DEBUG)
                Msg.sendMsg(null, "Cancelled CommandBlock event for player: " + event.getPlayer().getName());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerPreProcess(PlayerCommandPreprocessEvent expl) {
        if (!expl.getPlayer().isOp() && (expl.getMessage().toLowerCase().startsWith("/plugin") || expl.getMessage().toLowerCase().startsWith("/pl"))) {
            expl.setCancelled(true);
            expl.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
            if (ServerToolsPlugin.DEBUG)
                Msg.sendMsg(null, "player: " + expl.getPlayer().getName() + " tried to execute: " + expl.getMessage());
        }
    }
}

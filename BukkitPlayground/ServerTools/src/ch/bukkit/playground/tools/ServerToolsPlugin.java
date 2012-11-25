package ch.bukkit.playground.tools;

import ch.bukkit.playground.tools.util.Msg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ServerToolsPlugin extends JavaPlugin {
    private static final String PLUGIN_NAME = "ServerTools";
    public static boolean DEBUG = false;

    @Override
    public void onEnable() {

        if (Bukkit.getServer() != null) {
            Bukkit.getServer().getPluginManager().registerEvents(new ServerToolsListener(), Bukkit.getPluginManager().getPlugin(ServerToolsPlugin.PLUGIN_NAME));
        }
        System.out.println("###ServerTools have been ENABLED!###");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isOP = true;
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            isOP = player.isOp();
        }
        if (isOP && cmd.getName().equalsIgnoreCase("servertoolsdebug")) {
            DEBUG = !DEBUG;
            Msg.sendMsg(player, "Debug is now: " + DEBUG);
        } else if ((isOP || player.hasPermission("servertools.visibility")) && player != null) {
            if (cmd.getName().equalsIgnoreCase("invisible")) {
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    Msg.sendMsg(player, ChatColor.GREEN + "You are already " + ChatColor.RED + "INVISIBLE" + ChatColor.GREEN + "!");
                } else {
                    PotionEffect potion = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1);
                    player.addPotionEffect(potion);
                    Msg.sendMsg(player, ChatColor.GREEN + "You are now " + ChatColor.RED + "INVISIBLE" + ChatColor.GREEN + "!");
                }
            } else if (cmd.getName().equalsIgnoreCase("visible")) {
                if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    Msg.sendMsg(player, ChatColor.GREEN + "You are now " + ChatColor.RED + "VISIBLE" + ChatColor.GREEN + "!");
                } else {
                    Msg.sendMsg(player, ChatColor.GREEN + "You are already " + ChatColor.RED + "VISIBLE" + ChatColor.GREEN + "!");
                }
            }
            if (cmd.getName().equalsIgnoreCase("info")) {
                Msg.sendMsg(player, ChatColor.GREEN + "Name: " + ChatColor.AQUA + Bukkit.getServer().getServerName());
                Msg.sendMsg(player, ChatColor.GREEN + "Slogan: " + ChatColor.AQUA + Bukkit.getServer().getMotd());
                Msg.sendMsg(player, ChatColor.GREEN + "Online Players: " + ChatColor.AQUA + Bukkit.getServer().getOnlinePlayers().length);
                Msg.sendMsg(player, ChatColor.GREEN + "Total Players: " + ChatColor.AQUA + (Bukkit.getServer().getOnlinePlayers().length + Bukkit.getServer().getOfflinePlayers().length));
                Msg.sendMsg(player, ChatColor.GREEN + "PVP: " + ChatColor.AQUA + "true    " + ChatColor.GREEN + "Nether: " + ChatColor.AQUA + Bukkit.getServer().getAllowNether() + ChatColor.GREEN + "    The End: " + ChatColor.AQUA + Bukkit.getServer().getAllowEnd());
                Msg.sendMsg(player, ChatColor.GREEN + "Supporter: ");
                Msg.sendMsg(player, ChatColor.GREEN + "Moderator: ");
                Msg.sendMsg(player, ChatColor.GREEN + "Admin: ");
                Msg.sendMsg(player, ChatColor.GREEN + "Owner: ");


            }
            boolean first = true;
            String opnames = "";
            for (OfflinePlayer playerop : Bukkit.getServer().getOperators()) {
                if (first) first = false;
                else opnames += ", ";
                opnames += playerop.getName();
            }
            if (cmd.getName().equalsIgnoreCase(("server"))) {
                Msg.sendMsg(player, ChatColor.GREEN + "Name: " + ChatColor.AQUA + Bukkit.getServer().getServerName());
                Msg.sendMsg(player, ChatColor.GREEN + "Slogan: " + ChatColor.AQUA + Bukkit.getServer().getMotd());
                Msg.sendMsg(player, ChatColor.GREEN + "Online Players: " + ChatColor.AQUA + Bukkit.getServer().getOnlinePlayers().length);
                Msg.sendMsg(player, ChatColor.GREEN + "Total Players: " + ChatColor.AQUA + (Bukkit.getServer().getOnlinePlayers().length + Bukkit.getServer().getOfflinePlayers().length));
                Msg.sendMsg(player, ChatColor.GREEN + "PVP: " + ChatColor.AQUA + "true    " + ChatColor.GREEN + "Nether: " + ChatColor.AQUA + Bukkit.getServer().getAllowNether() + ChatColor.GREEN + "    The End: " + ChatColor.AQUA + Bukkit.getServer().getAllowEnd());
                Msg.sendMsg(player, ChatColor.GOLD + "OP: " + ChatColor.AQUA + ChatColor.ITALIC + opnames);
                Msg.sendMsg(player, ChatColor.DARK_RED + "Banned: " + Bukkit.getServer().getBannedPlayers().size() + " for more Informations do: /server banned");


                if (args != null && args.length > 0) {
                    if (args[0].equalsIgnoreCase("banned")) {
                        String names = "";
                        for (OfflinePlayer offlinePlayer : Bukkit.getServer().getBannedPlayers()) {
                            if (first) first = false;
                            else names += ", ";
                            names += offlinePlayer.getName();
                        }
                        Msg.sendMsg(player, "Banned Players: " + names);
                    }
                }
            }

            if (isOP && cmd.getName().equalsIgnoreCase("name")) {


                if (args != null && args.length > 0) {
                    String text = ChatColor.translateAlternateColorCodes('&', args[0]);
                    player.setDisplayName(text);
                    player.setPlayerListName(text);
                }
            }
        }

        return true;
    }

}
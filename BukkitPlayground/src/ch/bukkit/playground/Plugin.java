package ch.bukkit.playground;

import ch.bukkit.playground.instant.InstantHandler;
import ch.bukkit.playground.util.ClassPathHack;
import ch.bukkit.playground.util.Msg;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class Plugin extends JavaPlugin {


    public static final String PLUGIN_NAME = "BukkitPlayground";
    public static final Charset CHARSET = Charset.forName("UTF-8");
    public static boolean DEBUG = false;
    private static Logger logger = Logger.getLogger("Plugin");

    private InstantHandler instantHandler;

    @Override
    public void onEnable() {
        logger.info("Playground Plugin enabled!");

        InitializeLibraries();

        instantHandler = new InstantHandler();
    }

    private void InitializeLibraries() {
        try {
            ClassPathHack.addFile("./lib/commons-collections-3.2.1.jar");
            ClassPathHack.addFile("./lib/commons-dbutils-1.3.jar");
            ClassPathHack.addFile("./lib/commons-io-2.1.jar");
            ClassPathHack.addFile("./lib/gson-2.2.2.jar");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        boolean isOP = true;
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            isOP = player.isOp();
        }

        if(cmd.getName().equalsIgnoreCase("instant") && ArrayUtils.contains(args, "join")){
            instantHandler.registerPlayer(player);
        } else if(cmd.getName().equalsIgnoreCase("instant") && ArrayUtils.contains(args, "leave")){
            instantHandler.unregisterPlayer(player);
        } else if(cmd.getName().equalsIgnoreCase("instant")) {
            Msg.sendMsg(player, "/instant join|leave");
        } else if(cmd.getName().equalsIgnoreCase("instant") && ArrayUtils.contains(args, "specjoin")){
            instantHandler.specJoin(player);
        } else if(cmd.getName().equalsIgnoreCase("instant") && ArrayUtils.contains(args, "specleave")){
            instantHandler.specLeave(player);
        } else if(cmd.getName().equalsIgnoreCase("instant")) {
            Msg.sendMsg(player, "/instant join|leave|specjoin|specleave");
        } else if(isOP && cmd.getName().equalsIgnoreCase("instantop") && args != null && args.length > 1 ){
            String name = args[0];
            String arg1 = args[1];
            String arg2 = args.length > 2 ? args[2] : null;

            instantHandler.handlePlayerCommands(name, arg1, arg2, player);
        } else if(isOP && cmd.getName().equalsIgnoreCase("instantop")) {
            Msg.sendMsg(player, "/instantop pos1|pos2|posstart|posspec|starttime|stat|restart|forcestop|kick|ban|addspawn|clearspawn");
        } else if(isOP && cmd.getName().equalsIgnoreCase("instantdebug")) {
            DEBUG = !DEBUG;
            Msg.sendMsg(player, "Debug is now: " + DEBUG);
        }

        return true;
    }

    @Override
    public void onDisable() {
        if(instantHandler != null) {
            try {
                instantHandler.disable();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("Playground Plugin disabled!");
    }
}

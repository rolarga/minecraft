package ch.bukkit.playground;

import ch.bukkit.playground.instant.InstantHandler;
import ch.bukkit.playground.util.ClassPathHack;
import ch.bukkit.playground.util.Msg;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class InstantBattlePlugin extends JavaPlugin {

    public final static File PLUGIN_DIRECTORY = new File("plugins/InstantBattle/");
    public final static String PLUGIN_NAME = "InstantBattle";
    public final static Charset CHARSET = Charset.forName("UTF-8");
    private final static Logger logger = Logger.getLogger("InstantBattlePlugin");

    public static boolean DEBUG = false;

    private InstantHandler instantHandler;

    @Override
    public void onEnable() {
        logger.info("InstantBattle InstantBattlePlugin enabled!");

        InitializeLibraries();

        instantHandler = new InstantHandler();
        instantHandler.start();
    }

    private static void InitializeLibraries() {
        try {
            ClassPathHack.addFile("./lib/commons-collections-3.2.1.jar");
            ClassPathHack.addFile("./lib/commons-dbutils-1.3.jar");
            ClassPathHack.addFile("./lib/commons-io-2.1.jar");
            ClassPathHack.addFile("./lib/jackson-core-2.0.5.jar");
            ClassPathHack.addFile("./lib/jackson-databind-2.0.5.jar");
            ClassPathHack.addFile("./lib/jackson-annotations-2.0.5.jar");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isOP = true;
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            isOP = player.isOp();
        }

        if (cmd.getName().equalsIgnoreCase("instant") && !ArrayUtils.isEmpty(args)) {
            String arg1 = args[0];
            String name = args.length > 1 ? args[1] : "";

            instantHandler.handlePlayerCommands(name, arg1, player);
        } else if (cmd.getName().equalsIgnoreCase("instant")) {
            Msg.sendMsg(player, "/instant list|join|leave|spec|unspec <battle>");
        } else if (cmd.getName().equalsIgnoreCase("instant")) {
            Msg.sendMsg(player, "/instant <battle> join|leave|specjoin|specleave");
        } else if (isOP && cmd.getName().equalsIgnoreCase("instantop") && args != null && args.length > 1) {
            String name = args[0];
            String arg1 = args[1];
            String arg2 = args.length > 2 ? args[2] : null;

            instantHandler.handleOpCommands(name, arg1, arg2, player);

        } else if (isOP && cmd.getName().equalsIgnoreCase("instantop")) {
            Msg.sendMsg(player, "/instantop <battle> pos1|pos2|posstart|posspec|duration|offset|stat|start|stop|kick|ban|addspawn|clearspawn");
        } else if (isOP && cmd.getName().equalsIgnoreCase("instantdebug")) {
            DEBUG = !DEBUG;
            Msg.sendMsg(player, "Debug is now: " + DEBUG);
        }

        return true;
    }

    @Override
    public void onDisable() {
        if (instantHandler != null) {
            instantHandler.disable();
        }
        logger.info("InstantBattle InstantBattlePlugin disabled!");
    }

    @Override
    public String toString() {
        return "InstantBattlePlugin{" +
                "instantHandler=" + instantHandler +
                '}';
    }
}

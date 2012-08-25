package ch.bukkit.playground;

import ch.bukkit.playground.instant.InstantHandler;
import ch.bukkit.playground.util.ClassPathHack;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Plugin extends JavaPlugin {


    public static final String PLUGIN_NAME = "BukkitPlayground";
    private static Logger logger = Logger.getLogger("Plugin");

    private InstantHandler instantHandler;

    @Override
    public void onEnable() {
        logger.info("Playground Plugin enabled!");

        InitializeLibraries();
        InitializeDataStructure();
        Tests();

        instantHandler = new InstantHandler();


    }

    private void Tests() {

    }

    private void InitializeLibraries() {
        try {
            ClassPathHack.addFile("./plugins/BukkitPlayground/commons-collections-3.2.1.jar");
            ClassPathHack.addFile("./plugins/BukkitPlayground/commons-dbutils-1.3.jar");
            ClassPathHack.addFile("./plugins/BukkitPlayground/commons-io-2.1.jar");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void InitializeDataStructure() {
        File file = new File("./plugins/BukkitPlayground");
        if(!file.exists()) {
            if(file.mkdir()) {
                logger.info("Plugin folder created!");
            }
        }

        file = new File("./plugins/BukkitPlayground/arenas.yml");
        if(!file.exists()) {
            try {
                FileUtils.touch(file);
                logger.info("Arenas file created!");
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Could not create arenas file", e);
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean isOP = player.isOp();

            if(cmd.getName().equalsIgnoreCase("instant") && ArrayUtils.contains(args, "join")){
                instantHandler.registerPlayer(player);
            } else if(cmd.getName().equalsIgnoreCase("instant") && ArrayUtils.contains(args, "leave")){
                instantHandler.unregisterPlayer(player);
            } else if(cmd.getName().equalsIgnoreCase("instant")) {
                player.sendMessage("/instant join|leave");
            } else if(cmd.getName().equalsIgnoreCase("instant") && ArrayUtils.contains(args, "specjoin")){
                instantHandler.specJoin(player);
            } else if(cmd.getName().equalsIgnoreCase("instant") && ArrayUtils.contains(args, "specleave")){
                instantHandler.specLeave(player);
            } else if(cmd.getName().equalsIgnoreCase("instant")) {
                player.sendMessage("/instant join|leave|specjoin|specleave");
            } else if(isOP && cmd.getName().equalsIgnoreCase("instantop") && args != null && args.length > 1 ){
                String name = args[0];
                String arg1 = args[1];
                String arg2 = args.length > 2 ? args[2] : null;

                instantHandler.handleArena(name, arg1, arg2, player);
            } else if(isOP && cmd.getName().equalsIgnoreCase("instantop")) {
                player.sendMessage("/instantop pos1|pos2|posstart|posspec|starttime|stat|restart|forcestop|kick|ban|addspawn|clearspawn");
            }
        }
        return false;
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

package ch.bukkit.playground.instant;

import ch.bukkit.playground.Plugin;
import ch.bukkit.playground.instant.model.ArenaConfiguration;
import ch.bukkit.playground.instant.model.ArenaData;
import ch.bukkit.playground.instant.tasks.ArenaHandlerTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the save/load operations for instant battles
 */
public class InstantConfig {

    private static Logger logger = Logger.getLogger("InstantConfig");

    private static Gson gson;
    private static File directory = new File("./plugins/BukkitPlayground");

    static  {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.disableHtmlEscaping();
        builder.serializeNulls();
        gson = builder.create();

        // Initialize data structure
        if(!directory.exists()) {
            if(directory.mkdir()) {
                logger.info("Plugin folder created!");
            }
        }
    }

    public static void saveArenaHandlerTask(ArenaHandlerTask arenaHandlerTask) {
        String config = gson.toJson(arenaHandlerTask.getArenaConfiguration());
        String data = gson.toJson(arenaHandlerTask.getArenaData());

        try {
            File configFile = new File(getArenaFile(arenaHandlerTask) + ".acon");
            File dataFile = new File(getArenaFile(arenaHandlerTask) + ".adat");
            FileUtils.writeStringToFile(configFile, config, Plugin.CHARSET.name());
            FileUtils.writeStringToFile(dataFile, data, Plugin.CHARSET.name());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while saving model.", e);
            logger.log(Level.SEVERE, config);
            logger.log(Level.SEVERE, data);
        }
    }

    public static ArenaHandlerTask loadArenaHandlerTask(String arenaName) {
        try {
            File configFile = new File(getArenaFile(new ArenaHandlerTask(arenaName)) + ".acon");
            File dataFile   = new File(getArenaFile(new ArenaHandlerTask(arenaName)) + ".adat");

            ArenaConfiguration configuration = gson.fromJson(FileUtils.readFileToString(configFile, Plugin.CHARSET.name()), ArenaConfiguration.class);
            ArenaData data = gson.fromJson(FileUtils.readFileToString(dataFile, Plugin.CHARSET.name()), ArenaData.class);

            // we found it and we can deliver it --> do it
            return new ArenaHandlerTask(arenaName, configuration, data);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while loading single model.", e);
        }

        // otherwise return null
        return null;
    }

    // Helper methods

    public static HashMap<String, ArenaHandlerTask> loadAllArenaHandlerTasks() {
        HashMap<String, ArenaHandlerTask> arenaHandlerTasks = new HashMap<String, ArenaHandlerTask>();

        Iterator<File> files = FileUtils.iterateFiles(directory, new AbstractFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().contains("acon");
            }
        }, null);

        while(files.hasNext()) {
            File configFile = files.next();
            String arenaName = configFile.getName().replace(".acon", "");
            File dataFile = new File(configFile.getParent() + "/" + arenaName + ".adat");
            try {
                if(dataFile.createNewFile()) {
                    logger.info("Created data file for arena: " + arenaName);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IO Exception while loading configuration and data", e);
            }

            try {
                ArenaConfiguration configuration = gson.fromJson(FileUtils.readFileToString(configFile, Plugin.CHARSET.name()), ArenaConfiguration.class);
                ArenaData data = gson.fromJson(FileUtils.readFileToString(configFile, Plugin.CHARSET.name()), ArenaData.class);
                ArenaHandlerTask arenaHandlerTask = new ArenaHandlerTask(arenaName, configuration, data);
                arenaHandlerTasks.put(arenaName, arenaHandlerTask);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while loading arenas.", e);
            }
        }

        return arenaHandlerTasks;
    }

    private static String getArenaFile(ArenaHandlerTask arenaHandlerTask) {
        return directory.getAbsolutePath() + "/" + arenaHandlerTask.getName();
    }
}

package ch.bukkit.playground.instant;

import ch.bukkit.playground.InstantBattlePlugin;
import ch.bukkit.playground.instant.model.BattleConfiguration;
import ch.bukkit.playground.instant.model.BattleData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the save/load operations for instant battles
 */
public class InstantConfig {

    private final static Logger logger = Logger.getLogger("InstantConfig");
    private final static String configFileType = ".bcon";
    private final static String dataFileType = ".bdat";
    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        forceCreate();
        StringWriter writer = new StringWriter();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static void forceCreate() {
        // Initialize data structure
        if (!InstantBattlePlugin.PLUGIN_DIRECTORY.exists()) {
            try {
                FileUtils.forceMkdir(InstantBattlePlugin.PLUGIN_DIRECTORY);
                logger.info("InstantBattle plugin folder created!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveBattleHandler(BattleHandler battleHandler) {
        forceCreate();

        try {
            File configFile = new File(getBattleFile(battleHandler) + configFileType);
            File dataFile = new File(getBattleFile(battleHandler) + dataFileType);

            mapper.writeValue(configFile, battleHandler.getBattleConfiguration());
            mapper.writeValue(dataFile, battleHandler.getBattleData());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while saving model.", e);
            try {
                logger.log(Level.SEVERE, "Configuration: \n\n" + mapper.writeValueAsString(battleHandler.getBattleConfiguration()));
                logger.log(Level.SEVERE, "Data: \n\n" + mapper.writeValueAsString(battleHandler.getBattleData()));
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "CANNOT EVENT WRITE THE FILES", ex);
            }
        }
    }

    public static HashMap<String, BattleHandler> loadBattleHandlers() {
        forceCreate();

        HashMap<String, BattleHandler> battleHandlers = new HashMap<String, BattleHandler>();

        if (!InstantBattlePlugin.PLUGIN_DIRECTORY.exists()) {
            return battleHandlers;
        }

        Iterator<File> files = FileUtils.iterateFiles(InstantBattlePlugin.PLUGIN_DIRECTORY, new AbstractFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().contains(configFileType);
            }
        }, null);

        logger.info("Loading battles.");
        while (files.hasNext()) {
            File configFile = files.next();
            String battleName = configFile.getName().replace(configFileType, "");
            File dataFile = new File(configFile.getParent() + "/" + battleName + dataFileType);
            try {
                if (dataFile.createNewFile()) {
                    logger.info("Created data file for battle: " + battleName);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IO Exception while loading configuration and data files.", e);
            }

            try {
                BattleConfiguration configuration = mapper.readValue(configFile, BattleConfiguration.class); //gson.fromJson(FileUtils.readFileToString(configFile, InstantBattlePlugin.CHARSET.name()), BattleConfiguration.class);
                BattleData data = mapper.readValue(dataFile, BattleData.class); //gson.fromJson(FileUtils.readFileToString(configFile, InstantBattlePlugin.CHARSET.name()), BattleData.class);
                BattleHandler battleHandler = new BattleHandler(battleName, configuration, data);
                battleHandlers.put(battleName, battleHandler);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while loading battles.", e);
            }
        }

        return battleHandlers;
    }

    private static String getBattleFile(BattleHandler battleHandler) {
        return InstantBattlePlugin.PLUGIN_DIRECTORY.getAbsolutePath() + "/" + battleHandler.getName();
    }
}

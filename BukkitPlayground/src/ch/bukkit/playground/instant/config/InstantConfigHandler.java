package ch.bukkit.playground.instant.config;

import ch.bukkit.playground.Plugin;
import ch.bukkit.playground.instant.arena.Arena;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the save/load operations for instant battles
 */
public class InstantConfigHandler {

    private static Logger logger = Logger.getLogger("InstantConfigHandler");

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

    public static void saveArena(Arena arena) {
        String config = gson.toJson(arena);
        try {
            FileUtils.writeStringToFile(getArenaFile(arena), config, Plugin.CHARSET.name());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while saving arena.", e);
        }
    }

    public static Arena loadArena(Arena arena) {
        try {
            arena = gson.fromJson(FileUtils.readFileToString(getArenaFile(arena), Plugin.CHARSET.name()), Arena.class);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while loading single arena.", e);
        }
        return arena;
    }

    public static List<Arena> loadAllArenas() {
        List<Arena> arenas = new LinkedList<Arena>();

        Iterator<File> files = FileUtils.iterateFiles(directory, new AbstractFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().contains("acon");
            }
        }, null);

        while(files.hasNext()) {
            File file = files.next();
            try {
                arenas.add(gson.fromJson(FileUtils.readFileToString(file, Plugin.CHARSET.name()), Arena.class));
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while loading arenas.", e);
            }
        }

        return arenas;
    }

    // Helper methods

    private static File getArenaFile(Arena arena) {
        return new File(directory.getAbsolutePath() + "/" + arena.getName() + ".acon");
    }
}

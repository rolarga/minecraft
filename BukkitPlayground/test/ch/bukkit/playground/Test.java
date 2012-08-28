package ch.bukkit.playground;

import ch.bukkit.playground.instant.model.Arena;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

public class Test {

    public static void main(String[] args) throws IOException {

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.disableHtmlEscaping();
        builder.serializeNulls();
        Gson gson = builder.create();

        File directory = new File("C:/Users/sey.CORESYSTEMSAG/Desktop/craftbukkit/plugins/BukkitPlayground");

        Iterator<File> files = FileUtils.iterateFiles(directory, new AbstractFileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().contains("acon");
            }
        }, null);

        while(files.hasNext()) {
            File file = files.next();

            Arena ar = gson.fromJson(FileUtils.readFileToString(file, Plugin.CHARSET.name()), Arena.class);
            System.out.println(ar);
        }

        Arena arena = new Arena();
        arena.setPosSpectator(new Location(null, 0, 0, 0));
        arena.setEndDate(new Date());
        arena.setName("model");
        arena.setPos1(new Location(null, 1, 0, 0));
        arena.setPos2(new Location(null, 2, 0, 0));
        arena.setPosStart(new Location(null, 3, 0, 0));
        arena.setTime(0);
        arena.addBlockedPlayer("blocked2", "blocked by admin");

        Arena arena2 = new Arena(arena);
        arena2.setName("arena2");

        String config = gson.toJson(arena);
        File arenaFile = new File(directory.getAbsolutePath() + "/" + arena.getName() + ".acon");
        FileUtils.writeStringToFile(arenaFile, config, Plugin.CHARSET.name());
    }
}


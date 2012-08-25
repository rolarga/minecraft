package ch.bukkit.playground;

import ch.bukkit.playground.instant.arena.Arena;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Test {

    public static void main(String[] args) throws IOException {

        File file = new File("C:\\Users\\sey.CORESYSTEMSAG\\Desktop\\craftbukkit\\plugins\\BukkitPlayground/arenas.yml");

//        Arena arena = new Arena();
//        arena.setPosSpectator(new Location(null, 0, 0, 0));
//        arena.setEndDate(new Date());
//        arena.setName("arena");
//        arena.setPos1(new Location(null, 1, 0, 0));
//        arena.setPos2(new Location(null, 2, 0, 0));
//        arena.setPosStart(new Location(null, 3, 0, 0));
//        arena.setTime(0);
//        arena.addBlockedPlayer("blocked1", "block");
//        arena.addBlockedPlayer("blocked2", "blocked by admin");
//
//        List<Arena> arenas = new LinkedList<Arena>();
//        arenas.add(arena);
//        arenas.add(new Arena(arena));
//        config.set("arenas", arenas);
//
//        System.out.println(config.saveToString());
//        config.save(file);

        FileConfiguration configLoaded = YamlConfiguration.loadConfiguration(file);
        List<Arena> arenas = new LinkedList<Arena>();
        configLoaded.get("arenas", arenas);
        System.out.println(arenas);
    }
}


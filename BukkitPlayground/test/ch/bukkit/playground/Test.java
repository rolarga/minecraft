package ch.bukkit.playground;

import ch.bukkit.playground.instant.arena.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(new File("C:\\Users\\sey.CORESYSTEMSAG\\Desktop\\craftbukkit\\plugins\\BukkitPlayground/arenas.yml"));

        Arena arena = new Arena();
        arena.setPosSpectator(new Location(null, 0, 0, 0));
        arena.setDate(new Date());
        arena.setEndDate(new Date());
        arena.setHeight(100);
        arena.setName("arena");
        arena.setPos1(new Location(null, 1, 0, 0));
        arena.setPos2(new Location(null, 2, 0, 0));
        arena.setPosStart(new Location(null, 3, 0, 0));
        arena.setTime(0);
        arena.addBlockedPlayer("blocked1");
        arena.addBlockedPlayer("blocked2");
        arena.addVipPlayer("vip1");
        arena.addVipPlayer("vip2");

        List<Arena> arenas = new LinkedList<Arena>();
        arenas.add(arena);
        arenas.add(new Arena(arena));
        config.set("arenas", arenas);

        System.out.println(config.saveToString());
    }
}


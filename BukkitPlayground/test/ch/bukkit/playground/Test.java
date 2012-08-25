package ch.bukkit.playground;

import ch.bukkit.playground.instant.arena.Arena;
import org.bukkit.Location;

import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        Location location1 = new Location(null, -33.30000001192093, 69.0, 605.6999999880791);
        Location location2 = new Location(null, -57.83001789847776, 77.0, 584.1374060721122);
        double height = location2.getZ() - location1.getZ();

        Arena arena = new Arena();
        arena.setHeight(height);
        arena.setPos1(location1);
        arena.setPos2(location2);
        
        Location loc = new Location(null, -51.88925556595144, 71.44598714373386, 591.6675811626664);

        System.out.println(arena.isInArena(loc));

        double arenaBottom = Math.min(location1.getX(), location2.getX());
        double arenaTop = Math.max(location1.getX(), location2.getX());
        double arenaLeft = Math.min(location1.getY(), location2.getY());
        double arenaRight = Math.max(location1.getY(), location2.getY());
        double arenaGround = Math.max(location1.getZ(), location2.getZ());
        double arenaHeight = arenaGround + height;
        double playerX = loc.getX();
        double playerY = loc.getY();
        double playerZ = loc.getZ();

        System.out.println(arenaBottom);
        System.out.println(arenaTop);
        System.out.println(arenaLeft);
        System.out.println(arenaRight);
        System.out.println(arenaGround);
        System.out.println(arenaHeight);
        System.out.println(playerX);
        System.out.println(playerY);
        System.out.println(playerZ);
        
//        File file = new File("C:\\Users\\sey.CORESYSTEMSAG\\Desktop\\craftbukkit\\plugins\\BukkitPlayground/arenas.yml");
//        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
//
//        Arena arena = new Arena();
//        arena.setPosSpectator(new Location(null, 0, 0, 0));
//        arena.setEndDate(new Date());
//        arena.setHeight(100);
//        arena.setName("arena");
//        arena.setPos1(new Location(null, 1, 0, 0));
//        arena.setPos2(new Location(null, 2, 0, 0));
//        arena.setPosStart(new Location(null, 3, 0, 0));
//        arena.setTime(0);
//        arena.addBlockedPlayer("blocked1");
//        arena.addBlockedPlayer("blocked2");
//        arena.addVipPlayer("vip1");
//        arena.addVipPlayer("vip2");
//
//        List<Arena> arenas = new LinkedList<Arena>();
//        arenas.add(arena);
//        arenas.add(new Arena(arena));
//        config.set("arenas", arenas);
//
//        System.out.println(config.saveToString());
//        config.save(file);
//
//        FileConfiguration configLoaded = YamlConfiguration.loadConfiguration(file);
//        arenas = new LinkedList<Arena>();
//        configLoaded.get("arenas", arenas);
//        System.out.println(arenas);
    }
}


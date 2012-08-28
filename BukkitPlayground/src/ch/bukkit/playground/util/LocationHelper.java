package ch.bukkit.playground.util;

import org.bukkit.Location;

import java.util.logging.Logger;

public class LocationHelper {

    private static Logger logger = Logger.getLogger("LocationHelper");

    public static boolean isInSquare(Location pos1, Location pos2, Location loc) {
        double squareBottom = Math.min(pos1.getX(), pos2.getX());
        double squareTop = Math.max(pos1.getX(), pos2.getX());
        double squareLeft = Math.min(pos1.getZ(), pos2.getZ());
        double squareRight = Math.max(pos1.getZ(), pos2.getZ());

        // return true if loc is in square built by pos1, pos2
        return !(loc.getX() < squareBottom || loc.getX() > squareTop ||
                loc.getZ() < squareLeft || loc.getZ() > squareRight);
    }
}

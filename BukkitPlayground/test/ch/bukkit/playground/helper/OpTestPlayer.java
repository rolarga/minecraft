package ch.bukkit.playground.helper;

import org.bukkit.Location;

public class OpTestPlayer extends TestPlayer {

    public OpTestPlayer(String name, int level) {
        super(name, level);
    }

    public OpTestPlayer(String name, int level, Location location) {
        super(name, level, location);
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void giveExpLevels(int i) {

    }

    @Override
    public void setBedSpawnLocation(Location location, boolean b) {

    }
}

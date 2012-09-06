package ch.bukkit.playground.helper;

import org.bukkit.Location;

public class VipTestPlayer extends TestPlayer {

    public VipTestPlayer(String name, int level) {
        super(name, level);
    }

    public VipTestPlayer(String viptester, int level, Location location) {
        super(viptester, level, location);
    }

    @Override
    public boolean hasPermission(String name) {
        return "instant.vip".equals(name);
    }
}

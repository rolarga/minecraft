package ch.bukkit.playground.helper;

public class VipTestPlayer extends TestPlayer {

    public VipTestPlayer(String name, int level) {
        super(name, level);
    }

    @Override
    public boolean hasPermission(String name) {
        return "instant.vip".equals(name);
    }
}

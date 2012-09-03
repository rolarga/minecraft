package ch.bukkit.playground.helper;

public class OpTestPlayer extends TestPlayer {

    public OpTestPlayer(String name, int level) {
        super(name, level);
    }

    @Override
    public boolean isOp() {
        return true;
    }
}

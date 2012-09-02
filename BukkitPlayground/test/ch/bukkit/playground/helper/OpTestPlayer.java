package ch.bukkit.playground.helper;

public class OpTestPlayer extends TestPlayer {

    public OpTestPlayer(String name) {
        super(name);
    }

    @Override
    public boolean isOp() {
        return true;
    }
}

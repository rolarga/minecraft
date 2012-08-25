package ch.bukkit.playground.instant.tasks;

import ch.bukkit.playground.instant.arena.Arena;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.TimerTask;

public class AllMessageTask extends TimerTask {

    private Arena arena;
    private String message;

    public AllMessageTask(Arena arena, String message) {
        this.message = message;
        this.arena = arena;
    }

    @Override
    public void run() {
        // replacements
        List<Player> players = arena.getPos1().getWorld().getPlayers();
        message = message.replace("%players%", arena.getRegisteredPlayers().size() + "");
        for (Player player : players) {
            player.sendMessage(message);
        }
    }
}

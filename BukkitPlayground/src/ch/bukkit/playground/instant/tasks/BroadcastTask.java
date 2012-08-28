package ch.bukkit.playground.instant.tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

public class BroadcastTask extends TimerTask {

    private Set<Player> players;
    private String message;
    private World world;

    public BroadcastTask(Set<Player> players, String message) {
        this.message = message;
        this.players = players;
    }

    public BroadcastTask(String message) {
        this.message = message;
        this.players = new HashSet<Player>(0);
    }

    @Override
    public void run() {
        // replacements
        message = message.replace("%players%", players.size() + "");
        Bukkit.broadcastMessage(message);
    }
}

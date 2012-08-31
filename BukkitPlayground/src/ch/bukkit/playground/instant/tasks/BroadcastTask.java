package ch.bukkit.playground.instant.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.TimerTask;

public class BroadcastTask extends TimerTask {

    private Collection<Player> players;
    private String message;

    public BroadcastTask(Collection<Player> players, String message) {
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

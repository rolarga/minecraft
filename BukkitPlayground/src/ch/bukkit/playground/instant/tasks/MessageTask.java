package ch.bukkit.playground.instant.tasks;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

public class MessageTask extends TimerTask {

    private List<Player> players;
    private String message;

    public MessageTask(List<Player> players, String message) {
        this.players = players;
        this.message = message;
    }

    public MessageTask(Collection<Player> values, String message) {
        this(new LinkedList<Player>(values), message);
    }

    @Override
    public void run() {
        // replacements
        message = message.replace("%players%", players.size() + "");

        for (Player player : players) {
            player.sendMessage(message);
        }
    }
}

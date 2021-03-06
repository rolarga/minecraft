package ch.bukkit.playground.instant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Level implements Validataeble {

    private final static Logger logger = Logger.getLogger("Level");

    private List<Round> rounds = new LinkedList<Round>();
    private String welcomeMessage = "Welcome to a new level!";

    public Level() {
    }

    public Level(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    @JsonIgnore
    public double getRoundQuantity() {
        return (double) rounds.size();
    }

    public void addRound(Round round) {
        rounds.add(round);
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    @Override
    public boolean checkValidity() {
        for (Round round : rounds) {
            if (!round.checkValidity()) {
                logger.warning("Found invalid round " + round);
                return false;
            }
        }

        return true;
    }
}

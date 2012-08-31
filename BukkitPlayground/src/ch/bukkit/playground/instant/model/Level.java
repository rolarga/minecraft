package ch.bukkit.playground.instant.model;

import java.util.LinkedList;
import java.util.List;

public class Level implements Validataeble {

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
    public boolean isValid() {
        for (Round round : rounds) {
            if(!round.isValid()) return false;
        }

        return true;
    }
}

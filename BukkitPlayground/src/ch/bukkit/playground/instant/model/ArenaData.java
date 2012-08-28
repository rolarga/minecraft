package ch.bukkit.playground.instant.model;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class ArenaData {

    private Set<Player> registeredPlayers = new HashSet<Player>();
    private HashMap<Player, Location> activePlayers = new HashMap<Player, Location>();
    private Map<String, String> blockedPlayers = new HashMap<String, String>();
    private List<Entity> spawnedMobs = new LinkedList<Entity>();
    private HashMap<Player, Location> originSpectatorLocations = new HashMap<Player, Location>();
    private Map<TimerTask, Date> tasks = new HashMap<TimerTask, Date>();
    private int totalActivePlayers;

    public Set<Player> getRegisteredPlayers() {
        return registeredPlayers;
    }

    public void setRegisteredPlayers(Set<Player> registeredPlayers) {
        this.registeredPlayers = registeredPlayers;
    }

    public HashMap<Player, Location> getActivePlayers() {
        return activePlayers;
    }

    public void setActivePlayers(HashMap<Player, Location> activePlayers) {
        this.activePlayers = activePlayers;
    }

    public Map<String, String> getBlockedPlayers() {
        return blockedPlayers;
    }

    public void setBlockedPlayers(Map<String, String> blockedPlayers) {
        this.blockedPlayers = blockedPlayers;
    }

    public List<Entity> getSpawnedMobs() {
        return spawnedMobs;
    }

    public void setSpawnedMobs(List<Entity> spawnedMobs) {
        this.spawnedMobs = spawnedMobs;
    }

    public HashMap<Player, Location> getOriginSpectatorLocations() {
        return originSpectatorLocations;
    }

    public void setOriginSpectatorLocations(HashMap<Player, Location> originSpectatorLocations) {
        this.originSpectatorLocations = originSpectatorLocations;
    }

    public Map<TimerTask, Date> getTasks() {
        return tasks;
    }

    public void setTasks(Map<TimerTask, Date> tasks) {
        this.tasks = tasks;
    }

    // Helper methods

    public boolean addRegisteredPlayer(Player player) {
        if(blockedPlayers.containsKey(player.getName())) {
            return false;
        }
        registeredPlayers.add(player);
        return true;
    }

    public Player unregisterPlayer(Player player) {
        if(registeredPlayers.remove(player)) {
            return player;
        }
        return null;
    }

    public void addSpecator(Player player, Location loc) {
        originSpectatorLocations.put(player, loc);
    }

    public void addSpawnedMob(Entity e) {
        spawnedMobs.add(e);
    }
    public void addActivePlayer(Player player, Location loc) {
        activePlayers.put(player, loc);
    }

    public void addTask(TimerTask task, Date time) {
        tasks.put(task, time);
    }

    public void addBlockedPlayer(String playerName, String reason) {
        blockedPlayers.put(playerName, reason);
    }

    public int getTotalActivePlayers() {
        return totalActivePlayers;
    }

    public void setTotalActivePlayers(int totalActivePlayers) {
        this.totalActivePlayers = totalActivePlayers;
    }
}

package ch.bukkit.playground.instant.arena;

import ch.bukkit.playground.util.DateFormatter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class Arena {

    private Location pos1;
    private Location pos2;
    private int height;
    private String name;
    private int time;
    private Location posStart;
    private Date date;
    private Date endDate;
    private Location posSpectator;

    private HashMap<String, Player> registeredPlayers = new HashMap<String, Player>();
    private HashMap<Player, Location> activePlayers = new HashMap<Player, Location>();
    private Set<String> blockedPlayers = new HashSet<String>();
    private Set<String> vipPlayers = new HashSet<String>();
    private List<Entity> spawnedMobs = new LinkedList<Entity>();

    public Arena() {
    }

    public Arena(Arena arena) {
        this.pos1 = arena.pos1;
        this.pos2 = arena.pos2;
        this.height = arena.height;
        this.name = arena.name;
        this.time = arena.time;
        this.posStart = arena.posStart;
        this.date = arena.date;
        this.endDate = arena.endDate;
        this.posSpectator = arena.posSpectator;
        this.registeredPlayers = new HashMap<String, Player>(arena.registeredPlayers);
        this.activePlayers = new HashMap<Player, Location>(arena.activePlayers);
        this.blockedPlayers = new HashSet<String>(arena.blockedPlayers);
        this.vipPlayers = new HashSet<String>(arena.vipPlayers);
        this.spawnedMobs = new LinkedList<Entity>(arena.spawnedMobs);
    }

    public Location getPos1() {
        return pos1;
    }

    public void setPos1(Location pos1) {
        this.pos1 = pos1;
    }

    public Location getPos2() {
        return pos2;
    }

    public void setPos2(Location pos2) {
        this.pos2 = pos2;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Location getPosStart() {
        return posStart;
    }

    public void setPosStart(Location posStart) {
        this.posStart = posStart;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Location getPosSpectator() {
        return posSpectator;
    }

    public void setPosSpectator(Location posSpectator) {
        this.posSpectator = posSpectator;
    }

    public String getTimeString() {
        return DateFormatter.format(date);
    }

    public HashMap<String, Player> getRegisteredPlayers() {
        return registeredPlayers;
    }

    public Set<String> getBlockedPlayers() {
        return blockedPlayers;
    }

    public void addBlockedPlayer(String blockedPlayer) {
        blockedPlayers.add(blockedPlayer);
    }

    public Set<String> getVipPlayers() {
        return blockedPlayers;
    }

    public void addVipPlayer(String vipPlayer) {
        blockedPlayers.add(vipPlayer);
    }

    public boolean addRegisteredPlayer(Player player) {
        if(blockedPlayers.contains(player.getName())) {
            return false;
        }
        registeredPlayers.put(player.getName(), player);
        return true;
    }

    public Player unregisterPlayer(Player player) {
        return registeredPlayers.remove(player.getName());
    }

    public void addSpawnedMob(Entity e) {
        spawnedMobs.add(e);
    }

    public List<Entity> getSpawnedMobs() {
        return spawnedMobs;
    }

    public void addActivePlayer(Player player) {
        activePlayers.put(player, player.getLocation());
    }

    public HashMap<Player, Location> getActivePlayers() {
        return activePlayers;
    }

    public void setRegisteredPlayers(HashMap<String, Player> registeredPlayers) {
        this.registeredPlayers = registeredPlayers;
    }

    public void setActivePlayers(HashMap<Player, Location> activePlayers) {
        this.activePlayers = activePlayers;
    }

    public void setBlockedPlayers(Set<String> blockedPlayers) {
        this.blockedPlayers = blockedPlayers;
    }

    public void setVipPlayers(Set<String> vipPlayers) {
        this.vipPlayers = vipPlayers;
    }
}

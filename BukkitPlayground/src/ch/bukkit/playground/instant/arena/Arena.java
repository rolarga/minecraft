package ch.bukkit.playground.instant.arena;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

public class Arena {

    private Location pos1;
    private Location pos2;
    private String name;
    private int time;
    private Location posStart;
    private Date endDate;
    private Location posSpectator;

    private HashMap<String, Player> registeredPlayers = new HashMap<String, Player>();
    private HashMap<Player, Location> activePlayers = new HashMap<Player, Location>();
    private Set<String> blockedPlayers = new HashSet<String>();
    private Set<String> vipPlayers = new HashSet<String>();
    private List<Entity> spawnedMobs = new LinkedList<Entity>();

    private static Logger logger = Logger.getLogger("Arena");

    public Arena() {
    }

    public Arena(Arena arena) {
        this.pos1 = arena.pos1;
        this.pos2 = arena.pos2;
        this.name = arena.name;
        this.time = arena.time;
        this.posStart = arena.posStart;
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

    public void addActivePlayer(Player player, Location loc) {
        activePlayers.put(player, loc);
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

    public boolean isInArena(Location loc) {
        double arenaBottom = Math.min(getPos1().getX(), getPos2().getX());
        double arenaTop = Math.max(getPos1().getX(), getPos2().getX());
        double arenaLeft = Math.min(getPos1().getZ(), getPos2().getZ());
        double arenaRight = Math.max(getPos1().getZ(), getPos2().getZ());
        double playerX = loc.getX();
        double playerZ = loc.getZ();

        logger.info("Move event of player catched X: " + playerX + " Z: " + playerZ);

        // return true if the player is in the arena by that movement
        return !(playerX < arenaBottom || playerX > arenaTop ||
                playerZ < arenaLeft || playerZ > arenaRight);
    }

}

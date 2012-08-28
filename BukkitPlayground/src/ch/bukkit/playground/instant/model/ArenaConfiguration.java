package ch.bukkit.playground.instant.model;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class ArenaConfiguration {

    private Location pos1;
    private Location pos2;
    private int offset;
    private int duration;
    private Date endDate;
    private Location posStart;
    private Location posSpectator;
    private List<Location> spanws = new LinkedList<Location>();
    private Set<Reward> rewards = new HashSet<Reward>();

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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Location getPosStart() {
        return posStart;
    }

    public void setPosStart(Location posStart) {
        this.posStart = posStart;
    }

    public Location getPosSpectator() {
        return posSpectator;
    }

    public void setPosSpectator(Location posSpectator) {
        this.posSpectator = posSpectator;
    }

    public List<Location> getSpanws() {
        return spanws;
    }

    public void setSpanws(List<Location> spanws) {
        this.spanws = spanws;
    }

    public Set<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(Set<Reward> rewards) {
        this.rewards = rewards;
    }

    /**
     * @return compelete duration of an arena run + 1 minute to ensure small break and minimum duration
     */
    public int getCompleteRunDuration() {
        return 1 + offset + duration;
    }

    public void addSpawn(Location location) {
        spanws.add(location);
    }

    public World getWorld() {
        return pos1.getWorld();
    }
}

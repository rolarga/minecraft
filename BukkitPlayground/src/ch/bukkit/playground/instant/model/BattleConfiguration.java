package ch.bukkit.playground.instant.model;

import ch.bukkit.playground.instant.model.serializer.LocationList;
import ch.bukkit.playground.instant.model.serializer.Locations;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;

import java.util.LinkedList;
import java.util.List;

public class BattleConfiguration implements Validataeble {

    private boolean autostart = false;
    private int offset = 5;
    private int duration = 30;
    private BattleType battleType = BattleType.COOP;

    private int groupAmount = 1;

    @JsonSerialize(using = Locations.Serializer.class)
    @JsonDeserialize(using = Locations.Deserializer.class)
    private Location pos1;

    @JsonSerialize(using = Locations.Serializer.class)
    @JsonDeserialize(using = Locations.Deserializer.class)
    private Location pos2;

    @JsonSerialize(using = Locations.Serializer.class)
    @JsonDeserialize(using = Locations.Deserializer.class)
    private Location posStart;

    @JsonSerialize(using = Locations.Serializer.class)
    @JsonDeserialize(using = Locations.Deserializer.class)
    private Location posSpectator;

    @JsonSerialize(using = LocationList.Serializer.class)
    @JsonDeserialize(using = LocationList.Deserializer.class)
    private List<Location> spanws = new LinkedList<Location>();

    private List<Level> levels = new LinkedList<Level>();

    public BattleConfiguration() {
        if (CollectionUtils.isEmpty(levels)) {
            Level level1 = new Level("Welcome to level 1!");
            level1.addRound(getDefaultRound(262, 1));
            level1.addRound(getDefaultRound(262, 2));
            level1.addRound(getDefaultRound(262, 3));
            level1.addRound(getDefaultRound(360, 4));

            Level level2 = new Level("You reached level 2 - not bad.");
            level2.addRound(getDefaultRound(352, 5));
            level2.addRound(getDefaultRound(352, 6));
            level2.addRound(getDefaultRound(352, 7));
            level2.addRound(getDefaultRound(360, 8));

            Level level3 = new Level("You reached the bossfight - seems you guys are pretty good, arent you? have fun!");
            level3.addRound(getDefaultRound(354, 9));
            level3.addRound(getDefaultRound(354, 10));
            level3.addRound(getDefaultRound(354, 11));
            level3.addRound(getDefaultRound(360, 12));

            levels.add(level1);
            levels.add(level2);
            levels.add(level3);
        }
    }

    public boolean isAutostart() {
        return autostart;
    }

    public void setAutostart(boolean autostart) {
        this.autostart = autostart;
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

    public List<Level> getLevels() {
        return levels;
    }

    public void setLevels(List<Level> levels) {
        this.levels = levels;
    }

    public BattleType getBattleType() {
        return battleType;
    }

    public void setBattleType(BattleType battleType) {
        this.battleType = battleType;
    }

    public int getGroupAmount() {
        return groupAmount;
    }

    public void setGroupAmount(int groupAmount) {
        this.groupAmount = groupAmount;
    }

    /**
     * @return compelete duration of an battle run + 1 minute to ensure small break and minimum duration
     */
    @JsonIgnore
    public int getCompleteRunDuration() {
        return 1 + offset + duration;
    }

    public void addSpawn(Location location) {
        spanws.add(location);
    }

    @JsonIgnore
    public World getWorld() {
        return pos1 != null ? pos1.getWorld() : null;
    }

    @JsonIgnore
    public double getTotalRounds() {
        double totalRounds = 0.;
        for (Level level : levels) {
            totalRounds += level.getRoundQuantity();
        }
        return totalRounds;
    }

    // helper method to get round

    @JsonIgnore
    private Round getDefaultRound(int itemId, int factor) {
        Round round1 = new Round();
        round1.addMob(Zombie.class.getSimpleName(), 2 * factor);
        round1.addMob(Golem.class.getSimpleName(), 2 * factor);
        round1.addMob(Spider.class.getSimpleName(), 2 * factor);
        round1.addReward(itemId, 2 * factor);
        return round1;
    }

    @Override
    public boolean checkValidity() {
        if (battleType == BattleType.COOP) {
            for (Level level : levels) {
                if (!level.checkValidity()) return false;
            }
        }

        return pos1 != null &&
                pos2 != null &&
                posStart != null &&
                posSpectator != null &&
                offset > 0. &&
                duration > 0. &&
                (battleType != BattleType.COOP || levels.size() > 0);
    }
}

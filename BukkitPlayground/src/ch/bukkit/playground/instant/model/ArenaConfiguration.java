package ch.bukkit.playground.instant.model;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;

import java.util.LinkedList;
import java.util.List;

public class ArenaConfiguration implements Validataeble {

    private Location pos1;
    private Location pos2;
    private int offset = 5;
    private int duration = 30;
    private Location posStart;
    private Location posSpectator;
    private int groups = 1;
    private List<Location> spanws = new LinkedList<Location>();
    private List<Level> levels = new LinkedList<Level>();
    private BattleType battleType = BattleType.COOP;

    public ArenaConfiguration() {
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

    public int getGroups() {
        return groups;
    }

    public void setGroups(int groups) {
        this.groups = groups;
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

    public double getTotalRounds() {
        double totalRounds = 0.;
        for (Level level : levels) {
            totalRounds += level.getRoundQuantity();
        }
        return totalRounds;
    }

    // helper method to get round

    private Round getDefaultRound(int itemId, int factor) {
        Round round1 = new Round();
        round1.addMob(Zombie.class, 2 * factor);
        round1.addMob(Golem.class, 2 * factor);
        round1.addMob(Spider.class, 2 * factor);
        round1.addReward(itemId, 2 * factor);
        return round1;
    }

    @Override
    public boolean isValid() {
        if (battleType == BattleType.COOP) {
            for (Level level : levels) {
                if (!level.isValid()) return false;
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

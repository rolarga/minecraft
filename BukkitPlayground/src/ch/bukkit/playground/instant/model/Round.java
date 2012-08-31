package ch.bukkit.playground.instant.model;

import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Round implements Validataeble {

    private Set<Reward> rewards = new HashSet<Reward>();
    private Map<Class<? extends Entity>, Integer> mobs = new HashMap<Class<? extends Entity>, Integer>();

    public Set<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(Set<Reward> rewards) {
        this.rewards = rewards;
    }

    public Map<Class<? extends Entity>, Integer> getMobs() {
        return mobs;
    }

    public void setMobs(Map<Class<? extends Entity>, Integer> mobs) {
        this.mobs = mobs;
    }

    public void addMob(Class<? extends Entity> entity, int i) {
        mobs.put(entity, i);
    }

    public void addReward(int itemId, int quantity) {
        rewards.add(new Reward(itemId, quantity));
    }

    @Override
    public boolean isValid() {
        for (Reward reward : rewards) {
            if(!reward.isValid()) return false;
        }

        return mobs.size() > 0 && rewards.size() > 0;
    }
}

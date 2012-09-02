package ch.bukkit.playground.instant.model;

import ch.bukkit.playground.util.EntityHelper;
import org.bukkit.entity.Entity;

import java.util.*;

public class Round implements Validataeble {

    private Set<Reward> rewards = new HashSet<Reward>();
    private Map<String, Integer> mobs = new HashMap<String, Integer>();

    public Set<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(Set<Reward> rewards) {
        this.rewards = rewards;
    }

    public Map<String, Integer> getMobs() {
        return mobs;
    }

    public void setMobs(Map<String, Integer> mobs) {
        this.mobs = mobs;
    }

    public void addMob(String entity, int i) {
        Class<Entity> entityClass = EntityHelper.getEntityClassForString(entity);
        if (entityClass != null) {
            mobs.put(entity, i);
        }
    }

    public void addReward(int itemId, int quantity) {
        rewards.add(new Reward(itemId, quantity));
    }

    @Override
    public boolean isValid() {
        for (Reward reward : rewards) {
            if (!reward.isValid()) return false;
        }

        // we do not want to fail becuase just one name was wrong
        // but we do remove it and if there are no more of the - an error is thrown.
        List<String> mobNames = new LinkedList<String>(mobs.keySet());
        for (String entity : mobNames) {
            EntityHelper.getEntityClassForString(entity);
            mobs.remove(entity);
        }

        return mobs.size() > 0 && rewards.size() > 0;
    }
}

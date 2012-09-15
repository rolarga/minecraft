package ch.bukkit.playground.instant.model;

import ch.bukkit.playground.util.EntityHelper;
import org.bukkit.entity.LivingEntity;

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
        Class<LivingEntity> entityClass = EntityHelper.getLivingEntityClassForName(entity);
        if (entityClass != null) {
            mobs.put(entity, i);
        }
    }

    public void addReward(int itemId, int quantity) {
        rewards.add(new Reward(itemId, quantity));
    }

    @Override
    public boolean checkValidity() {
        for (Reward reward : rewards) {
            if (!reward.checkValidity()) return false;
        }

        // we do not want to fail becuase just one name was wrong
        // but we do remove it and if there are no more of the - an error is thrown.
        List<String> mobNames = new LinkedList<String>(mobs.keySet());
        for (String entity : mobNames) {
            Class<LivingEntity> entityClass = EntityHelper.getLivingEntityClassForName(entity);
            if (entityClass == null) {
                mobs.remove(entity);
            }
        }

        return mobs.size() > 0 && rewards.size() > 0;
    }

    @Override
    public String toString() {
        return "Round{" +
                "rewards=" + rewards +
                ", mobs=" + mobs +
                '}';
    }
}

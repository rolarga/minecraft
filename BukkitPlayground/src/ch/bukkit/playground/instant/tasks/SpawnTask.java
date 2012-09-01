package ch.bukkit.playground.instant.tasks;

import ch.bukkit.playground.instant.model.BattleConfiguration;
import ch.bukkit.playground.instant.model.BattleData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.TimerTask;

public class SpawnTask extends TimerTask {

    private BattleConfiguration battleConfiguration;
    private BattleData battleData;
    private Class<? extends Entity> entity;
    private int round;

    public SpawnTask(BattleConfiguration battleConfiguration, BattleData battleData, Class<? extends Entity> entity, int round) {
        this.battleConfiguration = battleConfiguration;
        this.battleData = battleData;
        this.entity = entity;
        this.round = round;
    }

    @Override
    public void run() {
        int quantity = battleData.getTotalActivePlayers() * round;
        for (int i = 0; i < quantity; i++) {
            int rnd = ((int) (Math.random() * 1000)) % battleConfiguration.getSpanws().size();
            Location loc = battleConfiguration.getSpanws().get(rnd);
            if (loc == null) {
                loc = battleConfiguration.getPosStart();
            }
            Entity e = battleConfiguration.getWorld().spawn(loc, entity);
            battleData.addSpawnedMob(e);
        }

        MessageTask messageTask = new MessageTask(battleData.getActivePlayers().keySet(), ChatColor.RED + "Spawned " + quantity + " " + entity.getSimpleName() + " monsters. Have fun killing them.");
        messageTask.run();
    }
}

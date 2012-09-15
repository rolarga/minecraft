package ch.bukkit.playground.instant.tasks;

import ch.bukkit.playground.instant.model.BattleConfiguration;
import ch.bukkit.playground.instant.model.BattleData;
import ch.bukkit.playground.util.EntityHelper;
import org.apache.commons.collections.CollectionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.TimerTask;
import java.util.logging.Logger;

public class SpawnTask extends TimerTask {

    private final static Logger logger = Logger.getLogger("SpawnTask");

    private BattleConfiguration battleConfiguration;
    private BattleData battleData;
    private String entity;
    private int round;

    public SpawnTask(BattleConfiguration battleConfiguration, BattleData battleData, String entity, int round) {
        this.battleConfiguration = battleConfiguration;
        this.battleData = battleData;
        this.entity = entity;
        this.round = round;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        Class<LivingEntity> entityClass = EntityHelper.getLivingEntityClassForName(entity);
        if (entityClass != null) {
            int quantity = battleData.getTotalActivePlayers() * round;
            for (int i = 0; i < quantity; i++) {
                Location loc;
                if (CollectionUtils.isNotEmpty(battleConfiguration.getSpanws())) {
                    int rnd = ((int) (Math.random() * 1000)) % battleConfiguration.getSpanws().size();
                    loc = battleConfiguration.getSpanws().get(rnd);
                } else {
                    loc = battleConfiguration.getPosStart();
                }
                try {
                    Entity e = battleConfiguration.getWorld().spawn(loc, entityClass);
                    battleData.addSpawnedMob(e);
                } catch (Exception e) {
                    logger.warning("Could not spawn entity '" + entity + "'.");
                }
            }
            new MessageTask(battleData.getActivePlayers().keySet(), ChatColor.RED + "Spawned " + quantity + " " + entity + " monsters. Have fun killing them.").run();
        }
    }
}

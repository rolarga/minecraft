package ch.bukkit.playground.instant.tasks;

import ch.bukkit.playground.instant.model.ArenaConfiguration;
import ch.bukkit.playground.instant.model.ArenaData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.TimerTask;

public class SpawnTask extends TimerTask {

    private ArenaConfiguration arenaConfiguration;
    private ArenaData arenaData;
    private Class<? extends Entity> entity;
    private int round;

    public SpawnTask(ArenaConfiguration arenaConfiguration, ArenaData arenaData, Class<? extends Entity> entity, int round) {
        this.arenaConfiguration = arenaConfiguration;
        this.arenaData = arenaData;
        this.entity = entity;
        this.round = round;
    }

    @Override
    public void run() {
        int quantity = arenaData.getTotalActivePlayers() * round;
        for(int i = 0; i < quantity; i++) {
            int rnd = ((int) (Math.random() * 1000)) % arenaConfiguration.getSpanws().size();
            Location loc = arenaConfiguration.getSpanws().get(rnd);
            if(loc == null) {
                loc = arenaConfiguration.getPosStart();
            }
            Entity e = arenaConfiguration.getWorld().spawn(loc, entity);
            arenaData.addSpawnedMob(e);
        }

        MessageTask messageTask = new MessageTask(arenaData.getActivePlayers().keySet(), ChatColor.RED + "Spawned " + quantity + " " + entity.getSimpleName() + " monsters. Have fun killing them.");
        messageTask.run();
    }
}

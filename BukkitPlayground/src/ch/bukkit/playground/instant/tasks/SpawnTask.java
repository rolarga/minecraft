package ch.bukkit.playground.instant.tasks;

import ch.bukkit.playground.instant.arena.Arena;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.TimerTask;

public class SpawnTask extends TimerTask {

    private Arena arena;
    private Class<? extends Entity> entity;
    private int amount;

    public SpawnTask(Arena arena, Class<? extends Entity> entity, int amount) {
        this.arena = arena;
        this.entity = entity;
        this.amount = amount;
    }

    @Override
    public void run() {
        for(int i = 0; i < amount; i++) {
            int rnd = ((int) (Math.random() * 1000)) % arena.getSpanws().size();
            Location loc = arena.getSpanws().get(rnd);
            if(loc == null) {
                loc = arena.getPosStart();
            }
            Entity e = arena.getPos1().getWorld().spawn(loc, entity);
            arena.addSpawnedMob(e);
        }

        MessageTask messageTask = new MessageTask(arena.getActivePlayers().keySet(), "Spawned " + amount + " " + entity.getSimpleName() + " monsters. Have fun killing them.");
        messageTask.run();
    }
}

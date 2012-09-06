package ch.bukkit.playground.instant.tasks;

import ch.bukkit.playground.TestBase;
import org.junit.Before;
import org.junit.Test;

public class SpawnTaskTest extends TestBase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        loadInstantBattle();
    }

    @Test
    public void testSpawnMobs() throws Exception {
        battleHandler.getBattleConfiguration().setPos1(originLocation);
        battleHandler.getBattleConfiguration().setPosStart(originLocation);
        new SpawnTask(battleHandler.getBattleConfiguration(), battleHandler.getBattleData(), "Zombie", 10).run();

        // spawns 10 for each active players --> 10 * 4
        assert battleHandler.getBattleData().getSpawnedMobs().size() == (10 * 4);
    }
}

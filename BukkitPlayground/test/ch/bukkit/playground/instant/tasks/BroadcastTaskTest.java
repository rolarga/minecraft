package ch.bukkit.playground.instant.tasks;

import ch.bukkit.playground.TestBase;
import org.junit.Before;
import org.junit.Test;

public class BroadcastTaskTest extends TestBase {
    @Before
    public void setUp() throws Exception {
        super.setUp();
        loadInstantBattle();
    }

    @Test
    public void testBroadcastMessage() throws Exception {
        new BroadcastTask(battleHandler.getBattleData().getActivePlayers().keySet(), "Players %players% please move.").run();
    }
}

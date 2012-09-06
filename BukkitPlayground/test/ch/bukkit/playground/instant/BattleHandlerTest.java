package ch.bukkit.playground.instant;

import ch.bukkit.playground.TestBase;
import ch.bukkit.playground.instant.model.BattleType;
import org.junit.Before;
import org.junit.Test;

public class BattleHandlerTest extends TestBase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        loadInstantBattle();
    }

    @Test
    public void testStart() throws Exception {
        battleHandler.start();
    }

    @Test
    public void testStop() throws Exception {
        battleHandler.start();
    }

    @Test
    public void testFinishBattle() throws Exception {
        battleHandler.finishBattle();
    }

    @Test
    public void testClearActivePlayersAndTeleportBack() throws Exception {
        battleHandler.clearActivePlayersAndTeleportBack();
    }

    @Test
    public void testGetName() throws Exception {
        assert battleHandler.getName().equals("test");
    }

    @Test
    public void testGetBattleConfiguration() throws Exception {
        assert battleHandler.getBattleConfiguration().getBattleType() == BattleType.COOP;
    }

    @Test
    public void testGetBattleData() throws Exception {
        assert battleHandler.getBattleData().getActivePlayers().size() == 4;
    }
}

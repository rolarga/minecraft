package ch.bukkit.playground.instant;

import ch.bukkit.playground.TestBase;
import ch.bukkit.playground.instant.model.BattleConfiguration;
import ch.bukkit.playground.instant.model.BattleData;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class InstantConfigTest extends TestBase {

    @Before
    public void setUp() throws Exception {
        super.setUp();
        loadInstantBattle();
    }

    @Test
    public void testSaveBattleHandler() throws Exception {
        BattleHandler battleHandler = new BattleHandler("test", new BattleConfiguration(), new BattleData());
        InstantConfig.saveBattleHandler(battleHandler);
    }

    @Test
    public void testLoadEmptyBattleHandlers() throws Exception {
        // folder is non existent --> no error should be thrown
        InstantConfig.loadBattleHandlers();
    }

    @Test
    public void testSaveAndLoadBattleHandler() throws Exception {
        battleHandler.getBattleData().addRegisteredPlayer(player);
        battleHandler.getBattleData().addRegisteredPlayer(player2);
        InstantConfig.saveBattleHandler(battleHandler);

        Map<String, BattleHandler> battleHandlers = InstantConfig.loadBattleHandlers();
        assert battleHandlers.size() == 1;
        assert battleHandlers.get("test") != null;

        BattleHandler referenceBattleHandler = new BattleHandler("test", new BattleConfiguration(), new BattleData());

        assert referenceBattleHandler.getBattleConfiguration().getLevels().size() == battleHandlers.get("test").getBattleConfiguration().getLevels().size();
    }
}

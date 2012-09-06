package ch.bukkit.playground;

import ch.bukkit.playground.helper.*;
import ch.bukkit.playground.instant.BattleHandler;
import ch.bukkit.playground.instant.InstantHandler;
import ch.bukkit.playground.instant.model.BattleConfiguration;
import ch.bukkit.playground.instant.model.BattleData;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class TestBase {

    InstantHandler instantHandler;
    protected World world;
    protected Player player;
    protected TestPlayer player2;
    protected Player op;
    protected Player vip;
    protected BattleHandler battleHandler;
    protected Location originLocation;
    protected TestPlugin plugin;

    protected void setUp() throws Exception {
        // delete
        try {
            FileUtils.forceDelete(InstantBattlePlugin.PLUGIN_DIRECTORY);
        } catch (FileNotFoundException fnfe) {
            // thats fine
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadInstantBattle() throws Exception {
        // just load it once per run  (cannot be load more ;) )
        if (Bukkit.getServer() == null) {
            TestServer testServer = new TestServer();
            Bukkit.setServer(testServer);
        }
        plugin = new TestPlugin(Bukkit.getServer());
        Bukkit.getServer().getPluginManager().enablePlugin(plugin);

        instantHandler = new InstantHandler();

        // setup basic test data
        world = new TestWorld();
        originLocation = new Location(world, 1, 1, 1);
        player = new TestPlayer("tester", 10, originLocation);
        player2 = new TestPlayer("tester2", 5, originLocation);
        vip = new VipTestPlayer("viptester", 20, originLocation);
        op = new OpTestPlayer("optester", 100, originLocation);

        TestServer testServer = (TestServer) Bukkit.getServer();
        testServer.clearPlayers();
        testServer.addPlayer(player);
        testServer.addPlayer(player2);
        testServer.addPlayer(op);
        testServer.addPlayer(vip);
        testServer.setWorld(world);

        battleHandler = new BattleHandler("test", new BattleConfiguration(), new BattleData());

        battleHandler.getBattleConfiguration().setGroupAmount(2);

        battleHandler.getBattleData().addActivePlayer(player, originLocation);
        battleHandler.getBattleData().addActivePlayer(player2, originLocation);
        battleHandler.getBattleData().addActivePlayer(op, originLocation);
        battleHandler.getBattleData().addActivePlayer(vip, originLocation);
        battleHandler.getBattleData().setActive(true);
        battleHandler.getBattleData().setTotalActivePlayers(battleHandler.getBattleData().getActivePlayers().size());

        instantHandler.getBattleHandlers().put("test", battleHandler);
    }
}

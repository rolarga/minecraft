package ch.bukkit.playground.instant;

import ch.bukkit.playground.TestBase;
import ch.bukkit.playground.helper.TestPlayer;
import ch.bukkit.playground.helper.TestPlugin;
import ch.bukkit.playground.helper.TestServer;
import ch.bukkit.playground.helper.TestWorld;
import ch.bukkit.playground.instant.model.BattleConfiguration;
import ch.bukkit.playground.instant.model.BattleData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InstantListenerTest extends TestBase {

    InstantHandler instantHandler;
    World world;
    Player player;
    BattleHandler battleHandler;
    Location originLocation;
    TestPlugin plugin;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        // just load it once per run  (cannot be load more ;) )
        if (Bukkit.getServer() == null) {
            TestServer testServer = new TestServer();
            Bukkit.setServer(testServer);
            world = new TestWorld();
            Bukkit.getServer().getWorlds().add(world);
        }
        plugin = new TestPlugin(Bukkit.getServer());
        Bukkit.getServer().getPluginManager().enablePlugin(plugin);
        instantHandler = new InstantHandler();

        // setup basic test data
        player = new TestPlayer("tester");
        originLocation = new Location(world, 1, 1, 1);
        battleHandler = new BattleHandler("test", new BattleConfiguration(), new BattleData());
        battleHandler.getBattleData().addActivePlayer(player, originLocation);
        instantHandler.battleHandlers.put("test", battleHandler);
    }

    @After
    public void tearDown() throws Exception {
        Bukkit.getServer().getPluginManager().clearPlugins();
    }

    @Test
    public void testPlayerRespawn() throws Exception {
        Location specLocation = new Location(world, 2, 2, 2);

        // player should be teleported to his origin location when he dies and there is no spectator location
        PlayerRespawnEvent playerRespawnEvent = new PlayerRespawnEvent(player, new Location(world, 0, 0, 0), false);
        Bukkit.getServer().getPluginManager().callEvent(playerRespawnEvent);

        assert playerRespawnEvent.getRespawnLocation().equals(originLocation);

        // player should be teleported to spectator location when he dies - and originSpec list should contain his origin location
        battleHandler.getBattleConfiguration().setPosSpectator(specLocation);
        playerRespawnEvent = new PlayerRespawnEvent(player, new Location(world, 0, 0, 0), false);
        Bukkit.getServer().getPluginManager().callEvent(playerRespawnEvent);

        assert playerRespawnEvent.getRespawnLocation().equals(specLocation);
        assert battleHandler.getBattleData().getOriginSpectatorLocations().get(player).equals(originLocation);
    }

    @Test
    public void testPlayerMove() throws Exception {
        PlayerMoveEvent playerMoveEvent = new PlayerMoveEvent(player, new Location(world, 0, 0, 0), new Location(world, 10, 10, 10));
        Bukkit.getServer().getPluginManager().callEvent(playerMoveEvent);

        assert playerMoveEvent.isCancelled();
    }

    @Test
    public void testPlayerTeleport() throws Exception {
        PlayerTeleportEvent playerTeleportEvent = new PlayerTeleportEvent(player, new Location(world, 0, 0, 0), new Location(world, 10, 10, 10));
        Bukkit.getServer().getPluginManager().callEvent(playerTeleportEvent);

        assert playerTeleportEvent.isCancelled();
    }

    @Test
    public void testPlayerFlight() throws Exception {

    }

    @Test
    public void testEntityDeathEvent() throws Exception {

    }

    @Test
    public void testPlayerQuit() throws Exception {
    }
}

package ch.bukkit.playground.instant;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InstantHandlerTest {

    private InstantHandler instantHandler;

    @Before
    public void setUp() throws Exception {
        instantHandler = new InstantHandler();
        instantHandler.start();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStart() throws Exception {
        instantHandler.start();
    }

    @Test
    public void testHandlePlayerCommands() throws Exception {
        instantHandler.handlePlayerCommands("a", "list", null);
        instantHandler.handlePlayerCommands("a", "join", null);
        instantHandler.handlePlayerCommands("a", "leave", null);
        instantHandler.handlePlayerCommands("a", "spec", null);
        instantHandler.handlePlayerCommands("a", "unspec", null);

        // test for illegal arguments
        instantHandler.handlePlayerCommands("a", "XXX", null);
        instantHandler.handlePlayerCommands(null, null, null);

        // test with battle 'a' existing
        instantHandler.handleOpCommands("a", "stat", null, null);

        instantHandler.handlePlayerCommands("a", "list", null);
        instantHandler.handlePlayerCommands("a", "join", null);
        instantHandler.handlePlayerCommands("a", "leave", null);
        instantHandler.handlePlayerCommands("a", "spec", null);
        instantHandler.handlePlayerCommands("a", "unspec", null);
    }

    @Test
    public void testHandleOpCommands() throws Exception {
        instantHandler.handleOpCommands("a", "stat", null, null);
    }

    @Test
    public void testHandlePlayerEvents() throws Exception {
        instantHandler.handlePlayerEvents(new PlayerJoinEvent(null, null));
    }

    @Test
    public void testHandleEntityEvents() throws Exception {
        instantHandler.handleEntityEvents(new EntityDamageByEntityEvent(null, null, null, 100));
    }

    @Test
    public void testDisable() throws Exception {
        instantHandler.disable();
    }
}

package ch.bukkit.playground.instant;

import ch.bukkit.playground.TestBase;
import ch.bukkit.playground.instant.eventhandlers.PlayerEventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PlayerToggleFlightEvent.class)
public class InstantListenerTest extends TestBase {

    private InstantListener instantListener;
    private InstantHandler instantHandler;

    @Before
    public void setUp() {
        super.setUp();
        instantHandler = new InstantHandler();
        instantListener = new InstantListener(instantHandler);

        // create an battle with name test
        instantHandler.handleOpCommands("test", "stat", null, null);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPlayerRespawn() throws Exception {

    }

    @Test
    public void testPlayerMove() throws Exception {

    }

    @Test
    public void testPlayerTeleport() throws Exception {

    }

    @Test
    public void testPlayerFlight() throws Exception {

    }

    @Test
    public void testEntityDeathEvent() throws Exception {

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPlayerQuit() throws Exception {
        PlayerToggleFlightEvent mockEvent = PowerMockito.mock(PlayerToggleFlightEvent.class);
        PlayerEventHandler eventHandler = instantHandler.playerEventHandlers.get(mockEvent.getClass());
        BattleHandler battleHandler = instantHandler.battleHandlers.get(0);

        // create player
        Player mockPlayer = mock(Player.class);
        when(mockPlayer.getName()).thenReturn("tester");

        // add player to active ones
        battleHandler.getBattleData().addActivePlayer(mockPlayer, null);

        // set return of event
        when(mockEvent.getPlayer()).thenReturn(mockPlayer);

        // send event
        instantListener.playerFlight(mockEvent);

        verify(mockEvent).getPlayer();
        verify(instantHandler).handlePlayerEvents(mockEvent);

        verify(eventHandler).processEvent(mockEvent, battleHandler);


    }
}

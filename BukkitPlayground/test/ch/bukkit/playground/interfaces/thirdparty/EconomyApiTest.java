package ch.bukkit.playground.interfaces.thirdparty;

import ch.bukkit.playground.TestBase;
import org.junit.Test;

public class EconomyApiTest extends TestBase {

    @Test
    public void testInit() throws Exception {
        EconomyApi.init();
    }

    @Test
    public void testAdd() throws Exception {
        EconomyApi.add(player, 100);
    }
}

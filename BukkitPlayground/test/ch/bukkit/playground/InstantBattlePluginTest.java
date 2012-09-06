package ch.bukkit.playground;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InstantBattlePluginTest extends TestBase {
    @Before
    public void setUp() throws Exception {
        super.setUp();
        loadInstantBattle();
    }

    @Test
    public void testOnEnable() throws Exception {
        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
        Bukkit.getServer().getPluginManager().enablePlugin(plugin);
    }

    @Test
    public void testOnCommand() throws Exception {
        Command instant = new Command("instant") {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };
        plugin.onCommand(null, instant, null, new String[]{"list", "test"});
        plugin.onCommand(null, instant, null, new String[]{""});


        Command instantop = new Command("instantop") {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return true;
            }
        };
        plugin.onCommand(null, instantop, null, new String[]{"test", "stat"});
        plugin.onCommand(null, instantop, null, new String[]{""});
        plugin.onCommand(op, instantop, null, new String[]{"test", "pos1"});
    }

    @Test
    public void testOnDisable() throws Exception {
        Bukkit.getServer().getPluginManager().disablePlugin(plugin);
    }

    @After
    public void tearDown() throws Exception {
        Bukkit.getServer().getPluginManager().enablePlugin(plugin);
    }
}

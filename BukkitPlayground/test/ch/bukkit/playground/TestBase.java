package ch.bukkit.playground;

import org.apache.commons.io.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class TestBase {

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
}

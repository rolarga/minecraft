package ch.bukkit.playground.helper;

import ch.bukkit.playground.InstantBattlePlugin;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class TestPlugin extends InstantBattlePlugin {

    public TestPlugin(Server testServer) throws Exception {
        initialize(new JavaPluginLoader(testServer), testServer, getDescriptioninternal(), new File("./plugins/" + PLUGIN_NAME), new File("./src/plugin.yml"), this.getClassLoader());

        PluginManager pluginManager = testServer.getPluginManager();
        Field pluginsField = pluginManager.getClass().getDeclaredField("plugins");
        pluginsField.setAccessible(true);
        ((List<Plugin>) pluginsField.get(pluginManager)).add(this);

        Field lookupNamesField = pluginManager.getClass().getDeclaredField("lookupNames");
        lookupNamesField.setAccessible(true);
        ((Map<String, Plugin>) lookupNamesField.get(pluginManager)).put(getName(), this);

    }


    public PluginDescriptionFile getDescriptioninternal() {
        PluginDescriptionFile file = null;
        try {
            file = new PluginDescriptionFile(new FileInputStream("./src/plugin.yml"));
        } catch (InvalidDescriptionException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return file;
    }
}

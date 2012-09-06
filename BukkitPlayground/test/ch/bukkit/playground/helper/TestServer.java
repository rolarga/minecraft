package ch.bukkit.playground.helper;

import com.avaje.ebean.config.ServerConfig;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.plugin.*;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.StandardMessenger;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class TestServer implements Server {

    private BukkitScheduler scheduler = new CraftScheduler();
    private PluginManager pluginManager = new SimplePluginManager(this, new SimpleCommandMap(this));
    private ServicesManager serviceManager = new SimpleServicesManager();
    private List<Player> players = new LinkedList<Player>();
    private World world;

    Logger logger = Logger.getLogger("TestServer");

    public void addPlayer(Player player) {
        if (players.contains(player)) return;
        players.add(player);
    }

    public void clearPlayers() {
        players.clear();
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public TestServer() throws Exception {
    }

    @Override
    public String getName() {
        return "testserver";
    }

    @Override
    public String getVersion() {
        return "testversion";
    }

    @Override
    public String getBukkitVersion() {
        return "testversion";
    }

    @Override
    public Player[] getOnlinePlayers() {
        return new Player[0];
    }

    @Override
    public int getMaxPlayers() {
        return 1;
    }

    @Override
    public int getPort() {
        return 1234;
    }

    @Override
    public int getViewDistance() {
        return 100;
    }

    @Override
    public String getIp() {
        return "127.0.0.1";
    }

    @Override
    public String getServerName() {
        return "testserver";
    }

    @Override
    public String getServerId() {
        return "testserver";
    }

    @Override
    public String getWorldType() {
        return "flat";
    }

    @Override
    public boolean getGenerateStructures() {
        return false;
    }

    @Override
    public boolean getAllowEnd() {
        return false;
    }

    @Override
    public boolean getAllowNether() {
        return false;
    }

    @Override
    public boolean hasWhitelist() {
        return false;
    }

    @Override
    public void setWhitelist(boolean value) {

    }

    @Override
    public Set<OfflinePlayer> getWhitelistedPlayers() {
        return new HashSet<OfflinePlayer>();
    }

    @Override
    public void reloadWhitelist() {

    }

    @Override
    public int broadcastMessage(String message) {
        System.out.println("TEST BROADCAST:" + message);
        return 0;
    }

    @Override
    public String getUpdateFolder() {
        return null;
    }

    @Override
    public File getUpdateFolderFile() {
        return null;
    }

    @Override
    public long getConnectionThrottle() {
        return 0;
    }

    @Override
    public int getTicksPerAnimalSpawns() {
        return 0;
    }

    @Override
    public int getTicksPerMonsterSpawns() {
        return 0;
    }

    @Override
    public Player getPlayer(String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    @Override
    public Player getPlayerExact(String name) {
        return getPlayer(name);
    }

    @Override
    public List<Player> matchPlayer(String name) {
        return Arrays.asList(getPlayer(name));
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public BukkitScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public ServicesManager getServicesManager() {
        return serviceManager;
    }

    @Override
    public List<World> getWorlds() {
        return Arrays.asList(world);
    }

    @Override
    public World createWorld(WorldCreator creator) {
        return null;
    }

    @Override
    public boolean unloadWorld(String name, boolean save) {
        return false;
    }

    @Override
    public boolean unloadWorld(World world, boolean save) {
        return false;
    }

    @Override
    public World getWorld(String name) {
        return world;
    }

    @Override
    public World getWorld(UUID uid) {
        return world;
    }

    @Override
    public MapView getMap(short id) {
        return null;
    }

    @Override
    public MapView createMap(World world) {
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public PluginCommand getPluginCommand(String name) {
        return null;
    }

    @Override
    public void savePlayers() {

    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String commandLine) throws CommandException {
        return false;
    }

    @Override
    public void configureDbConfig(ServerConfig config) {

    }

    @Override
    public boolean addRecipe(Recipe recipe) {
        return false;
    }

    @Override
    public List<Recipe> getRecipesFor(ItemStack result) {
        return null;
    }

    @Override
    public Iterator<Recipe> recipeIterator() {
        return null;
    }

    @Override
    public void clearRecipes() {
    }

    @Override
    public void resetRecipes() {
    }

    @Override
    public Map<String, String[]> getCommandAliases() {
        return null;
    }

    @Override
    public int getSpawnRadius() {
        return 0;
    }

    @Override
    public void setSpawnRadius(int value) {
    }

    @Override
    public boolean getOnlineMode() {
        return false;
    }

    @Override
    public boolean getAllowFlight() {
        return false;
    }

    @Override
    public boolean useExactLoginLocation() {
        return false;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public int broadcast(String message, String permission) {
        return 0;
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String name) {
        return null;
    }

    @Override
    public Set<String> getIPBans() {
        return null;
    }

    @Override
    public void banIP(String address) {

    }

    @Override
    public void unbanIP(String address) {

    }

    @Override
    public Set<OfflinePlayer> getBannedPlayers() {
        return null;
    }

    @Override
    public Set<OfflinePlayer> getOperators() {
        return null;
    }

    @Override
    public GameMode getDefaultGameMode() {
        return null;
    }

    @Override
    public void setDefaultGameMode(GameMode mode) {

    }

    @Override
    public ConsoleCommandSender getConsoleSender() {
        return null;
    }

    @Override
    public File getWorldContainer() {
        return null;
    }

    @Override
    public OfflinePlayer[] getOfflinePlayers() {
        return new OfflinePlayer[0];
    }

    @Override
    public Messenger getMessenger() {
        return new StandardMessenger();
    }

    @Override
    public HelpMap getHelpMap() {
        return null;
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, InventoryType type) {
        return null;
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, int size) {
        return null;
    }

    @Override
    public Inventory createInventory(InventoryHolder owner, int size, String title) {
        return null;
    }

    @Override
    public int getMonsterSpawnLimit() {
        return 0;
    }

    @Override
    public int getAnimalSpawnLimit() {
        return 0;
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        return 0;
    }

    @Override
    public boolean isPrimaryThread() {
        return false;
    }

    @Override
    public String getMotd() {
        return null;
    }

    @Override
    public Warning.WarningState getWarningState() {
        return null;
    }

    @Override
    public void sendPluginMessage(Plugin source, String channel, byte[] message) {

    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return null;
    }
}

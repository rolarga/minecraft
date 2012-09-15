package ch.bukkit.playground.interfaces.thirdparty;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Logger;

public class EconomyApi {

    private static Logger logger = Logger.getLogger("EconomyApi");

    private static net.milkbowl.vault.economy.Economy economy;
    private static boolean serverEconomyEnabled = false;
    private static boolean initialized = false;

    public static void init() {
        if (!initialized) {
            try {
                Class.forName("net.milkbowl.vault.economy.Economy");
            } catch (ClassNotFoundException e) {
                logger.warning("Vault is required for economy but wasn't found. Server economy is unavailable.");
                return;
            }
            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                economy = economyProvider.getProvider();
                serverEconomyEnabled = true;
            }
            initialized = true;
        }
    }

    // Add money to a player's account
    public static void add(Player player, double price) {
        init();
        if (serverEconomyEnabled) {
            economy.depositPlayer(player.getName(), price);
        } else {
            logger.warning("Server economy not enabled. Vault is required for economy.");
        }
    }
}

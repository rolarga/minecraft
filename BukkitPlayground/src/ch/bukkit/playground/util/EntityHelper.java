package ch.bukkit.playground.util;

import org.bukkit.entity.Monster;

import java.util.logging.Logger;

public class EntityHelper {

    private final static Logger logger = Logger.getLogger("EntityHelper");

    @SuppressWarnings("unchecked")
    public static Class<Monster> getMonsterClassForString(String entityName) {
        try {
            return (Class<Monster>) Class.forName("org.bukkit.entity." + entityName);
        } catch (ClassNotFoundException e) {
            logger.warning("Illegal Entity configured, cannot spawn mobs of type " + entityName + " as the are not existent.");
            return null;
        }
    }
}

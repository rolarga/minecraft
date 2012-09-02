package ch.bukkit.playground.util;

import org.bukkit.entity.Entity;

import java.util.logging.Logger;

public class EntityHelper {

    private final static Logger logger = Logger.getLogger("EntityHelper");

    @SuppressWarnings("unchecked")
    public static Class<Entity> getEntityClassForString(String entityName) {
        try {
            return (Class<Entity>) Class.forName("org.bukkit.entity." + entityName);
        } catch (ClassNotFoundException e) {
            logger.warning("Illegal Entity configured, cannot spawn mobs of type " + entityName + " as the are not existent.");
            return null;
        }
    }
}

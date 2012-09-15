package ch.bukkit.playground.util;

import org.bukkit.entity.LivingEntity;

import java.util.logging.Logger;

public class EntityHelper {

    private final static Logger logger = Logger.getLogger("EntityHelper");

    @SuppressWarnings("unchecked")
    public static Class<LivingEntity> getLivingEntityClassForName(String entityName) {
        try {
            return (Class<LivingEntity>) Class.forName("org.bukkit.entity." + entityName);
        } catch (ClassNotFoundException e) {
            logger.warning("Illegal Entity configured, cannot spawn mobs of type " + entityName + " as the are not existent.");
            return null;
        }
    }
}

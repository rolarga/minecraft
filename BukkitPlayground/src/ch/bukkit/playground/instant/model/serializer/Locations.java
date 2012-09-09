package ch.bukkit.playground.instant.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;

public class Locations {

    public static class Serializer extends JsonSerializer<Location> {

        @Override
        public void serialize(Location loc, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            LocationHolder locationHolder = new LocationHolder();
            locationHolder.setWorldName(loc.getWorld().getName());
            locationHolder.setX(loc.getX());
            locationHolder.setY(loc.getY());
            locationHolder.setZ(loc.getZ());
            jgen.writeObject(locationHolder);
        }
    }

    public static class Deserializer extends JsonDeserializer<Location> {

        @Override
        @SuppressWarnings("unchecked")
        public Location deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            LocationHolder locationHolder = jp.readValueAs(LocationHolder.class);
            return new Location(Bukkit.getWorld(locationHolder.getWorldName()), locationHolder.getX(), locationHolder.getY(), locationHolder.getZ());
        }
    }

    private static class LocationHolder {
        private double x;
        private double y;
        private double z;
        private String worldName;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public double getZ() {
            return z;
        }

        public void setZ(double z) {
            this.z = z;
        }

        public String getWorldName() {
            return worldName;
        }

        public void setWorldName(String worldName) {
            this.worldName = worldName;
        }
    }
}

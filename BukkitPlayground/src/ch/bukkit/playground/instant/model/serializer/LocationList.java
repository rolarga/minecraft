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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LocationList {

    public static class Serializer extends JsonSerializer<List<Location>> {

        @Override
        public void serialize(List<Location> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartArray();
            for (Location loc : value) {
                LocationHolder locationHolder = new LocationHolder();
                locationHolder.setWorldName(loc.getWorld().getName());
                locationHolder.setX(loc.getX());
                locationHolder.setY(loc.getY());
                locationHolder.setZ(loc.getZ());
                jgen.writeObject(locationHolder);
            }
            jgen.writeEndArray();
        }
    }

    public static class Deserializer extends JsonDeserializer<List<Location>> {

        @Override
        @SuppressWarnings("unchecked")
        public List<Location> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            List<Map> locationHolders = (List<Map>) jp.readValueAs(List.class);
            List<Location> locations = new LinkedList<Location>();
            for (Map mapEntry : locationHolders) {
                locations.add(new Location(Bukkit.getWorld("" + mapEntry.get("worldName")), (Double) mapEntry.get("x"), (Double) mapEntry.get("y"), (Double) mapEntry.get("z")));
            }
            return locations;
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

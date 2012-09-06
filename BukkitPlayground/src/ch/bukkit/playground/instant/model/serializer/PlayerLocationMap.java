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
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerLocationMap {

    public static class Serializer extends JsonSerializer<Map<Player, Location>> {

        @Override
        public void serialize(Map<Player, Location> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartArray();

            for (Map.Entry<Player, Location> playerLocationEntry : value.entrySet()) {
                PlayerLocationHolder playerLocationHolder = new PlayerLocationHolder();
                playerLocationHolder.setName(playerLocationEntry.getKey().getName());
                playerLocationHolder.setX(playerLocationEntry.getValue().getX());
                playerLocationHolder.setY(playerLocationEntry.getValue().getY());
                playerLocationHolder.setZ(playerLocationEntry.getValue().getZ());
                playerLocationHolder.setWorldName(playerLocationEntry.getValue().getWorld().getName());

                jgen.writeObject(playerLocationHolder);
            }

            jgen.writeEndArray();
        }
    }

    public static class Deserializer extends JsonDeserializer<Map<Player, Location>> {

        @Override
        @SuppressWarnings("unchecked")
        public Map<Player, Location> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            List<Map> playerLocationHolders = (List<Map>) jp.readValueAs(List.class);

            Map<Player, Location> players = new HashMap<Player, Location>();
            for (Map mapEntry : playerLocationHolders) {
                Player player = Bukkit.getServer().getPlayer("" + mapEntry.get("name"));
                players.put(player, new Location(Bukkit.getWorld("" + mapEntry.get("worldName")), (Double) mapEntry.get("x"), (Double) mapEntry.get("y"), (Double) mapEntry.get("z")));
            }
            return players;

        }
    }

    private static class PlayerLocationHolder {

        private String name;
        private double x;
        private double y;
        private double z;
        private String worldName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

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

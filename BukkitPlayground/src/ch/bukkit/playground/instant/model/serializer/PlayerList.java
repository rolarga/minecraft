package ch.bukkit.playground.instant.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PlayerList {

    public static class Serializer extends JsonSerializer<List<Player>> {

        @Override
        public void serialize(List<Player> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeStartArray();
            for (Player player : value) {
                jgen.writeString(player.getName());
            }
            jgen.writeEndArray();
        }
    }

    public static class Deserializer extends JsonDeserializer<List<Player>> {

        @Override
        @SuppressWarnings("unchecked")
        public List<Player> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            List<String> playerNames = (List<String>) jp.readValueAs(List.class);
            List<Player> players = new LinkedList<Player>();
            for (String playerName : playerNames) {
                players.add(Bukkit.getServer().getPlayer(playerName));
            }
            return players;
        }
    }
}

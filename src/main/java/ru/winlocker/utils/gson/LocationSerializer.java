package ru.winlocker.utils.gson;

import com.google.gson.*;
import lombok.*;
import org.bukkit.*;

import java.lang.reflect.*;

@NoArgsConstructor(staticName = "create")
public class LocationSerializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.isJsonObject()) {
            val jsonObject = json.getAsJsonObject();

            World world = null;

            double x = 0, y = 0, z = 0;
            float pitch = 0, yaw = 0;

            if(jsonObject.has("world"))
                world = Bukkit.getWorld(jsonObject.get("world").getAsString());

            if(jsonObject.has("x"))
                x = jsonObject.get("x").getAsDouble();

            if(jsonObject.has("y"))
                y = jsonObject.get("y").getAsDouble();

            if(jsonObject.has("z"))
                z = jsonObject.get("z").getAsDouble();

            if(jsonObject.has("pitch"))
                pitch = jsonObject.get("pitch").getAsFloat();

            if(jsonObject.has("yaw"))
                yaw = jsonObject.get("yaw").getAsFloat();

            return new Location(world, x, y, z, yaw, pitch);
        }
        return null;
    }

    @Override
    public JsonElement serialize(Location src, Type typeOfSrc, JsonSerializationContext context) {
        if(src != null) {
            JsonObject jsonObject = new JsonObject();

            val world = src.getWorld();

            if (world != null) {
                jsonObject.addProperty("world", world.getName());
            }

            jsonObject.addProperty("x", src.getX());
            jsonObject.addProperty("y", src.getY());
            jsonObject.addProperty("z", src.getZ());
            jsonObject.addProperty("pitch", src.getPitch());
            jsonObject.addProperty("yaw", src.getYaw());

            return jsonObject;
        }
        return null;
    }
}

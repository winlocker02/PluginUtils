package ru.winlocker.utils.messages.json;

import com.google.gson.*;
import lombok.*;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.*;
import net.md_5.bungee.chat.*;
import org.bukkit.entity.*;

import java.lang.reflect.*;
import java.util.*;

import static com.cryptomorin.xseries.ReflectionUtils.supports;
import static ru.winlocker.utils.Utils.*;

@RequiredArgsConstructor(staticName = "create")
public class JsonMessages {

    private static final Gson DEFAULT_GSON;

    private static Class<?> getContentClass(String path) {
        String key = "net.md_5.bungee.api.chat.hover.content." + path;
        try {
            return Class.forName(key);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Failed to get class: " + key, e);
        }
    }

    private static Class<?> getChatClass(String path) {
        String key = "net.md_5.bungee.api.chat." + path;
        try {
            return Class.forName(key);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Failed to get class: " + key, e);
        }
    }

    static {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();

        if (supports(16)) {
            try {
                builder.registerTypeAdapter(getContentClass("Entity"), getContentClass("EntitySerializer").newInstance());
                builder.registerTypeAdapter(getContentClass("Text"), getContentClass("TextSerializer").newInstance());
                builder.registerTypeAdapter(getContentClass("Item"), getContentClass("ItemSerializer").newInstance());

                builder.registerTypeAdapter(getChatClass("ItemTag"), getChatClass("ItemTag$Serializer").newInstance());

            } catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }

        builder.registerTypeAdapter(ClickEvent.class, new ClickEventSerializer());
        builder.registerTypeAdapter(HoverEvent.class, new HoverEventSerializer());
        builder.registerTypeAdapter(EventObject.class, new EventObjectSerializer());

        DEFAULT_GSON = builder.create();
    }

    public static EventObject createEvent() {
        return EventObject.create();
    }

    private @Getter final String text;
    private final TreeSet<ComparableEventObject> events = new TreeSet<>();

    public JsonMessages createEvent(@NonNull EventObject eventObject) {
        this.events.add(new ComparableEventObject(eventObject));
        return this;
    }

    public String toJsonFormat() {
        return toJsonFormat(DEFAULT_GSON);
    }

    public String toJsonFormat(@NonNull Gson gson) {
        JsonArray array = new JsonArray();

        int currentIndex = 0;

        for (ComparableEventObject comparableEventObject : events) {
            EventObject event = comparableEventObject.eventObject;

            String between = event.between;

            if (text.contains(between)) {
                int index = text.indexOf(between);
                String chunk = text.substring(currentIndex, index);

                currentIndex = index + between.length();

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("text", color(chunk));

                array.add(jsonObject);
                array.add(DEFAULT_GSON.toJsonTree(event, EventObject.class));
            }
        }

        if(currentIndex < text.length()) {
            String chunk = text.substring(currentIndex);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("text", color(chunk));

            array.add(jsonObject);
        }

        return gson.toJson(array);
    }

    public BaseComponent[] toBaseComponents() {
        return ComponentSerializer.parse(toJsonFormat());
    }

    public void sendJsonMessage(@NonNull Player player) {
        player.spigot().sendMessage(toBaseComponents());
    }

    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    @NoArgsConstructor(staticName = "create")
    public static class EventObject {

        private @NonNull String between;

        private ClickEvent clickEvent;
        private HoverEvent hoverEvent;

        public EventObject between(@NonNull String between) {
            this.between = between;
            return this;
        }

        public EventObject clickEvent(@NonNull ClickEvent clickEvent) {
            this.clickEvent = clickEvent;
            return this;
        }

        public EventObject hoverEvent(@NonNull HoverEvent hoverEvent) {
            this.hoverEvent = hoverEvent;
            return this;
        }

        public EventObject runCommand(String command) {
            this.clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
            return this;
        }

        public EventObject showText(String text) {
            if(!supports(16)) {
                this.hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(text));
            } else {
                this.hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(text));
            }
            return this;
        }
    }

    @RequiredArgsConstructor
    class ComparableEventObject implements Comparable<ComparableEventObject> {

        private final @NonNull EventObject eventObject;

        @Override
        public int compareTo(@NonNull ComparableEventObject comparableEventObject) {
            return text.indexOf(this.eventObject.between) > text.indexOf(comparableEventObject.eventObject.between) ? 1 : -1;
        }
    }

    public static class EventObjectSerializer implements JsonSerializer<EventObject> {

        @Override
        public JsonElement serialize(EventObject eventObject, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("text", color(eventObject.between));

            if (eventObject.getClickEvent() != null) {
                jsonObject.add("clickEvent", context.serialize(eventObject.getClickEvent()));
            }

            if (eventObject.getHoverEvent() != null) {
                jsonObject.add("hoverEvent", context.serialize(eventObject.getHoverEvent()));
            }

            return jsonObject;
        }
    }

    public static class ClickEventSerializer implements JsonSerializer<ClickEvent> {

        @Override
        public JsonElement serialize(ClickEvent clickEvent, Type typeOfSrc, JsonSerializationContext context) {

            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("action", clickEvent.getAction().name().toLowerCase(Locale.ROOT));
            jsonObject.addProperty("value", clickEvent.getValue());

            return jsonObject;
        }
    }

    public static class HoverEventSerializer implements JsonSerializer<HoverEvent> {

        @Override
        public JsonElement serialize(HoverEvent hoverEvent, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("action", hoverEvent.getAction().name().toLowerCase(Locale.ROOT));

            if (!supports(16)) {
                jsonObject.add("value", context.serialize(hoverEvent.getValue()));
            } else {
                if (hoverEvent.isLegacy()) {
                    jsonObject.add("value", context.serialize(hoverEvent.getContents().get(0)));
                } else {
                    jsonObject.add("contents", context.serialize(hoverEvent.getContents().size() == 1 ? hoverEvent.getContents().get(0) : hoverEvent.getContents()));
                }
            }

            return jsonObject;
        }
    }
}

package ru.winlocker.utils.messages;

import lombok.*;
import org.bukkit.command.*;
import org.bukkit.configuration.*;
import ru.winlocker.utils.messages.impl.*;

import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Messages {

    public static Messages create(@NonNull ConfigurationSection section) {
        val messages = new Messages(section.getString("prefix"));
        val data = fromConfigurationToMap(messages, section);

        messages.messages.putAll(data);

        return messages;
    }

    static Map<String, Message> fromConfigurationToMap(@NonNull Messages messages, @NonNull ConfigurationSection section) {
        Map<String, Message> data = new HashMap<>();

        section.getKeys(false).forEach(key -> {

            if(section.isConfigurationSection(key)) {
                Map<String, Message> newMessages = fromConfigurationToMap(messages, section.getConfigurationSection(key));
                newMessages.forEach((keyMessage, message) -> data.put(key + "." + keyMessage, message));
            } else {
                Message message = null;

                if(section.isString(key)) {
                    message = new PrimitiveMessage(messages, section.getString(key));
                } else if(section.isList(key)) {
                    message = new ListMessage(messages, section.getStringList(key));
                }

                if(message != null) {
                    data.put(key, message);
                }
            }
        });

        return data;
    }

    private @Setter String prefix;
    private @Setter @NonNull Map<String, Message> messages = new HashMap<>();

    public Messages(String prefix) {
        this.prefix = prefix;
    }

    public boolean has(@NonNull String key) {
        return this.messages.containsKey(key);
    }

    public Message get(@NonNull String key) {
        val message = this.messages.get(key);
        if(message == null) {
            throw new IllegalArgumentException("Message key " + key + " not found.");
        }
        return message;
    }

    public boolean hasPermission(@NonNull CommandSender sender, @NonNull String permission) {
        if(!sender.hasPermission(permission)) {
            if(has("no-permission")) {
                get("no-permission").sendMessage(sender);
            }
            return false;
        }
        return true;
    }

    public boolean hasPrefix() {
        return this.prefix != null;
    }

    public void put(@NonNull String key, @NonNull Message message) {
        this.messages.put(key, message);
    }

    public void putIfAbsent(@NonNull String key, @NonNull Message message) {
        this.messages.putIfAbsent(key, message);
    }
}

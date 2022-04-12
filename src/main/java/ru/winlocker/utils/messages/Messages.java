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

    public static Messages create(@NonNull ConfigurationSection configuration) {
        val messages = new Messages(configuration.getString("prefix"));

        configuration.getKeys(false).forEach(key -> {

            if(configuration.isConfigurationSection(key)) {
                val unite = create(configuration.getConfigurationSection(key));
                messages.messages.putAll(unite.messages);

            } else {
                Message message = null;

                if(configuration.isList(key)) {
                    message = new ListMessage(messages, configuration.getStringList(key));
                } else if(configuration.isString(key)) {
                    message = new PrimitiveMessage(messages, configuration.getString(key));
                }

                if(message != null) {
                    String keyMessage = configuration.getCurrentPath().replace(configuration.getName(), "");

                    if(keyMessage.isEmpty()) {
                        keyMessage = key;
                    } else {
                        keyMessage = keyMessage + "." + key;
                    }

                    messages.messages.put(keyMessage, message);
                }
            }
        });

        return messages;
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

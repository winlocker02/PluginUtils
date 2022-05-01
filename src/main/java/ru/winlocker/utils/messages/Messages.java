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
        val messagesData = fromConfigurationToMap(messages, configuration);

        val keyName = configuration.getName();

        messagesData.forEach((name, message) -> {
            if(!keyName.isEmpty()) {
                name = name.replace(keyName + ".", "");
            }
            messages.messages.put(name, message);
        });

        return messages;
    }

    static Map<String, Message> fromConfigurationToMap(@NonNull Messages messages, @NonNull ConfigurationSection configuration) {
        Map<String, Message> messagesMap = new HashMap<>();

        configuration.getKeys(false).forEach(key -> {

            if(configuration.isConfigurationSection(key)) {
                val newMessagesMap = fromConfigurationToMap(messages, configuration.getConfigurationSection(key));
                messagesMap.putAll(newMessagesMap);

            } else {
                Message message = null;

                if(configuration.isList(key)) {
                    message = new ListMessage(messages, configuration.getStringList(key));
                } else if(configuration.isString(key)) {
                    message = new PrimitiveMessage(messages, configuration.getString(key));
                }

                if(message != null) {
                    String keyMessage = configuration.getCurrentPath();

                    if(keyMessage.isEmpty()) {
                        keyMessage = key;
                    } else {
                        keyMessage = keyMessage + "." + key;
                    }

                    messagesMap.put(keyMessage, message);
                }
            }
        });

        return messagesMap;
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

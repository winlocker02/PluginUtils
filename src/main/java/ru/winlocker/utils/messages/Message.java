package ru.winlocker.utils.messages;

import lombok.*;
import org.bukkit.command.*;

import java.util.*;
import java.util.function.*;

@Getter
@AllArgsConstructor
public abstract class Message {

    protected final Messages messages;

    public abstract void sendMessage(@NonNull CommandSender sender);
    public abstract void sendMessage(@NonNull CommandSender sender, boolean enablePrefix);
    public abstract void sendMessage(@NonNull CommandSender sender, @NonNull UnaryOperator<String> apply);
    public abstract void sendMessage(@NonNull CommandSender sender, boolean enablePrefix, @NonNull UnaryOperator<String> apply);

    public abstract String toString();
    public abstract List<String> toList();
}

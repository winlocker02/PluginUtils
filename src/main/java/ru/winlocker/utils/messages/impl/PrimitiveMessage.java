package ru.winlocker.utils.messages.impl;

import lombok.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import ru.winlocker.utils.*;
import ru.winlocker.utils.messages.*;

import java.util.*;
import java.util.function.*;

public class PrimitiveMessage extends Message {

    private final @NonNull String value;

    public PrimitiveMessage(@NonNull Messages messages, @NonNull String value) {
        super(messages);
        this.value = value;
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender) {
        this.sendMessage(sender, true);
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, boolean enablePrefix) {
        this.sendMessage(sender, enablePrefix, message -> message);
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, @NonNull UnaryOperator<String> apply) {
        this.sendMessage(sender, true, apply);
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, boolean enablePrefix, UnaryOperator<String> apply) {
        val prefix = this.messages.getPrefix();
        val value = apply.apply(this.value);

        if(prefix != null && enablePrefix) {
            Utils.sendMessage(sender, prefix, value);
        } else {
            Utils.sendMessage(sender, value);
        }
    }

    @Override
    public String toString() {
        return Utils.color(this.value);
    }

    @Override
    public List<String> toList() {
        return Collections.singletonList(this.value);
    }
}

package ru.winlocker.utils.messages.impl;

import lombok.*;
import org.bukkit.command.*;
import ru.winlocker.utils.*;
import ru.winlocker.utils.messages.*;

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
        val prefix = getMessages().getPrefix();
        val value = apply.apply(this.value);

        if(prefix != null) {
            Utils.sendMessage(sender, prefix + value);
        } else {
            Utils.sendMessage(sender, value);
        }
    }
}

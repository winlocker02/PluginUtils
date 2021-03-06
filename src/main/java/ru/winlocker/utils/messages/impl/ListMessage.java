package ru.winlocker.utils.messages.impl;

import lombok.*;
import org.bukkit.command.*;
import ru.winlocker.utils.*;
import ru.winlocker.utils.messages.*;

import java.util.*;
import java.util.function.*;

public class ListMessage extends Message {

    private final @NonNull List<String> valueList;

    public ListMessage(@NonNull Messages messages, @NonNull List<String> valueList) {
        super(messages);
        this.valueList = valueList;
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender) {
        this.sendMessage(sender, false);
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, boolean enablePrefix) {
        this.sendMessage(sender, enablePrefix, message -> message);
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, @NonNull UnaryOperator<String> apply) {
        this.sendMessage(sender, false, apply);
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, boolean enablePrefix, @NonNull UnaryOperator<String> apply) {
        val prefix = this.messages.getPrefix();

        this.valueList.forEach(message -> {
            val value = apply.apply(message);

            if(prefix != null && enablePrefix) {
                Utils.sendMessage(sender, prefix, value);
            } else {
                Utils.sendMessage(sender, value);
            }
        });
    }

    @Override
    public String toString() {
        return Utils.color(String.join("\n", this.valueList));
    }

    @Override
    public List<String> toList() {
        return new ArrayList<>(this.valueList);
    }
}

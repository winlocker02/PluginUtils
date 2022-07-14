package ru.winlocker.utils.messages.impl;

import lombok.*;
import org.bukkit.command.*;
import ru.winlocker.utils.messages.*;

import java.util.*;
import java.util.function.*;

public class NullableMessage extends Message {

    public NullableMessage() {
        super(null);
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender) {

    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, boolean enablePrefix) {

    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, @NonNull UnaryOperator<String> apply) {

    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, boolean enablePrefix, @NonNull UnaryOperator<String> apply) {

    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public List<String> toList() {
        return Collections.emptyList();
    }
}

package ru.winlocker.utils.commands;

import org.bukkit.command.*;
import ru.winlocker.utils.messages.*;

import java.util.*;

public interface CommandSub {

    boolean execute(Messages messages, CommandSender sender, String[] args);
    List<String> tab(Messages messages, CommandSender sender, String[] args);
}

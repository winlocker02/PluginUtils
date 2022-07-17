package ru.winlocker.utils.commands.v2;

import lombok.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.command.defaults.*;
import ru.winlocker.utils.messages.*;

import java.lang.reflect.*;
import java.util.*;

@Getter
public class CommandRegistryBukkit extends BukkitCommand {

    private final @NonNull Messages messages;
    private final @NonNull CommandRegistry command;

    @Builder(buildMethodName = "create")
    public CommandRegistryBukkit(@NonNull Messages messages, @NonNull CommandRegistry command, List<String> aliases) {
        super(command.getDescription().getCommand(), command.getDescription().getDescription(), command.getDescription().getDescription(), aliases != null ? aliases : Collections.emptyList());
        this.command = command;
        this.messages = messages;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        return command.executeCommand(this.messages, sender, commandLabel, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return command.tabCommand(this.messages, sender, alias, args);
    }

    public void register() {
        try {
            Field fieldCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            fieldCommandMap.setAccessible(true);

            CommandMap commandMap = (CommandMap) fieldCommandMap.get(Bukkit.getServer());
            commandMap.register(this.getName(), this);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Failed to register command", e);
        }
    }

    public static void unregister(@NonNull String commandName) {
        try {
            Field fieldCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            fieldCommandMap.setAccessible(true);

            CommandMap commandMap = (CommandMap) fieldCommandMap.get(Bukkit.getServer());
            Command command = commandMap.getCommand(commandName);

            if(command != null) {
                command.unregister(commandMap);
            }
        } catch (Exception e) {
            throw new UnsupportedOperationException("Failed to unregister command", e);
        }
    }

    public void unregister() {
        unregister(getName());
    }
}

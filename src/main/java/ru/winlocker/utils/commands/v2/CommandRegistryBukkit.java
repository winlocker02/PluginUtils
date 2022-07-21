package ru.winlocker.utils.commands.v2;

import lombok.*;
import lombok.experimental.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.command.defaults.*;
import ru.winlocker.utils.messages.*;

import java.lang.reflect.*;
import java.util.*;

@Getter
public class CommandRegistryBukkit extends BukkitCommand {

    private static final CommandMap COMMAND_MAP;

    static {
        try {
            Field fieldCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            fieldCommandMap.setAccessible(true);

            COMMAND_MAP = (CommandMap) fieldCommandMap.get(Bukkit.getServer());
        } catch (Exception e) {
            throw new UnsupportedOperationException("Failed to get CommandMap", e);
        }
    }

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
        COMMAND_MAP.register(this.getName(), this);
    }

    public void unregister() {
        try {
            Field field = COMMAND_MAP.getClass().getDeclaredField("knownCommands");
            field.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, Command> commands = (Map<String, Command>) field.get(COMMAND_MAP);

            commands.keySet().removeIf(label -> label.contains(getName()));

            getAliases().forEach(alias -> {
                if(commands.containsKey(alias) && commands.get(alias).toString().contains(getName())) {
                    commands.remove(alias);
                }
            });
        } catch (Exception e) {
            throw new UnsupportedOperationException("Failed to unregister command: " + getName(), e);
        }
    }
}

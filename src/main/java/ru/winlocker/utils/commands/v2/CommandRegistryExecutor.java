package ru.winlocker.utils.commands.v2;

import lombok.*;
import org.bukkit.command.*;
import ru.winlocker.utils.messages.*;

import java.util.*;

@Getter
@AllArgsConstructor(staticName = "create")
public class CommandRegistryExecutor implements CommandExecutor, TabCompleter {

    private final @NonNull Messages messages;
    private final @NonNull CommandRegistry command;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.command.executeCommand(this.messages, sender, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return this.command.tabCommand(this.messages, sender, alias, args);
    }
}

package ru.winlocker.utils.commands.v2;

import lombok.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import ru.winlocker.utils.*;
import ru.winlocker.utils.commands.*;
import ru.winlocker.utils.messages.*;

import java.util.*;
import java.util.stream.*;

public abstract class CommandRegistry {

    private final @Getter CommandDescription description;
    private final Set<CommandRegistry> commands = new HashSet<>();

    public CommandRegistry() {
        if(!getClass().isAnnotationPresent(CommandDescription.class))
            throw new IllegalArgumentException("Command class " + getClass() + " is not annotated @" + CommandDescription.class);

        this.description = getClass().getAnnotation(CommandDescription.class);
    }

    public CommandRegistry register(@NonNull CommandRegistry command) {
        this.commands.add(command);
        return this;
    }

    boolean executeCommand(Messages messages, CommandSender sender, String label, String[] args) {

        if(args.length > 0) {
            CommandRegistry command = getCommand(args[0]);

            if(command != null) {
                CommandDescription description = command.getDescription();

                if(description.permission().isEmpty() || messages.hasPermission(sender, description.permission())) {

                    if(description.onlyPlayers() && !(sender instanceof Player)) {

                        if(messages.has("only-players")) {
                            messages.get("only-players").sendMessage(sender);
                        }
                        return true;
                    }

                    try {
                        List<String> argsList = new ArrayList<>(Arrays.asList(args));
                        argsList.remove(0);

                        if(!command.executeCommand(messages, sender, label, argsList.toArray(new String[0]))) {
                            if(messages.has(description.description())) {
                                messages.get(description.description()).sendMessage(sender);
                            } else {
                                Utils.sendMessage(sender, description.description());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(ChatColor.RED + "Произошла ошибка во время выполнении комамнды, пожалуйста обратитесь к администратору.");
                    }
                }
                return true;
            }
        }

        return execute(messages, sender, label, args);
    }

    List<String> tabCommand(Messages messages, CommandSender sender, String label, String[] args) {

        if(args.length > 1) {
            CommandRegistry command = getCommand(args[0]);

            if(command != null) {
                CommandDescription description = command.getDescription();

                if(description.permission().isEmpty() || sender.hasPermission(description.permission())) {

                    if(description.onlyPlayers() && !(sender instanceof Player))
                        return null;

                    try {
                        List<String> argsList = new ArrayList<>(Arrays.asList(args));
                        argsList.remove(0);

                        return filter(command.tabCommand(messages, sender, label, argsList.toArray(new String[0])), args);
                    } catch (Exception e) {
                        e.printStackTrace();
                        sender.sendMessage(ChatColor.RED + "Произошла ошибка, пожалуйста обратитесь к администратору");
                    }
                }
            }
        }

        return tab(messages, sender, label, args);
    }

    public List<String> filter(List<String> list, String[] args) {
        if(list == null || list.isEmpty()) return null;

        String last = args[args.length - 1].toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();

        for (String str : list) {
            if(str.startsWith(last)) {
                result.add(str);
            }
        }
        return result;
    }

    public boolean printHelpCommands(Messages messages, CommandSender sender) {
        List<CommandRegistry> commands = getAllowedCommands(sender);

        if(commands.isEmpty()) {
            if(messages.has("commands-empty")) {
                messages.get("commands-empty").sendMessage(sender);
            }
        } else {
            commands.forEach(command -> {
                CommandDescription description = command.getDescription();

                if(messages.has(description.description())) {
                    messages.get(description.description()).sendMessage(sender);
                } else {
                    Utils.sendMessage(sender, description.description());
                }
            });
        }
        return true;
    }

    protected CommandRegistry getCommand(@NonNull String label) {
        return this.commands.stream().filter(command -> command.getDescription().command().equalsIgnoreCase(label)).findFirst().orElse(null);
    }

    protected List<CommandRegistry> getAllowedCommands(@NonNull CommandSender sender) {
        return this.commands.stream()
                .filter(command -> {
                    CommandDescription description = command.getDescription();

                    return description.permission().isEmpty() || sender.hasPermission(description.command());
                }).collect(Collectors.toList());
    }

    public abstract boolean execute(Messages messages, CommandSender sender, String label, String[] args);

    public List<String> tab(Messages messages, CommandSender sender, String label, String[] args) {
        if(args.length == 1) {
            return filter(getAllowedCommands(sender).stream().map(command -> command.getDescription().command()).collect(Collectors.toList()), args);
        }
        return null;
    }
}

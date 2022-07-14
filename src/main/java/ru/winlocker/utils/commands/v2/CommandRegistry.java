package ru.winlocker.utils.commands.v2;

import lombok.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import ru.winlocker.utils.*;
import ru.winlocker.utils.messages.*;

import java.util.*;
import java.util.stream.*;

@AllArgsConstructor
public abstract class CommandRegistry {

    public static CommandRegistry createEmpty(CommandDescription description) {
        return new CommandRegistry(description) {
            @Override
            public boolean execute(Messages messages, CommandSender sender, String label, String[] args) {
                if (args.length > 0) {
                    messages.get("command-unknown").sendMessage(sender);
                    return true;
                } else {
                    return printHelpCommands(messages, sender, 0);
                }
            }
        };
    }

    public static CommandRegistry createHelpCommand(CommandRegistry command, CommandDescription description) {
        return new CommandRegistry(description) {
            @Override
            public boolean execute(Messages messages, CommandSender sender, String label, String[] args) {
                int page;

                try {
                    page = Math.max(1, Integer.parseInt(args[0])) - 1;
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    page = 0;
                }

                return command.printHelpCommands(messages, sender, page);
            }
        };
    }

    private final @Getter CommandDescription description;
    private final Set<CommandRegistry> commands = new LinkedHashSet<>();

    public CommandRegistry() {
        this.description = CommandDescription.createEmpty();
    }

    public CommandRegistry register(@NonNull CommandRegistry command) {
        this.commands.add(command);
        return this;
    }

    boolean executeCommand(Messages messages, CommandSender sender, String label, String[] args) {

        if (args.length > 0) {
            CommandRegistry command = getCommand(args[0]);

            if (command != null) {
                CommandDescription description = command.getDescription();

                if (description.getPermission() == null || messages.hasPermission(sender, description.getPermission())) {

                    if (description.isOnlyPlayers() && !(sender instanceof Player)) {
                        messages.get("only-players").sendMessage(sender);

                        return true;
                    }

                    try {
                        List<String> argsList = new ArrayList<>(Arrays.asList(args));
                        argsList.remove(0);

                        if (!command.executeCommand(messages, sender, label, argsList.toArray(new String[0]))) {

                            if (messages.has(description.getDescription())) {
                                messages.get(description.getDescription()).sendMessage(sender);
                            } else {
                                Utils.sendMessage(sender, description.getDescription());
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

        if (args.length > 1) {
            CommandRegistry command = getCommand(args[0]);

            if (command != null) {
                CommandDescription description = command.getDescription();

                if (description.getPermission() == null || sender.hasPermission(description.getPermission())) {

                    if (description.isOnlyPlayers() && !(sender instanceof Player))
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
        if (list == null || list.isEmpty()) return null;

        String last = args[args.length - 1].toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();

        for (String str : list) {
            if (str.startsWith(last)) {
                result.add(str);
            }
        }
        return result;
    }

    public boolean printHelpCommands(Messages messages, CommandSender sender, int page) {

        List<CommandRegistry> commands = getAllowedCommands(sender);

        if(commands.isEmpty()) {
            messages.get("commands-empty").sendMessage(sender);
            return true;
        }

        if(!messages.has("help-format")) {

            commands.forEach(command -> {
                CommandDescription description = command.description;

                if(messages.has(description.getDescription())) {
                    messages.get(description.getDescription()).sendMessage(sender);
                } else {
                    Utils.sendMessage(sender, description.getDescription());
                }
            });

            return true;
        }

        List<String> helpFormat = messages.get("help-format").toList();
        int count = (int) helpFormat.stream().filter(line -> line.contains("{index}")).count();

        int maxPages = Math.max(1, commands.size()) / Math.max(1, count);

        int fromIndex = page * count;
        int toIndex = Math.min((page + 1) * count, commands.size());

        if (commands.size() < fromIndex) {
            page = 0;
            fromIndex = 0;

            if(commands.size() < count) {
                toIndex = commands.size();
            } else {
                toIndex = count;
            }
        }

        commands = commands.subList(fromIndex, toIndex);

        int index = 0;

        for (String line : helpFormat) {

            if (line.contains("{index}")) {
                if (commands.size() <= index) continue;

                CommandRegistry command = commands.get(index);

                line = line.replace("{index}", Utils.numberFormat((fromIndex + index) + 1));
                line = line.replace("{description}", command.description.getDescription());

                index++;
            }

            line = line.replace("{page}", Utils.numberFormat(page + 1)).replace("{max-page}", Utils.numberFormat(maxPages + 1));
            Utils.sendMessage(sender, line);
        }

        return true;
    }

    protected CommandRegistry getCommand(@NonNull String label) {
        return this.commands.stream().filter(command -> command.getDescription().getCommand().equalsIgnoreCase(label)).findFirst().orElse(null);
    }

    protected List<CommandRegistry> getAllowedCommands(@NonNull CommandSender sender) {
        return this.commands.stream()
                .filter(command -> {
                    CommandDescription description = command.getDescription();

                    return description.getPermission() == null || sender.hasPermission(description.getPermission());
                }).collect(Collectors.toList());
    }

    public abstract boolean execute(Messages messages, CommandSender sender, String label, String[] args);

    public List<String> tab(Messages messages, CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return filter(getAllowedCommands(sender).stream().map(command -> command.getDescription().getCommand()).collect(Collectors.toList()), args);
        }
        return null;
    }
}

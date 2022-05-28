package ru.winlocker.utils.commands;

import lombok.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import ru.winlocker.utils.*;
import ru.winlocker.utils.messages.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

@Getter
@RequiredArgsConstructor
public class CommandManager implements CommandExecutor, TabCompleter {

    private final @NonNull Messages messages;
    private final Map<CommandDescription, CommandSub> commands = new LinkedHashMap<>();

    public void register(@NonNull CommandSub command) {
        Class<? extends  CommandSub> clazz = command.getClass();

        if(!clazz.isAnnotationPresent(CommandDescription.class))
            throw new IllegalArgumentException("Command class " + clazz.getName() + " is not annotated @CommandDescription");

        CommandDescription description = clazz.getAnnotation(CommandDescription.class);
        this.commands.put(description, command);
    }

    @SneakyThrows
    @Deprecated
    public void register(Class<? extends CommandSub> clazz) {
        if(!clazz.isAnnotationPresent(CommandDescription.class))
            throw new IllegalArgumentException("Command class " + clazz.getName() + " is not annotated @CommandDescription");

        CommandDescription description = clazz.getAnnotation(CommandDescription.class);

        Constructor<? extends CommandSub> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);

        CommandSub command = constructor.newInstance();
        this.commands.put(description, command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length == 0) {
            val commands = getAllowedCommands(sender);

            if(!commands.isEmpty()) {

                commands.forEach(entry -> {
                    val description = entry.getKey().description();

                    if(messages.has(description)) {
                        messages.get(description).sendMessage(sender);
                    } else {
                        Utils.sendMessage(sender, description);
                    }
                });
            } else {
                messages.get("commands-empty").sendMessage(sender);
            }

            return true;
        }

        val entry = getCommand(args[0]);

        if(entry != null) {
            val description = entry.getKey();

            if(description.permission().isEmpty() || messages.hasPermission(sender, description.permission())) {

                if(description.onlyPlayers() && !(sender instanceof Player)) {
                    messages.get("only-players").sendMessage(sender);

                } else {
                    val argsList = new ArrayList<>(Arrays.asList(args));
                    argsList.remove(0);

                    try {
                        val commandSub = entry.getValue();

                        if(!commandSub.execute(messages, sender, argsList.toArray(new String[0]))) {
                            val descriptionMessage = description.description();

                            if(messages.has(descriptionMessage)) {
                                messages.get(descriptionMessage).sendMessage(sender);
                            } else {
                                Utils.sendMessage(sender, descriptionMessage);
                            }
                        }
                    } catch (Exception e) {
                        Utils.sendMessage(sender, "Произошла ошибка при выполнении команды. Обратитесь к администратору.");
                        e.printStackTrace();
                    }
                }
            }
        } else {
            messages.get("unknown").sendMessage(sender);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String level, String[] args) {

        if(args.length == 1) {
            return filter(getAllowedCommands(sender).stream().map(entry -> entry.getKey().command()).collect(Collectors.toList()), args);

        } else if(args.length > 1) {
            val entry = getCommand(args[0]);

            if(entry != null) {
                val description = entry.getKey();

                if(description.onlyPlayers() && !(sender instanceof Player))
                    return null;

                if(!description.permission().isEmpty() && !sender.hasPermission(description.permission()))
                    return null;

                val argsList = new ArrayList<>(Arrays.asList(args));
                argsList.remove(0);

                try {
                    val commandSub = entry.getValue();

                    return filter(commandSub.tab(messages, sender, argsList.toArray(new String[0])), args);
                } catch (Exception e) {
                    Utils.sendMessage(sender, "Произошла ошибка, пожалуйста обратитесь к администратору.");
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    private List<String> filter(List<String> list, String[] args) {
        if(list == null) return null;

        String last = args[args.length - 1];
        List<String> result = new ArrayList<>();

        for (String str : list) {
            if(str.startsWith(last)) {
                result.add(str);
            }
        }
        return result;
    }

    public List<Map.Entry<CommandDescription, CommandSub>> getAllowedCommands(CommandSender sender) {
        return this.commands.entrySet()
                .stream()
                .filter(entry -> {
                    val description = entry.getKey();

                    return description.permission().isEmpty() || sender.hasPermission(description.permission());
                }).collect(Collectors.toList());
    }

    public Map.Entry<CommandDescription, CommandSub> getCommand(String label) {
        return this.commands.entrySet().stream()
                .filter(entry -> entry.getKey().command().equalsIgnoreCase(label))
                .findFirst()
                .orElse(null);
    }
}

package ru.winlocker.utils;

import lombok.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.configuration.file.*;
import org.bukkit.entity.*;

import java.text.*;
import java.util.*;
import java.util.stream.*;

public class Utils {

    public static final String VERSION;
    private static DecimalFormat decimalFormat;

    @Getter
    @Setter
    private static FileConfiguration configuration;

    static {
        VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        Utils.decimalFormat = new DecimalFormat("#0.0");
    }

    public static String color(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> color(List<String> list) {
        return list.stream().map(Utils::color).collect(Collectors.toList());
    }

    public static String getMessage(String path) {
        return getString("messages." + path);
    }

    public static List<String> getMessageList(String path) {
        return getStringList("messages." + path);
    }

    public static String getString(final String path) {
        return getConfiguration().getString(path);
    }

    public static List<String> getStringList(final String path) {
        return getConfiguration().getStringList(path);
    }

    public static int getInt(final String path) {
        return getConfiguration().getInt(path);
    }

    public static double getDouble(final String path) {
        return getConfiguration().getDouble(path);
    }

    public static boolean getBoolean(final String path) {
        return getConfiguration().getBoolean(path);
    }

    public static boolean has(final CommandSender player, final String permission) {
        if (!player.hasPermission(permission)) {
            sendMessage(player, getMessage("no-permission"));
            return false;
        }
        return true;
    }


    public static String numberFormat(final double number) {
        return decimalFormat.format(number);
    }

    public static String numberFormat(final int number) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return numberFormat.format(number);
    }

    public static String format(int time) {

        int days = time / 86400;
        int hours = time % 86400 / 3600;
        int minutes = time % 3600 / 60;
        int seconds = time % 60;

        StringBuilder builder = new StringBuilder();

        if (days > 0) {
            builder.append(getString("time.days").replace("{size}", String.valueOf(days))).append(" ");
        }
        if (hours > 0) {
            builder.append(getString("time.hours").replace("{size}", String.valueOf(hours))).append(" ");
        }
        if (minutes > 0) {
            builder.append(getString("time.minutes").replace("{size}", String.valueOf(minutes))).append(" ");
        }
        if (seconds > 0) {
            builder.append(getString("time.seconds").replace("{size}", String.valueOf(seconds))).append(" ");
        }

        final String format = builder.toString().trim().isEmpty() ? getString("time.now") : builder.toString().trim();
        return color(format);
    }

    public static void sendMessage(CommandSender sender, String text) {
        sendMessage(sender, text, true);
    }

    public static void sendMessage(CommandSender sender, String text, boolean prefix) {
        for (String line : text.split(";")) {
            line = line.trim();

            if(line.startsWith("title:")) {

                if(sender instanceof Player) {
                    String[] args = line.split("title:")[1].split("%nl%");

                    String title = color(args[0].trim());
                    String subTitle = null;

                    if(args.length > 1) {
                        subTitle = color(args[1].trim());
                    }

                    ((Player) sender).sendTitle(title, subTitle, 15, 60, 15);
                }
            } else {
                sender.sendMessage(color((prefix ? getMessage("prefix") : "") + line));
            }
        }
    }
}

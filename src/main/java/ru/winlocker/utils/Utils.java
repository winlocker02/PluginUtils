package ru.winlocker.utils;

import com.cryptomorin.xseries.messages.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.text.*;
import java.util.*;
import java.util.stream.*;

public class Utils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.0");

    public static String color(final String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> color(List<String> list) {
        return list.stream().map(Utils::color).collect(Collectors.toList());
    }

    public static String numberFormat(double number) {
        return DECIMAL_FORMAT.format(number);
    }

    public static String numberFormat(int number) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        return numberFormat.format(number);
    }

    public static void sendMessage(CommandSender sender, String text) {
        for (String line : text.split(";")) {
            line = line.trim();

            if(line.startsWith("title:")) {

                if (sender instanceof Player) {
                    String[] args = line.split("title:")[1].split("%nl%");

                    String title = color(args[0].trim());
                    String subTitle = null;

                    if (args.length > 1) {
                        subTitle = color(args[1].trim());
                    }

                    Titles.sendTitle((Player) sender, 15, 60, 15, title, subTitle);
                }
            } else if(line.startsWith("actionbar:")) {
                if(sender instanceof Player) {
                    ActionBar.sendActionBar((Player) sender, color(line.split("actionbar:")[1]));
                }
            } else {
                sender.sendMessage(color(line));
            }
        }
    }
}

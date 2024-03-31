package ru.winlocker.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.*;
import lombok.NonNull;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.text.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;

public class Utils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#0.0");
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String color(@NonNull String text) {
        if(XMaterial.supports(16)) {
            Matcher matcher = HEX_PATTERN.matcher(text);

            while (matcher.find()) {
                String hexCode = text.substring(matcher.start(), matcher.end());
                String replaceSharp = hexCode.replace('#', 'x');

                char[] ch = replaceSharp.toCharArray();
                StringBuilder builder = new StringBuilder();
                for (char c : ch) {
                    builder.append("&").append(c);
                }

                text = text.replace(hexCode, builder.toString());
                matcher = HEX_PATTERN.matcher(text);
            }
        }
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
        sendMessage(sender, null, text);
    }

    public static void sendMessage(CommandSender sender, String prefix, String text) {
        for (String line : text.split(";")) {

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
                sender.sendMessage(color((prefix != null ? prefix : "") + line));
            }
        }
    }
}

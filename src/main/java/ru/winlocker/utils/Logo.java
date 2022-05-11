package ru.winlocker.utils;

import lombok.*;
import org.bukkit.*;
import org.bukkit.plugin.*;

import static ru.winlocker.utils.Utils.*;

public class Logo {

    public static void printLogo(@NonNull Plugin plugin) {
        val consoleSender = Bukkit.getConsoleSender();

        consoleSender.sendMessage("");
        consoleSender.sendMessage(color("&d&l┏"));
        consoleSender.sendMessage(color("&d&l| &d" + plugin.getName() + " &f- Версия: &c" + plugin.getDescription().getVersion()));
        consoleSender.sendMessage(color("&d&l| &fСоздатель плагина &dWinLocker &f- &dvk.com/winlocker02"));
        consoleSender.sendMessage(color("&d&l┗"));
        consoleSender.sendMessage("");
    }
}

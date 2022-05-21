package ru.winlocker.utils;

import com.cryptomorin.xseries.*;
import org.bukkit.*;

import java.util.regex.*;

public class Version {

    private static final int VERSION;

    static {
        String version = Bukkit.getVersion();
        Matcher matcher = Pattern.compile("MC: \\d\\.(\\d+)").matcher(version);

        if (matcher.find()) VERSION = Integer.parseInt(matcher.group(1));
        XMaterial.FEATHER
        else throw new IllegalArgumentException("Failed to parse server version from: " + version);
    }

    public static boolean supports(int version) {
        return VERSION >= version;
    }
}

package ru.winlocker.utils;

import lombok.*;
import ru.winlocker.utils.config.annotations.*;

import java.util.*;

public class TimeUtils {

    @ConfigName("times")
    private static final Map<TimeType, String> FORMATS = new HashMap<>();

    public static void addDefaultFormats() {
        FORMATS.put(TimeType.DAYS, "&c{size} &fдн.");
        FORMATS.put(TimeType.HOURS, "&c{size} &fчас.");
        FORMATS.put(TimeType.MINUTES, "&c{size} &fмин.");
        FORMATS.put(TimeType.SECONDS, "&c{size} &fсек.");
        FORMATS.put(TimeType.NOW, "&cСейчас");
    }

    public static String format(@NonNull Number number, TimeType...excludes) {
        int timeToSeconds = number.intValue();

        int days = timeToSeconds / 86400;
        int hours = timeToSeconds % 86400 / 3600;
        int minutes = timeToSeconds % 3600 / 60;
        int seconds = timeToSeconds % 60;

        val stringBuilder = new StringBuilder();

        List<TimeType> excludesList = excludes != null ? Arrays.asList(excludes) : new ArrayList<>();

        if(days > 0 && (!excludesList.contains(TimeType.DAYS) && FORMATS.containsKey(TimeType.DAYS))) {
            stringBuilder.append(FORMATS.get(TimeType.DAYS).replace("{size}", Utils.numberFormat(days))).append(" ");
        }

        if(hours > 0 && (!excludesList.contains(TimeType.HOURS) && FORMATS.containsKey(TimeType.HOURS))) {
            stringBuilder.append(FORMATS.get(TimeType.HOURS).replace("{size}", Utils.numberFormat(hours))).append(" ");
        }

        if(minutes > 0 && (!excludesList.contains(TimeType.MINUTES) && FORMATS.containsKey(TimeType.MINUTES))) {
            stringBuilder.append(FORMATS.get(TimeType.MINUTES).replace("{size}", Utils.numberFormat(minutes))).append(" ");
        }

        if(seconds > 0 && (!excludesList.contains(TimeType.SECONDS) && FORMATS.containsKey(TimeType.SECONDS))) {
            stringBuilder.append(FORMATS.get(TimeType.SECONDS).replace("{size}", Utils.numberFormat(seconds))).append(" ");
        }

        String format = stringBuilder.toString();

        if(format.isEmpty() && !excludesList.contains(TimeType.NOW) && FORMATS.containsKey(TimeType.NOW)) {
            format = FORMATS.get(TimeType.NOW);
        } else {
            format = "";
        }

        return format;
    }

    public enum TimeType {
        DAYS, HOURS, MINUTES, SECONDS, NOW
    }
}

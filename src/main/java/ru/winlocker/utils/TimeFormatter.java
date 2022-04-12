package ru.winlocker.utils;

import lombok.*;
import ru.winlocker.utils.config.annotations.*;

import java.util.*;

@Getter
@ConfigMappable
public class TimeFormatter {

    @ConfigName("formats")
    private final Map<TimeType, String> formats = new HashMap<>();
    @ConfigName("excludes")
    private final List<TimeType> excludes = new ArrayList<>();

    public String format(@NonNull Number number) {
        int timeToSeconds = number.intValue();

        int days = timeToSeconds / 86400;
        int hours = timeToSeconds % 86400 / 3600;
        int minutes = timeToSeconds % 3600 / 60;
        int seconds = timeToSeconds % 60;

        val stringBuilder = new StringBuilder();

        if(days > 0 && !excludes.contains(TimeType.DAYS) && formats.containsKey(TimeType.DAYS)) {
            stringBuilder.append(formats.get(TimeType.DAYS).replace("{size}", Utils.numberFormat(days))).append(" ");
        }

        if(hours > 0 && !excludes.contains(TimeType.HOURS) && formats.containsKey(TimeType.HOURS)) {
            stringBuilder.append(formats.get(TimeType.HOURS).replace("{size}", Utils.numberFormat(hours))).append(" ");
        }

        if(minutes > 0 && !excludes.contains(TimeType.MINUTES) && formats.containsKey(TimeType.MINUTES)) {
            stringBuilder.append(formats.get(TimeType.MINUTES).replace("{size}", Utils.numberFormat(minutes))).append(" ");
        }

        if(seconds > 0 && !excludes.contains(TimeType.SECONDS) && formats.containsKey(TimeType.SECONDS)) {
            stringBuilder.append(formats.get(TimeType.SECONDS).replace("{size}", Utils.numberFormat(seconds))).append(" ");
        }

        String format = stringBuilder.toString().trim();

        if(format.isEmpty() && !excludes.contains(TimeType.NOW) && formats.containsKey(TimeType.NOW)) {
            format = formats.get(TimeType.NOW);
        }

        return format;
    }

    public enum TimeType {
        DAYS, HOURS, MINUTES, SECONDS, NOW
    }
}

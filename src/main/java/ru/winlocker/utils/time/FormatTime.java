package ru.winlocker.utils.time;

import lombok.*;
import ru.winlocker.utils.*;
import ru.winlocker.utils.config.annotations.*;

import java.util.*;
import java.util.function.*;

@Getter
@ConfigMappable
@NoArgsConstructor(staticName = "create")
public class FormatTime {

    @ConfigName
    private final Map<TimeType, String> formats = new HashMap<>();

    public String format(@NonNull Number number) {
        int timeToSeconds = number.intValue();

        var stringBuilder = new StringBuilder();

        for (TimeType timeType : TimeType.values()) {

            if(timeType != TimeType.NOW && this.formats.containsKey(timeType)) {
                int time = timeType.math(timeToSeconds);

                if(time > 0) {
                    stringBuilder.append(this.formats.get(timeType).replace("{size}", Utils.numberFormat(time))).append(" ");
                }
            }
        }

        String format = stringBuilder.toString().trim();

        if(this.formats.containsKey(TimeType.NOW) && format.isEmpty()) {
            format = this.formats.get(TimeType.NOW);
        }

        return format;
    }

    @AllArgsConstructor
    public enum TimeType {
        DAYS (value -> value / 86400),
        HOURS (value -> value % 86400 / 3600),
        MINUTES (value -> value % 3600 / 60),
        SECONDS (value -> value % 60),
        NOW (value -> value);

        private final Function<Integer, Integer> mathFunction;

        public int math(int value) {
            return this.mathFunction.apply(value);
        }
    }
}

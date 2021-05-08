package com.dbw.util;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class TimeDiffUtils {
    private static String DAYS_SYMBOL = "d";
    private static String HOURS_SYMBOL = "h";
    private static String MINUTES_SYMBOL = "m";
    private static String SECONDS_SYMBOL = "s";
    private static String MILLIS_SYMBOL = "ms";
    private static String SPACE = " ";

    public static long getTimeDiffInMillis(Timestamp lastAuditRecordsTime, Timestamp currentAuditRecordsTime) {
        LocalDateTime lastAuditRecordsLocalDateTime = lastAuditRecordsTime.toLocalDateTime();
        LocalDateTime currentAuditRecordsLocalDateTime = currentAuditRecordsTime.toLocalDateTime();
        Duration timeDiff = Duration.between(lastAuditRecordsLocalDateTime, currentAuditRecordsLocalDateTime);
        return timeDiff.toMillis();
    }

    public static String getTimeFormattedDiff(long timeDiffInMillis) {
        long days = TimeUnit.MILLISECONDS.toDays(timeDiffInMillis);
        timeDiffInMillis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(timeDiffInMillis);
        timeDiffInMillis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeDiffInMillis);
        timeDiffInMillis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(timeDiffInMillis);
        long millis = timeDiffInMillis - TimeUnit.SECONDS.toMillis(seconds);

        StringBuilder builder = new StringBuilder();
        if (days > 0) {
            builder.append(days + DAYS_SYMBOL);
            builder.append(SPACE);
        }
        if (hours > 0 || builder.length() > 0) {
            builder.append(hours + HOURS_SYMBOL);
            builder.append(SPACE);
        }
        if (minutes > 0 || builder.length() > 0) {
            builder.append(minutes + MINUTES_SYMBOL);
            builder.append(SPACE);
        }
        if (seconds > 0 || builder.length() > 0) {
            builder.append(seconds + SECONDS_SYMBOL);
            builder.append(SPACE);
        }
        builder.append(millis + MILLIS_SYMBOL);
        return builder.toString();
    }
}

package com.dbw.output;

import com.dbw.app.App;
import com.dbw.util.StringUtils;
import com.dbw.util.TimeDiffUtils;

import java.sql.Timestamp;
import java.util.Optional;

public class TimeDiffSeparator implements OutputBuilder {
    private static final Short DEFAULT_TIME_DIFF_SEP_MIN_VAL = 5000;
    private static final short INTERNAL_PADDING_LENGTH = 2;

    private final String timeDiff;

    public TimeDiffSeparator(String timeDiff) {
        this.timeDiff = timeDiff;
    }

    public static Optional<TimeDiffSeparator> create(Timestamp lastAuditRecordsTime, Timestamp currentAuditRecordsTime) {
        if (lastAuditRecordsTime == null) {
            return Optional.empty();
        }
        long timeDiffInMillis = TimeDiffUtils.getTimeDiffInMillis(lastAuditRecordsTime, currentAuditRecordsTime);
        short timeDiffSepMinVal =
                App.options.getTimeDiffSeparatorMinVal() != null ? App.options.getTimeDiffSeparatorMinVal() : DEFAULT_TIME_DIFF_SEP_MIN_VAL;
        if (timeDiffInMillis > timeDiffSepMinVal) {
            String formattedTimeDiff = TimeDiffUtils.getTimeFormattedDiff(timeDiffInMillis);
            TimeDiffSeparator ts = new TimeDiffSeparator(formattedTimeDiff);
            return Optional.of(ts);
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        String separatorRow = createSeparatorRow();
        StringBuilder builder = new StringBuilder();
        builder.append(separatorRow);
        builder.append(separatorRow);
        builder.append(separatorRow);
        builder.append(createTimeDiffRow());
        builder.append(separatorRow);
        builder.append(separatorRow);
        builder.append(separatorRow);
        return builder.toString();
    }

    private String createSeparatorRow() {
        StringBuilder builder = new StringBuilder();
        builder.append(createInternalPadding());
        builder.append(NEW_LINE);
        return builder.toString();
    }

    private String createInternalPadding() {
        int timeDiffLength = timeDiff.length();
        int totalLength = timeDiffLength + INTERNAL_PADDING_LENGTH * 2;
        int dotPosition = Math.floorDiv(totalLength, 2);
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.multiplyNTimes(dotPosition, PADDING));
        builder.append(DOT);
        builder.append(StringUtils.multiplyNTimes(totalLength - dotPosition - 1, PADDING));
        return builder.toString();
    }

    private String createTimeDiffRow() {
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.multiplyNTimes(INTERNAL_PADDING_LENGTH, PADDING));
        builder.append(timeDiff);
        builder.append(StringUtils.multiplyNTimes(INTERNAL_PADDING_LENGTH, PADDING));
        builder.append(NEW_LINE);
        return builder.toString();
    }

}

package com.dbw.output;

import com.dbw.app.App;
import com.dbw.util.StringUtils;
import com.dbw.util.TimeDiffUtils;

import java.sql.Timestamp;
import java.util.Optional;

public class TimeDiffSeparator implements OutputBuilder {
    private static final Short DEFAULT_TIME_DIFF_SEP_MIN_VAL = 5000;

    public static Optional<TimeDiffSeparator> create(Timestamp lastAuditRecordsTime, Timestamp currentAuditRecordsTime) {
        if (lastAuditRecordsTime == null) {
            return Optional.empty();
        }
        long timeDiffInMillis = TimeDiffUtils.getTimeDiffInMillis(lastAuditRecordsTime, currentAuditRecordsTime);
        short timeDiffSepMinVal =
                App.options.getTimeDiffSeparatorMinVal() != null ? App.options.getTimeDiffSeparatorMinVal() : DEFAULT_TIME_DIFF_SEP_MIN_VAL;
        if (timeDiffInMillis > timeDiffSepMinVal) {
            TimeDiffSeparator ts = new TimeDiffSeparator();
            return Optional.of(ts);
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        String separatorRow = createSeparatorRow();
        StringBuilder builder = new StringBuilder();
        for (short i = 0; i < 6; i++) {
            builder.append(separatorRow);
        }
        return builder.toString();
    }

    private String createSeparatorRow() {
        StringBuilder builder = new StringBuilder();
        for (short i = 0; i < 3; i++) {
            builder.append(PADDING);
        }
        builder.append(DOT);
        builder.append(NEW_LINE);
        return builder.toString();
    }
}

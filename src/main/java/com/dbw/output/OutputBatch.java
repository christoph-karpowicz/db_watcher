package com.dbw.output;

import com.dbw.frame.AuditFrame;
import com.dbw.util.TimeDiffUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class OutputBatch extends ArrayList<AuditFrame> {
    private Timestamp previousTime;

    public OutputBatch(Timestamp previousTime) {
        this.previousTime = previousTime;
    }

    public Timestamp getPreviousTime() {
        return previousTime;
    }

    public void sort() {
        this.sort(Comparator.comparing(frameA -> frameA.getAuditRecord().getTimestamp()));
    }

    public void calculateTimes() {
        for (int i = 0; i < this.size(); i++) {
            long timeDiffInMillis;
            AuditFrame currentFrame = this.get(i);
            if (i == 0 && previousTime == null) {
                continue;
            } else if (i == 0) {
                timeDiffInMillis =
                        TimeDiffUtils.getTimeDiffInMillis(previousTime, currentFrame.getAuditRecord().getTimestamp());
            } else {
                AuditFrame previousFrame = this.get(i - 1);
                timeDiffInMillis =
                        TimeDiffUtils.getTimeDiffInMillis(previousFrame.getAuditRecord().getTimestamp(), currentFrame.getAuditRecord().getTimestamp());
            }
            String formattedTimeDiff = TimeDiffUtils.getTimeFormattedDiff(timeDiffInMillis);
            currentFrame.setTimeSincePrevious(formattedTimeDiff);
        }
    }

    public void output() {
        this.forEach(this::outputFrame);
    }

    private void outputFrame(AuditFrame frame) {
        Timestamp currentRecordsTime = frame.getAuditRecord().getTimestamp();
        Optional<TimeDiffSeparator> timeSeparator =
                TimeDiffSeparator.create(previousTime, currentRecordsTime);
        timeSeparator.ifPresent(separator -> System.out.println(separator.toString()));
        System.out.println(frame.toString());
        previousTime = currentRecordsTime;
    }
}

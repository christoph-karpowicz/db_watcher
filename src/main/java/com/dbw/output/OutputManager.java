package com.dbw.output;

import com.dbw.app.App;
import com.dbw.err.WatcherRunException;
import com.dbw.frame.AuditFrame;
import com.dbw.watcher.WatcherManager;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class OutputManager {
    @Inject
    private WatcherManager watcherManager;

    private boolean isRunning = true;
    private Timestamp lastAuditRecordsTime;

    public void pollAndOutput() {
        try {
            while (isRunning) {
                Thread.sleep(App.getInterval());
                List<AuditFrame> frames = getSortedFrames();
                frames.forEach(this::outputFrame);
            }
        } catch (InterruptedException e) {
            new WatcherRunException(e.getMessage(), e).handle();
        }
    }

    private List<AuditFrame> getSortedFrames() {
        List<AuditFrame> frames = Lists.newArrayList();
        watcherManager.getFrameQueue().drainTo(frames);
        return frames
                .stream()
                .sorted(Comparator.comparing(frameA -> frameA.getAuditRecord().getTimestamp()))
                .collect(Collectors.toList());
    }

    private void outputFrame(AuditFrame frame) {
        Timestamp currentRecordsTime = frame.getAuditRecord().getTimestamp();
        Optional<TimeDiffSeparator> timeSeparator =
                TimeDiffSeparator.create(lastAuditRecordsTime, currentRecordsTime);
        timeSeparator.ifPresent(separator -> System.out.println(separator.toString()));
        System.out.println(frame.toString());
        setLastAuditRecordsTime(currentRecordsTime);
    }

    private void setLastAuditRecordsTime(Timestamp lastAuditRecordsTime) {
        this.lastAuditRecordsTime = lastAuditRecordsTime;
    }
}

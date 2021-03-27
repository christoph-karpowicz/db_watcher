package com.dbw.output;

import com.dbw.app.App;
import com.dbw.err.WatcherRunException;
import com.dbw.frame.AuditFrame;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
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

    private Timestamp lastAuditRecordsTime;

    public void pollAndOutput() {
        boolean outputInitialInfoDone = false;
        try {
            while (true) {
                Thread.sleep(App.getInterval());
                OutputBatch frames = getSortedFrames();
                frames.output();
                if (!outputInitialInfoDone && watcherManager.areAllAfterInitialRun()) {
                    outputInitialInfo();
                    outputInitialInfoDone = true;
                }
                setLastAuditRecordsTime(frames.getPreviousTime());
            }
        } catch (InterruptedException e) {
            new WatcherRunException(e.getMessage(), e).handle();
        }
    }

    private OutputBatch getSortedFrames() {
        OutputBatch frames = new OutputBatch(lastAuditRecordsTime);
        watcherManager.getFrameQueue().drainTo(frames);
        frames.sort();
        frames.calculateTimes();
        return frames;
    }

    private void outputInitialInfo() {
        watcherManager.outputInitialInfo();
    }

    private void setLastAuditRecordsTime(Timestamp lastAuditRecordsTime) {
        this.lastAuditRecordsTime = lastAuditRecordsTime;
    }
}

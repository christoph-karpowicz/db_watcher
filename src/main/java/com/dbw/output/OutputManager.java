package com.dbw.output;

import com.dbw.app.App;
import com.dbw.err.UnrecoverableException;
import com.dbw.watcher.WatcherManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.Timestamp;

@Singleton
public class OutputManager {
    private final short INTERVAL = 50;

    @Inject
    private WatcherManager watcherManager;

    private Timestamp lastAuditRecordsTime;

    public void pollAndOutput() {
        boolean outputInitialInfoDone = false;
        try {
            while (true) {
                Thread.sleep(INTERVAL);
                if (watcherManager.areAllCheckedIn() || App.options.getOneOff()) {
                    OutputBatch frames = getSortedFrames();
                    frames.output();
                    if (!outputInitialInfoDone && watcherManager.areAllAfterInitialRun()) {
                        outputInitialInfo();
                        outputInitialInfoDone = true;
                    }
                    setLastAuditRecordsTime(frames.getPreviousTime());
                    watcherManager.checkOutAll();
                }
                if (outputInitialInfoDone && App.options.getOneOff()) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            new UnrecoverableException("WatcherRunException", e.getMessage(), e).handle();
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

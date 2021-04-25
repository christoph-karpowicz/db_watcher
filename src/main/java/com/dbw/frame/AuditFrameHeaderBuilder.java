package com.dbw.frame;

import com.dbw.cfg.DatabaseConfig;
import com.dbw.db.AuditRecord;
import com.dbw.diff.TableDiffBuilder;
import com.dbw.output.OutputBuilder;
import com.dbw.watcher.WatcherManager;
import com.google.inject.Inject;

import javax.inject.Singleton;

@Singleton
public class AuditFrameHeaderBuilder implements OutputBuilder {
    @Inject
    private WatcherManager watcherManager;

    public AuditFrameHeader build(AuditRecord auditRecord, String frameNo, DatabaseConfig dbConfig) {
        AuditFrameHeader header = new AuditFrameHorizontalHeader();
        header.setIncludeDbInfo(watcherManager.getWatchersSize() > 1);
        header.prepare(auditRecord, frameNo, dbConfig);
        int tableWidth = header.getRowWidth();
        if (tableWidth > TableDiffBuilder.getMaxRowWidth()) {
            header = new AuditFrameVerticalHeader();
            header.setIncludeDbInfo(watcherManager.getWatchersSize() > 1);
            header.prepare(auditRecord, frameNo, dbConfig);
        }
        return header;
    }
}

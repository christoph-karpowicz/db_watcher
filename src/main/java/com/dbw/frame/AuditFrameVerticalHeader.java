package com.dbw.frame;

import com.dbw.cfg.DatabaseConfig;
import com.dbw.db.AuditRecord;
import com.google.common.collect.Lists;

public class AuditFrameVerticalHeader extends AuditFrameHeader {

    public void prepare(AuditRecord auditRecord, String frameNo, DatabaseConfig dbConfig) {
        headerTable.addRow(Lists.newArrayList(FRAME_HEADER_NO, frameNo));
        if (includeDbInfo) {
            headerTable.addRow(Lists.newArrayList(FRAME_HEADER_DB_NAME, dbConfig.getName()));
            headerTable.addRow(Lists.newArrayList(FRAME_HEADER_DB_TYPE, dbConfig.getType()));
        }
        headerTable.addRow(Lists.newArrayList(FRAME_HEADER_TABLE, auditRecord.getTableName()));
        headerTable.addRow(Lists.newArrayList(FRAME_HEADER_OPERATION, auditRecord.getOperation().name()));
        headerTable.addRow(Lists.newArrayList(FRAME_HEADER_TIMESTAMP, auditRecord.getFormattedTimestamp()));

        headerTable.calculateRowWidth();
    }
}

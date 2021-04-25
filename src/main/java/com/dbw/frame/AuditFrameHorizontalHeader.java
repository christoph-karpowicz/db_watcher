package com.dbw.frame;

import com.dbw.cfg.DatabaseConfig;
import com.dbw.db.AuditRecord;
import com.google.common.collect.Lists;

import java.util.List;

public class AuditFrameHorizontalHeader extends AuditFrameHeader {

    public void prepare(AuditRecord auditRecord, String frameNo, DatabaseConfig dbConfig) {
        List<String> headerRow = Lists.newArrayList();
        headerRow.add(FRAME_HEADER_NO);
        if (includeDbInfo) {
            headerRow.add(FRAME_HEADER_DB_NAME);
            headerRow.add(FRAME_HEADER_DB_TYPE);
        }
        headerRow.add(FRAME_HEADER_TABLE);
        headerRow.add(FRAME_HEADER_OPERATION);
        headerRow.add(FRAME_HEADER_TIMESTAMP);
        headerTable.addRow(headerRow);

        List<String> valueRow = Lists.newArrayList();
        valueRow.add(frameNo);
        if (includeDbInfo) {
            valueRow.add(dbConfig.getName());
            valueRow.add(dbConfig.getType());
        }
        valueRow.add(auditRecord.getTableName());
        valueRow.add(auditRecord.getOperation().name());
        valueRow.add(auditRecord.getFormattedTimestamp());
        headerTable.addRow(valueRow);
        headerTable.calculateRowWidth();
    }
}

package com.dbw.frame;

import com.dbw.cfg.DatabaseConfig;
import com.dbw.db.AuditRecord;
import com.dbw.output.OutputBuilder;

public abstract class AuditFrameHeader implements OutputBuilder {
    protected AuditFrameHeaderTable headerTable = new AuditFrameHeaderTable();
    protected boolean includeDbInfo;

    abstract void prepare(AuditRecord auditRecord, String frameNo, DatabaseConfig dbConfig);

    public void setIncludeDbInfo(boolean includeDbInfo) {
        this.includeDbInfo = includeDbInfo;
    }

    public int getRowWidth() {
        return headerTable.getRowWidth();
    }

    @Override
    public String toString() {
        return headerTable.build();
    }
}

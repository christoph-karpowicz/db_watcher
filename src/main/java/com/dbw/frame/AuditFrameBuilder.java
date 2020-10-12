package com.dbw.frame;

import java.util.List;

import com.dbw.db.AuditRecord;
import com.dbw.db.Operation;
import com.dbw.diff.DiffService;
import com.dbw.diff.StateColumn;
import com.dbw.output.OutputBuilder;

public class AuditFrameBuilder implements OutputBuilder {
    private AuditRecord auditRecord;
    private List<StateColumn> stateColumns;
    private DiffService diffService;
    private StringBuilder builder;

    public AuditFrameBuilder(DiffService diffService, AuditRecord auditRecord, List<StateColumn> stateColumns) {
        this.diffService = diffService;
        this.auditRecord = auditRecord;
        this.stateColumns = stateColumns;
        builder = new StringBuilder();
    }

    public void build() {
        builder.append(HR);
        builder.append(NEW_LINE);
        builder.append("ID: " + auditRecord.getId());
        builder.append(NEW_LINE);
        builder.append("Table: " + auditRecord.getTableName());
        builder.append(NEW_LINE);
        builder.append("Operation: " + Operation.valueOfSymbol(auditRecord.getOperation()));
        builder.append(NEW_LINE);
        builder.append("Timestamp: " + auditRecord.getTimestamp());
        builder.append(NEW_LINE);
        builder.append(findDiff());
    }

    public String findDiff() {
        return diffService.findDiff(stateColumns, auditRecord);
    }

    public String findVerboseDiffs() {
        StringBuilder builder = new StringBuilder();

        stateColumns.forEach(stateColumn -> {
            if (stateColumn.isCut()) {
                builder.append(findVerboseDiff(stateColumn));
            }
        });
        
        return builder.toString();
    }

    public String findVerboseDiff(StateColumn stateColumn) {

        StringBuilder builder = new StringBuilder();
        return builder.toString();
    }

    @Override
    public String toString() {
        return builder.toString();
    }
    
}

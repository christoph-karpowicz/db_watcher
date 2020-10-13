package com.dbw.frame;

import java.util.List;

import com.dbw.db.AuditRecord;
import com.dbw.db.Operation;
import com.dbw.diff.StateColumn;
import com.dbw.diff.StateDiffService;
import com.dbw.output.OutputBuilder;
import com.google.inject.Inject;

public class AuditFrameBuilder implements OutputBuilder {
    private AuditRecord auditRecord;
    private List<StateColumn> stateColumns;
    private StringBuilder builder;
    private Operation dbOperation;

    @Inject
    private StateDiffService diffService;

    public void setAuditRecord(AuditRecord auditRecord) {
        this.auditRecord = auditRecord;
    }

    public void setStateColumns(List<StateColumn> stateColumns) {
        this.stateColumns = stateColumns;
    }

    public void init() {
        builder = new StringBuilder();
        dbOperation = Operation.valueOfSymbol(auditRecord.getOperation());
    }
    
    public void build() {
        builder.append(HR);
        builder.append(NEW_LINE);
        builder.append("ID: " + auditRecord.getId());
        builder.append(NEW_LINE);
        builder.append("Table: " + auditRecord.getTableName());
        builder.append(NEW_LINE);
        builder.append("Operation: " + dbOperation);
        builder.append(NEW_LINE);
        builder.append("Timestamp: " + auditRecord.getTimestamp());
        builder.append(NEW_LINE);
        builder.append(findTableDiff());
        if (dbOperation.equals(Operation.UPDATE)) {
            builder.append(findColumnDiffs());
        }
    }

    public String findTableDiff() {
        return diffService.findTableDiff(stateColumns, dbOperation);
    }

    public String findColumnDiffs() {
        StringBuilder columnDiffBuilder = new StringBuilder();

        short diffCount = 0;
        for (StateColumn stateColumn : stateColumns) {
            if (stateColumn.hasDiff() && stateColumn.isCut()) {
                columnDiffBuilder.append(diffService.findColumnDiff(stateColumn, ++diffCount));
            }
        }
        
        return columnDiffBuilder.toString();
    }

    @Override
    public String toString() {
        return builder.toString();
    }
    
}

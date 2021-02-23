package com.dbw.frame;

import java.util.Collections;
import java.util.List;

import com.dbw.app.App;
import com.dbw.db.AuditRecord;
import com.dbw.db.Operation;
import com.dbw.diff.StateColumn;
import com.dbw.diff.StateDiffService;
import com.dbw.diff.TableDiffBuilder;
import com.dbw.output.OutputBuilder;
import com.google.inject.Inject;

public class AuditFrameBuilder implements OutputBuilder {
    private AuditRecord auditRecord;
    private List<StateColumn> stateColumns;
    private StringBuilder builder;

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
    }
    
    public void build() {
        builder.append(String.join("", Collections.nCopies(TableDiffBuilder.getMaxRowWidth(), HR)));
        builder.append(DIFF_VERTICAL_BORDER);
        builder.append(NEW_LINE);
        builder.append(FRAME_HEADER_ID + auditRecord.getId());
        builder.append(NEW_LINE);
        builder.append(FRAME_HEADER_TABLE + auditRecord.getTableName());
        builder.append(NEW_LINE);
        builder.append(FRAME_HEADER_OPERATION + auditRecord.getOperation());
        builder.append(NEW_LINE);
        builder.append(FRAME_HEADER_TIMESTAMP + auditRecord.getTimestamp());
        builder.append(NEW_LINE);
        builder.append(findTableDiff());
        if (Operation.UPDATE.equals(auditRecord.getOperation()) && App.options.getVerboseDiff()) {
            builder.append(findVerboseColumnDiffs());
        }
    }

    public String findTableDiff() {
        return diffService.findTableDiff(stateColumns, auditRecord.getOperation());
    }

    public String findVerboseColumnDiffs() {
        StringBuilder columnDiffBuilder = new StringBuilder();

        for (StateColumn stateColumn : stateColumns) {
            if (stateColumn.hasDiff() && stateColumn.isCut()) {
                columnDiffBuilder.append(diffService.findColumnDiff(stateColumn));
            }
        }
        
        return columnDiffBuilder.toString();
    }

    @Override
    public String toString() {
        return builder.toString();
    }
    
}

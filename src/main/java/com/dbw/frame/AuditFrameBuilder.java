package com.dbw.frame;

import com.dbw.app.App;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.db.AuditRecord;
import com.dbw.db.Operation;
import com.dbw.diff.StateColumn;
import com.dbw.diff.StateDiffService;
import com.dbw.diff.TableDiffBuilder;
import com.dbw.output.OutputBuilder;
import com.dbw.util.StringUtils;
import com.google.common.base.Strings;
import com.google.inject.Inject;

import java.util.List;

public class AuditFrameBuilder implements OutputBuilder {
    @Inject
    private AuditFrameHeaderBuilder headerBuilder;

    private StringBuilder builder;
    private DatabaseConfig dbConfig;
    private AuditRecord auditRecord;
    private List<StateColumn> stateColumns;
    private String timeSincePrevious;
    private String frameNo;

    @Inject
    private StateDiffService diffService;

    public void setDbConfig(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public void setAuditRecord(AuditRecord auditRecord) {
        this.auditRecord = auditRecord;
    }

    public void setStateColumns(List<StateColumn> stateColumns) {
        this.stateColumns = stateColumns;
    }

    public void setTimeSincePrevious(String timeSincePrevious) {
        this.timeSincePrevious = timeSincePrevious;
    }

    public void setFrameNo(int frameNo) {
        this.frameNo = String.valueOf(frameNo);
    }

    public void init() {
        builder = new StringBuilder();
    }
    
    public void build() {
        String headerTable = buildHeaderTable();
        builder.append(buildSeparator(headerTable.length()));
        builder.append(NEW_LINE);
        builder.append(headerTable);
        builder.append(NEW_LINE);
        builder.append(findTableDiff());
        if (Operation.UPDATE.equals(auditRecord.getOperation()) && App.options.getVerboseDiff()) {
            builder.append(findVerboseColumnDiffs());
        }
    }

    private String buildSeparator(int headerTableLength) {
        int lineLength;
        StringBuilder sb = new StringBuilder();
        if (!Strings.isNullOrEmpty(timeSincePrevious)) {
            String front = buildSeparatorFrontWithTime();
            lineLength = TableDiffBuilder.getMaxRowWidth() - front.length();
            sb.append(front);
        } else {
            lineLength = TableDiffBuilder.getMaxRowWidth();
        }
        sb.append(StringUtils.multiplyNTimes(lineLength, HR));
        sb.append(VERTICAL_BORDER);
        sb.setCharAt((headerTableLength / 4) - 2, EDGE_BORDER.charAt(0));
        return sb.toString();
    }

    private String buildSeparatorFrontWithTime() {
        StringBuilder sb = new StringBuilder();
        sb.append(HR);
        sb.append(PADDING);
        sb.append(timeSincePrevious);
        sb.append(PADDING);
        return sb.toString();
    }

    private String buildHeaderTable() {
        return headerBuilder.build(auditRecord, frameNo, dbConfig);
    }

    private String findTableDiff() {
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

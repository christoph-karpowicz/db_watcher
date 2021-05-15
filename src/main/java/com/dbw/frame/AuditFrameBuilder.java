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
    @Inject
    private StateDiffService diffService;

    private StringBuilder builder;
    private DatabaseConfig dbConfig;
    private AuditRecord auditRecord;
    private List<StateColumn> stateColumns;
    private String timeSincePrevious;
    private String frameNo;

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
        AuditFrameHeader headerTable = buildHeaderTable();
        builder.append(buildSeparator(headerTable.getRowWidth()));
        builder.append(NEW_LINE);
        builder.append(headerTable);
        if (App.options.getShowQuery() && auditRecord.getQuery() != null) {
            builder.append(FRAME_HEADER_QUERY);
            builder.append(auditRecord.getQuery());
            builder.append(NEW_LINE);
        }
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
        if (headerTableLength - 1 < TableDiffBuilder.getMaxRowWidth()) {
            sb.setCharAt(headerTableLength - 1, EDGE_BORDER.charAt(0));
        }
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

    private AuditFrameHeader buildHeaderTable() {
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

package com.dbw.frame;

import com.dbw.cfg.DatabaseConfig;
import com.dbw.db.AuditRecord;
import com.dbw.output.OutputBuilder;
import com.dbw.util.StringUtils;
import com.dbw.watcher.WatcherManager;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class AuditFrameHeaderBuilder implements OutputBuilder {
    @Inject
    private WatcherManager watcherManager;

    public String build(AuditRecord auditRecord, String frameNo, DatabaseConfig dbConfig) {
        StringBuilder sb = new StringBuilder();
        List<AuditFrameHeaderColumn> columns = createColumns(auditRecord, frameNo, dbConfig);
        buildTableHeaders(sb, columns);
        final int tableLength = sb.length();
        sb.append(NEW_LINE);
        buildTableHorizontalBorder(sb, tableLength, VERTICAL_BORDER);
        sb.append(NEW_LINE);
        buildTableValues(sb, columns);
        sb.append(NEW_LINE);
        buildTableHorizontalBorder(sb, tableLength, EDGE_BORDER);
        sb.append(NEW_LINE);
        return sb.toString();
    }

    private List<AuditFrameHeaderColumn> createColumns(AuditRecord auditRecord, String frameNo, DatabaseConfig dbConfig) {
        List<AuditFrameHeaderColumn> columns = Lists.newArrayList();
        columns.add(new AuditFrameHeaderColumn(FRAME_HEADER_NO, frameNo));
        if (watcherManager.getWatchersSize() > 1) {
            columns.add(new AuditFrameHeaderColumn(FRAME_HEADER_DB_NAME, dbConfig.getName()));
            columns.add(new AuditFrameHeaderColumn(FRAME_HEADER_DB_TYPE, dbConfig.getType()));
        }
        columns.add(new AuditFrameHeaderColumn(FRAME_HEADER_TABLE, auditRecord.getTableName()));
        columns.add(new AuditFrameHeaderColumn(FRAME_HEADER_OPERATION, auditRecord.getOperation().name()));
        columns.add(new AuditFrameHeaderColumn(FRAME_HEADER_TIMESTAMP, auditRecord.getFormattedTimestamp()));
        return columns;
    }

    private void buildTableHeaders(StringBuilder sb, List<AuditFrameHeaderColumn> columns) {
        columns.forEach(column -> {
            sb.append(PADDING);
            sb.append(column.getHeader());
            sb.append(PADDING);
            sb.append(VERTICAL_BORDER);
        });
    }

    private void buildTableValues(StringBuilder sb, List<AuditFrameHeaderColumn> columns) {
        columns.forEach(column -> {
            sb.append(PADDING);
            sb.append(column.getValue());
            sb.append(PADDING);
            sb.append(VERTICAL_BORDER);
        });
    }

    private void buildTableHorizontalBorder(StringBuilder sb, int tableLength, String edge) {
        sb.append(StringUtils.multiplyNTimes(tableLength - 1, HORIZONTAL_BORDER));
        sb.append(edge);
    }
}

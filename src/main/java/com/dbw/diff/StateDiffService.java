package com.dbw.diff;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dbw.app.ObjectCreator;
import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.db.Operation;
import com.dbw.db.Postgres;
import com.dbw.log.ErrorMessages;
import com.dbw.output.OutputBuilder;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StateDiffService implements DiffService {
    private final String DATE_FMT = "MM-dd-yyyy HH:mm:ss";
    
    @Inject
    private TableDiffBuilder tableDiffBuilder;
    @Inject
    private ColumnDiffBuilder columnDiffBuilder;

    public Diff createDiff(Database db, AuditRecord auditRecord) throws Exception {
        Diff diff;
        if (db instanceof Postgres) {
            diff = new JsonDiff((Postgres)db, auditRecord.getTableName());
        } else {
            diff = ObjectCreator.create(XmlDiff.class);
        }
        validateStateData(auditRecord);
        diff.parseOldData(auditRecord.getOldData());
        diff.parseNewData(auditRecord.getNewData());
        return diff;
    }

    private void validateStateData(AuditRecord auditRecord) throws Exception {
        boolean isUpdate = auditRecord.getOperation().equals(Operation.UPDATE);
        boolean isInsert = auditRecord.getOperation().equals(Operation.INSERT);
        boolean isDelete = auditRecord.getOperation().equals(Operation.DELETE);
        if ((isDelete || isUpdate) && Strings.isNullOrEmpty(auditRecord.getOldData())) {
            throw new Exception(String.format(ErrorMessages.STATE_VALIDATION_NULL_OR_EMPTY, auditRecord.getId(), "old"));
        }
        if ((isInsert || isUpdate) && Strings.isNullOrEmpty(auditRecord.getNewData())) {
            throw new Exception(String.format(ErrorMessages.STATE_VALIDATION_NULL_OR_EMPTY, auditRecord.getId(), "new"));
        }
    }

    public String findTableDiff(List<StateColumn> stateColumns, Operation dbOperation) {
        tableDiffBuilder.init();
        tableDiffBuilder.build(getStateColumnsDividedIntoRows(stateColumns), dbOperation);
        return tableDiffBuilder.toString();
    }

    private List<List<StateColumn>> getStateColumnsDividedIntoRows(List<StateColumn> stateColumns) {
        List<List<StateColumn>> rows = new ArrayList<List<StateColumn>>();
        int paddingLength = OutputBuilder.PADDING.length() * 2;
        short characterCount = 0;
        short rowNumber = -1;
        for (StateColumn stateColumn : stateColumns) {
            int stateColumnLength = 
                (stateColumn.getMaxLength() > TableDiffBuilder.getMaxColumnLength() ? TableDiffBuilder.getMaxColumnLength() : stateColumn.getMaxLength());
            stateColumnLength += paddingLength;
            if (rowNumber < 0 || characterCount + stateColumnLength > TableDiffBuilder.getMaxRowLength()) {
                List<StateColumn> newRow = new ArrayList<StateColumn>();
                rows.add(newRow);
                rowNumber++;
                characterCount = 0;
            } else {
                characterCount += stateColumnLength;
            }
            rows.get(rowNumber).add(stateColumn);
        }
        return rows;
    } 

    public String findColumnDiff(StateColumn stateColumn, short count) {
        columnDiffBuilder.init();
        columnDiffBuilder.build(stateColumn, count);
        return columnDiffBuilder.toString();
    }

    public String stateValueToString(Object value) {
        if (value instanceof Integer) {
            return Integer.toString((Integer)value);
        } else if (value instanceof Double) {
            return Double.toString((Double)value);
        } else if (value instanceof Short) {
            return Short.toString((Short)value);
        } else if (value instanceof Long) {
            return Long.toString((Long)value);
        } else if (value instanceof Boolean) {
            return Boolean.toString((Boolean)value);
        } else if (value instanceof Character) {
            return Character.toString((Character)value);
        } else if (value instanceof Date || value instanceof Timestamp) {
            DateFormat df = new SimpleDateFormat(DATE_FMT);
            return df.format((Date)value);
        } else if (value instanceof ArrayList) {
            return String.join(", ", (ArrayList)value);
        }
        return (String)value;
    }
    
}

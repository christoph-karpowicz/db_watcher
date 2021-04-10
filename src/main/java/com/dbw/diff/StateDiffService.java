package com.dbw.diff;

import com.dbw.app.ObjectCreator;
import com.dbw.db.*;
import com.dbw.err.RecoverableException;
import com.dbw.err.RecoverableException;
import com.dbw.output.OutputBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Singleton
public class StateDiffService implements DiffService {
    private final String DATE_FMT = "MM.dd.yyyy HH:mm:ss";

    @Inject
    private TableDiffBuilder tableDiffBuilder;
    @Inject
    private ColumnDiffBuilder columnDiffBuilder;

    public Diff createDiff(Database db, AuditRecord auditRecord) throws RecoverableException, SQLException {
        Diff diff;
        if (db instanceof Postgres) {
            String[] tableColumnNames = ((Postgres)db).getWatchedTablesColumnNames().get(auditRecord.getTableName());
            diff = new JsonDiff(tableColumnNames);
        } else {
            diff = ObjectCreator.create(XmlDiff.class);
        }
        try {
            validateStateData(auditRecord);
            diff.parseOldData(auditRecord.getOldData());
            diff.parseNewData(auditRecord.getNewData());
        } catch (RecoverableException | JsonProcessingException e) {
            throw new RecoverableException("StateDataProcessing", e.getMessage(), e);
        }
        return diff;
    }

    private void validateStateData(AuditRecord auditRecord) throws RecoverableException {
        StateDataValidator.ValidationResult result =
                StateDataValidator.isOldStateNullOrEmpty()
                .and(StateDataValidator.isNewStateNullOrEmpty())
                .apply(auditRecord);
        if (!result.equals(StateDataValidator.ValidationResult.SUCCESS)) {
            throw new RecoverableException("StateDataValidation", result.getErrorMessage(auditRecord));
        }
    }

    public String findTableDiff(List<StateColumn> stateColumns, Operation dbOperation) {
        tableDiffBuilder.init();
        tableDiffBuilder.build(getStateColumnsDividedIntoRows(stateColumns), dbOperation);
        return tableDiffBuilder.toString();
    }

    private List<List<StateColumn>> getStateColumnsDividedIntoRows(List<StateColumn> stateColumns) {
        List<List<StateColumn>> rows = new ArrayList<>();
        int paddingLength = OutputBuilder.PADDING.length() * 4;
        short characterCount = 0;
        short rowNumber = -1;
        for (StateColumn stateColumn : stateColumns) {
            int stateColumnWidth = 
                (stateColumn.getMaxWidth() > TableDiffBuilder.getMaxColumnWidth() ? TableDiffBuilder.getMaxColumnWidth() : stateColumn.getMaxWidth());
            stateColumnWidth += paddingLength;
            if (rowNumber < 0 || characterCount + stateColumnWidth > TableDiffBuilder.getMaxRowWidth()) {
                List<StateColumn> newRow = new ArrayList<>();
                rows.add(newRow);
                rowNumber++;
                characterCount = (short)stateColumnWidth;
            } else {
                characterCount += (short)stateColumnWidth;
            }
            rows.get(rowNumber).add(stateColumn);
        }
        return rows;
    } 

    public String findColumnDiff(StateColumn stateColumn) {
        columnDiffBuilder.init();
        columnDiffBuilder.build(stateColumn);
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
            return String.join(Common.COMMA_DELIMITER, (ArrayList)value);
        }
        return (String)value;
    }
    
}

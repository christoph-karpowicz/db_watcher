package com.dbw.diff;

import java.util.ArrayList;
import java.util.List;

import com.dbw.db.AuditRecord;
import com.dbw.db.Operation;
import com.dbw.db.Postgres;
import com.dbw.output.OutputBuilder;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StateDiffService implements DiffService {
    @Inject
    private TableDiffBuilder tableDiffBuilder;
    @Inject
    private ColumnDiffBuilder columnDiffBuilder;

    public Diff createDiff(Class<?> dbClass, AuditRecord auditRecord) throws Exception {
        Diff diff;
        if (dbClass.equals(Postgres.class)) {
            diff = new JsonDiff();
        } else {
            diff = new XmlDiff();
        }
        validateStateData(auditRecord);
        diff.parseOldData(auditRecord.getOldData());
        diff.parseNewData(auditRecord.getNewData());
        return diff;
    }

    private void validateStateData(AuditRecord auditRecord) throws Exception {
        String exceptionFmt = "Audit record ID: %d. Could not parse state data. Provided %s state string is null or empty.";
        boolean isUpdate = auditRecord.getOperation().equals(Operation.UPDATE);
        boolean isInsert = auditRecord.getOperation().equals(Operation.INSERT);
        boolean isDelete = auditRecord.getOperation().equals(Operation.DELETE);
        if ((isDelete || isUpdate) && Strings.isNullOrEmpty(auditRecord.getOldData())) {
            throw new Exception(String.format(exceptionFmt, auditRecord.getId(), "old"));
        }
        if ((isInsert || isUpdate) && Strings.isNullOrEmpty(auditRecord.getNewData())) {
            throw new Exception(String.format(exceptionFmt, auditRecord.getId(), "new"));
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
    
}

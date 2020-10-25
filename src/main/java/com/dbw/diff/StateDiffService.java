package com.dbw.diff;

import java.util.ArrayList;
import java.util.List;

import com.dbw.db.AuditRecord;
import com.dbw.db.Operation;
import com.dbw.db.Postgres;
import com.dbw.output.OutputBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StateDiffService implements DiffService {
    @Inject
    private TableDiffBuilder tableDiffBuilder;
    @Inject
    private ColumnDiffBuilder columnDiffBuilder;

    public Diff createDiff(Class<?> dbClass, AuditRecord auditRecord) {
        Diff diff;
        if (dbClass.equals(Postgres.class)) {
            diff = new JsonDiff();
        } else {
            diff = new XmlDiff();
        }
        diff.parseOldData(auditRecord.getOldData());
        diff.parseNewData(auditRecord.getNewData());
        return diff;
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
                (stateColumn.getMaxLength() > TableDiffBuilder.MAX_COL_LENGTH ? TableDiffBuilder.MAX_COL_LENGTH : stateColumn.getMaxLength());
            stateColumnLength += paddingLength;
            if (rowNumber < 0 || characterCount + stateColumnLength > TableDiffBuilder.MAX_ROW_LENGTH) {
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

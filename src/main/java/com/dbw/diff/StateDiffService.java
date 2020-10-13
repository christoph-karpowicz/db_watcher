package com.dbw.diff;

import java.util.List;

import com.dbw.db.AuditRecord;
import com.dbw.db.Operation;
import com.dbw.db.Postgres;
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
        tableDiffBuilder.build(stateColumns, dbOperation);
        return tableDiffBuilder.toString();
    }

    public String findColumnDiff(StateColumn stateColumn, short count) {
        columnDiffBuilder.init();
        columnDiffBuilder.build(stateColumn, count);
        return columnDiffBuilder.toString();
    }
    
}

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
    private TableDiffBuilder diffBuilder;

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

    public String findDiff(List<StateColumn> stateColumns, AuditRecord auditRecord) {
        Operation dbOperation = Operation.valueOfSymbol(auditRecord.getOperation());
        diffBuilder.init();
        diffBuilder.build(stateColumns, dbOperation);
        return diffBuilder.toString();
    }
    
}

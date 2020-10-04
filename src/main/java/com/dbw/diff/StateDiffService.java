package com.dbw.diff;

import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.db.Operation;
import com.dbw.db.Postgres;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class StateDiffService implements DiffService {

    @Inject
    private DiffBuilder diffBuilder;

    public String findDiff(Database db, AuditRecord auditRecord) {
        diffBuilder.init();
        Operation dbOperation = Operation.valueOfSymbol(auditRecord.getOperation());
        Diff diff;
        if (db instanceof Postgres) {
            diff = new JsonDiff();
        } else {
            diff = new XmlDiff();
        }
        diff.parseOldData(auditRecord.getOldData());
        diff.parseNewData(auditRecord.getNewData());
        diff.createStatePairs();
        diffBuilder.build(dbOperation);
        return diffBuilder.toString();
    }
    
}

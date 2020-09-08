package com.dbw.diff;

import com.dbw.db.AuditRecord;
import com.dbw.db.Operation;
import com.dbw.db.Record;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DiffService implements Differable {

    @Inject
    private DiffBuilder diffBuilder;

    public String findDiff(AuditRecord auditRecord) {
        diffBuilder.init();
        Operation dbOperation = Operation.valueOf(auditRecord.getOperation());
        Record record = new Record();
        record.parseOldData(auditRecord.getOldData());
        record.parseNewData(auditRecord.getNewData());
        switch (dbOperation) {
            case UPDATE: 
                for (Object value : record.getOldState().values()) {
                    diffBuilder.append(value);
                }
                for (Object value : record.getNewState().values()) {
                    diffBuilder.append(value);
                }
                break;
        }
        return diffBuilder.toString();
    }
    
}

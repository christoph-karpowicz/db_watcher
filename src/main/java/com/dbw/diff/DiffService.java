package com.dbw.diff;

import java.util.List;

import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.db.Operation;

public interface DiffService {
    public Diff createDiff(Database db, AuditRecord auditRecord) throws Exception;
    public String findTableDiff(List<StateColumn> stateColumns, Operation dbOperation);
}

package com.dbw.diff;

import com.dbw.db.AuditRecord;
import com.dbw.db.Database;
import com.dbw.db.Operation;

import java.util.List;

public interface DiffService {
    Diff createDiff(Database db, AuditRecord auditRecord) throws Exception;
    String findTableDiff(List<StateColumn> stateColumns, Operation dbOperation);
}

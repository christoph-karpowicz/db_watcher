package com.dbw.diff;

import java.util.List;

import com.dbw.db.AuditRecord;
import com.dbw.db.Database;

public interface DiffService {
    public Diff createDiff(Database db, AuditRecord auditRecord);
    public String findDiff(List<StateColumn> stateColumns, AuditRecord auditRecord);
}

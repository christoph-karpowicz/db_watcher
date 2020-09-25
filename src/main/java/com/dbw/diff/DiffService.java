package com.dbw.diff;

import com.dbw.db.AuditRecord;
import com.dbw.db.Database;

public interface DiffService {
    public String findDiff(Database db, AuditRecord auditRecord);
}

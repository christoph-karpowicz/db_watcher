package com.dbw.diff;

import java.util.List;

import com.dbw.db.AuditRecord;

public interface DiffService {
    public Diff createDiff(Class<?> dbClass, AuditRecord auditRecord);
    public String findDiff(List<StateColumn> stateColumns, AuditRecord auditRecord);
}

package com.dbw.diff;

import java.util.List;

import com.dbw.db.AuditRecord;
import com.dbw.db.Operation;

public interface DiffService {
    public Diff createDiff(Class<?> dbClass, AuditRecord auditRecord);
    public String findTableDiff(List<StateColumn> stateColumns, Operation dbOperation);
}

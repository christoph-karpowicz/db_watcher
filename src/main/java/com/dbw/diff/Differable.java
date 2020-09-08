package com.dbw.diff;

import com.dbw.db.AuditRecord;

public interface Differable {
    public String findDiff(AuditRecord auditRecord);
}

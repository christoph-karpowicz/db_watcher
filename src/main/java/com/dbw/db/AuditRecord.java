package com.dbw.db;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

@Getter
@Setter
public class AuditRecord {
    private int id;
    private String tableName;
    private String oldData;
    private String newData;
    private Operation operation;
    private String query;
    private Timestamp timestamp;

    public String getFormattedTimestamp() {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(timestamp);
    }
}

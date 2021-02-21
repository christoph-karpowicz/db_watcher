package com.dbw.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class ConfigCache implements Serializable {
    private static final long serialVersionUID = 2L;
    private String checksum;
    private List<String> tables;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> newTables) {
        LinkedHashSet<String> newTablesSet = new LinkedHashSet<>(newTables);
        if (!Objects.isNull(this.tables) && this.tables.size() > 0) {
            LinkedHashSet<String> oldTablesSet = new LinkedHashSet<>(this.tables);
            newTablesSet.addAll(oldTablesSet);
        }
        this.tables = new ArrayList<>(newTablesSet);
    }
}

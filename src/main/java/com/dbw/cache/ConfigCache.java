package com.dbw.cache;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class ConfigCache implements Serializable {
    private static final long serialVersionUID = 2L;
    private String checksum;
    private Set<String> tables;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Set<String> getTables() {
        return tables;
    }

    public void setTables(Set<String> newTables) {
        LinkedHashSet<String> newTablesSet = new LinkedHashSet<>(newTables);
        if (!Objects.isNull(this.tables) && this.tables.size() > 0) {
            LinkedHashSet<String> oldTablesSet = new LinkedHashSet<>(this.tables);
            newTablesSet.addAll(oldTablesSet);
        }
        this.tables = newTablesSet;
    }
}

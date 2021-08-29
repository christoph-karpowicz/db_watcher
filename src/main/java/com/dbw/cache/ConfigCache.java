package com.dbw.cache;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class ConfigCache implements Serializable {
    private static final long serialVersionUID = 2L;
    private String checksum;
    private Set<String> tables;

    public void setTables(Set<String> newTables) {
        LinkedHashSet<String> newTablesSet = new LinkedHashSet<>(newTables);
        if (!Objects.isNull(this.tables) && this.tables.size() > 0) {
            LinkedHashSet<String> oldTablesSet = new LinkedHashSet<>(this.tables);
            newTablesSet.addAll(oldTablesSet);
        }
        this.tables = newTablesSet;
    }
}

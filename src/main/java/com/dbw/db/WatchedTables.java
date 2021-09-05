package com.dbw.db;

import com.dbw.db.query.QueryHelper;
import com.google.common.collect.HashBiMap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

public class WatchedTables {
    private final HashBiMap<String, String> tableToEntityNames = HashBiMap.create();

    public void put(String tableName) {
        tableToEntityNames.put(tableName, QueryHelper.buildAuditTriggerName(tableName));
    }

    public boolean containsEntityName(String hash) {
        return tableToEntityNames.containsValue(hash);
    }

    public Set<String> getTableNames() {
        return tableToEntityNames.keySet();
    }

    public String getTableByEntityName(String entityName) {
        return tableToEntityNames.inverse().get(entityName);
    }

    public Set<Entry> entrySet() {
        return tableToEntityNames
                .entrySet()
                .stream()
                .map(entry -> new Entry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    @AllArgsConstructor
    @Getter
    public class Entry {
        private final String tableName;
        private final String entityName;
    }
}

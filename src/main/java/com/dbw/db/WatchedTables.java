package com.dbw.db;

import com.dbw.log.ErrorMessages;
import com.dbw.util.StringUtils;
import com.google.common.collect.HashBiMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class WatchedTables {
    private final Database db;
    private final HashBiMap<String, String> tableToEntityNames = HashBiMap.create();

    public void put(String tableName) {
        tableToEntityNames.put(tableName, StringUtils.createShortHash(tableName));
    }

    public boolean containsEntityName(String hash) {
        return tableToEntityNames.containsValue(hash.toUpperCase());
    }

    public Set<String> getTableNames() {
        return tableToEntityNames.keySet();
    }

    public String getTableByEntityName(String entityName) throws Exception {
        String tableName = tableToEntityNames.inverse().get(entityName);
        if (tableName == null) {
            Optional<String> tbName = db.getAllTables()
                    .stream()
                    .filter(tName -> StringUtils.createShortHash(tName).equalsIgnoreCase(entityName))
                    .findFirst();
            if (tbName.isPresent()) {
                return tbName.get();
            } else {
                throw new Exception(String.format(ErrorMessages.TABLE_NAME_NOT_FOUND_BY_ENTITY_NAME, entityName));
            }
        }
        return tableName;
    }

    public Set<Entry> entrySet() {
        return tableToEntityNames
                .entrySet()
                .stream()
                .map(entry -> new Entry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toSet());
    }

    @AllArgsConstructor
    public static class Entry {
        @Getter
        private final String tableName;
        private final String entityName;

        public String getEntityName() {
            return Common.DBW_PREFIX + entityName;
        }
    }
}

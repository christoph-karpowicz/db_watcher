package com.dbw.db;

import com.google.common.collect.Lists;

import java.util.List;

public enum DatabaseType {
    POSTGRES("postgresql"),
    ORCL("oracle");

    public final String type;

    DatabaseType(String type) {
        this.type = type;
    }

    public static List<String> getTypeList() {
        return Lists.newArrayList(POSTGRES.type, ORCL.type);
    }
}

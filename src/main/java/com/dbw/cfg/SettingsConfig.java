package com.dbw.cfg;

public class SettingsConfig {
    private Integer operationsMinimum;
    private Integer operationsLimit;
    private Boolean tableNamesRegex;

    public Integer getOperationsMinimum() {
        return operationsMinimum;
    }

    public Integer getOperationsLimit() {
        return operationsLimit;
    }

    public Boolean getTableNamesRegex() {
        return tableNamesRegex;
    }
}

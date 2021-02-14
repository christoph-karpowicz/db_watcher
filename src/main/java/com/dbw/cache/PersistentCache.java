package com.dbw.cache;

import java.io.Serializable;

public class PersistentCache implements Serializable {
    private static final long serialVersionUID = 1L;
    private ConfigCache config;

    public ConfigCache getConfig() {
        return config;
    }

    public void setConfig(ConfigCache config) {
        this.config = config;
    }
}

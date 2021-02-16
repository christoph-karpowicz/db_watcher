package com.dbw.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PersistentCache implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, ConfigCache> configs;

    public PersistentCache() {
        configs = new HashMap<String, ConfigCache>();
    }

    public Map<String, ConfigCache> getConfigs() {
        return configs;
    }

    public Optional<ConfigCache> getConfig(String path) {
        return Optional.ofNullable(configs.get(path));
    }

    public void setConfig(String path, ConfigCache configCache) {
        configs.put(path, configCache);
    }

    public void removeConfig(String path) {
        configs.remove(path);
    }

}

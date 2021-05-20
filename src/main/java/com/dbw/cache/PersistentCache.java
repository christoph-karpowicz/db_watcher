package com.dbw.cache;

import com.google.common.collect.Sets;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PersistentCache implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, ConfigCache> configs;
    private Set<String> lastUsedConfigPaths;

    public PersistentCache() {
        configs = new HashMap<>();
        lastUsedConfigPaths = Sets.newHashSet();
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

    public boolean isConfigPresent(String path) {
        return configs.containsKey(path);
    }

    public void removeConfig(String path) {
        configs.remove(path);
    }

    public Set<String> getLastUsedConfigPaths() {
        return lastUsedConfigPaths;
    }

    public void setLastUsedConfigPaths(Set<String> lastUsedConfigPaths) {
        this.lastUsedConfigPaths = lastUsedConfigPaths;
    }
}

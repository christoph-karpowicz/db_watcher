package com.dbw.cache;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PersistentCache implements Serializable {
    private static final long serialVersionUID = 1L;
    @Setter
    private Map<String, ConfigCache> configs;
    @Getter @Setter
    private Set<String> lastUsedConfigPaths;

    public PersistentCache() {
        configs = new HashMap<>();
        lastUsedConfigPaths = Sets.newHashSet();
    }

    public Optional<ConfigCache> getConfig(String path) {
        return Optional.ofNullable(configs.get(path));
    }

    public boolean isConfigPresent(String path) {
        return configs.containsKey(path);
    }

    public void removeConfig(String path) {
        configs.remove(path);
    }
}

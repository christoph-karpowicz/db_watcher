package com.dbw.cache;

import com.dbw.cfg.Config;
import com.dbw.watcher.Watcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class ConfigCachePersister implements Runnable {
    @Setter
    private Cache cache;
    private final Map<String, ConfigCache> configs = Maps.newHashMap();

    public void addConfigCache(Watcher watcher) {
        Config cfg = watcher.getCfg();
        ConfigCache configCache = cache.createOrGetConfigCache(cfg.getPath());
        configCache.setChecksum(cfg.getCheckSum());
        configCache.setTables(watcher.getWatchedTables());
        configs.put(cfg.getPath(), configCache);
    }

    @Override
    public void run() {
        cache.createPersistentCacheIfDoesntExist();
        cache.getPersistentCache().get().setConfigs(configs);
        cache.persist();
    }
}

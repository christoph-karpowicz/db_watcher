package com.dbw.cache;

import com.dbw.cfg.Config;
import com.google.common.collect.Lists;

import java.util.List;

public class ConfigCachePersister implements Runnable {
    private Cache cache;
    private final List<Config> configs = Lists.newArrayList();

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void addConfig(Config config) {
        configs.add(config);
    }

    @Override
    public void run() {
        cache.createPersistentCacheIfDoesntExist();
        for (Config cfg : configs) {
            ConfigCache configCache = cache.createOrGetConfigCache(cfg.getPath());
            configCache.setChecksum(cfg.getCheckSum());
            configCache.setTables(cfg.getTables());
            cache.getPersistentCache().get().setConfig(cfg.getPath(), configCache);
        }
        cache.persist();
    }
}

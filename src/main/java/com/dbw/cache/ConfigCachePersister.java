package com.dbw.cache;

import com.dbw.cfg.Config;

public class ConfigCachePersister implements Runnable {
    private Cache cache;
    private Config config;
    private String configFileChecksum;

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setConfigFileChecksum(String configFileChecksum) {
        this.configFileChecksum = configFileChecksum;
    }

    public void run() {
        cache.createPersistentCacheIfDoesntExist();
        ConfigCache configCache = cache.createOrGetConfigCache(config.getPath());
        configCache.setChecksum(configFileChecksum);
        configCache.setTables(config.getTables());
        cache.getPersistentCache().get().setConfig(config.getPath(), configCache);
        cache.persist();
    }
}

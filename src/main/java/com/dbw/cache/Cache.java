package com.dbw.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import com.dbw.log.Level;
import com.dbw.log.Logger;
import com.dbw.log.WarningMessages;

public class Cache {
    public static final String CACHE_FILE_PATH = "./dbw.cache";
    private Optional<PersistentCache> persistentCache;

    public Optional<PersistentCache> getPersistentCache() {
        return persistentCache;
    }

    public void load() {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(CACHE_FILE_PATH))) {
            persistentCache = Optional.of((PersistentCache) input.readObject());
        } catch (IOException | ClassNotFoundException e) {
            persistentCache = Optional.empty();
            Logger.log(Level.WARNING, WarningMessages.NO_CACHE_FILE);
        }
    }

    public boolean compareConfigFileChecksums(String path, String currentChecksum) throws IOException, NoSuchAlgorithmException {
        boolean areNotEqual = true;
        if (getPersistentCache().isPresent()) {
            Optional<ConfigCache> configCache = getPersistentCache().get().getConfig(path);
            if (configCache.isPresent()) {
                String previousChecksum = configCache.get().getChecksum();
                areNotEqual = !currentChecksum.equals(previousChecksum);
            }
        }
        return areNotEqual;
    }

    public void createPersistentCacheIfDoesntExist() {
        if (!persistentCache.isPresent()) {
            persistentCache = Optional.of(new PersistentCache());
        }
    }

    public ConfigCache createOrGetConfigCache(String path) {
        Optional<ConfigCache> persistedConfigCache = getPersistentCache().get().getConfig(path);
        return persistedConfigCache.orElseGet(ConfigCache::new);
    }

    public List<String> getConfigTables(String path) {
        return getPersistentCache().get().getConfig(path).get().getTables();
    }

    public void persist() {
        if (!persistentCache.isPresent()) {
            return;
        }
        CachePersister cachePersister = new CachePersister();
        cachePersister.setPersistentCache(persistentCache.get());
        Thread cachePersisterThread = new Thread(cachePersister);
        cachePersisterThread.start();
    }

    public void removeConfig(String path) {
        getPersistentCache().get().removeConfig(path);
        persist();
    }
}

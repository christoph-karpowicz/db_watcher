package com.dbw.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import com.dbw.log.Level;
import com.dbw.log.Logger;
import com.dbw.log.WarningMessages;

public class Cache {
    public final String CACHE_FILE_PATH = "./dbw.cache";
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

    public boolean compareConfigFileChecksums(String currentChecksum) throws IOException, NoSuchAlgorithmException {
        boolean areNotEqual = true;
        if (getPersistentCache().isPresent()) {
            String previousChecksum = getPersistentCache().get().getConfig().getChecksum();
            areNotEqual = !currentChecksum.equals(previousChecksum);
        }
        return areNotEqual;
    }

    public void createPersistentCacheIfDoesntExist() {
        if (!persistentCache.isPresent()) {
            persistentCache = Optional.of(new PersistentCache());
        }
    }

    public void persist() {
        try (FileOutputStream fout = new FileOutputStream(CACHE_FILE_PATH, false);
            ObjectOutputStream oos = new ObjectOutputStream(fout);) {
            oos.writeObject(persistentCache.get());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void delete() {
        File file = new File(CACHE_FILE_PATH);
        file.delete();
    }
}

package com.dbw.cache;

import com.dbw.err.UnrecoverableException;
import com.dbw.log.ErrorMessages;
import lombok.Setter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class CachePersister implements Runnable {
    @Setter
    private PersistentCache persistentCache;

    @Override
    public void run() {
        try (FileOutputStream fOut = new FileOutputStream(Cache.CACHE_FILE_PATH, false);
             ObjectOutputStream oos = new ObjectOutputStream(fOut)) {
            oos.writeObject(persistentCache);
        } catch (IOException e) {
            new UnrecoverableException("CachePersistence", ErrorMessages.CACHE_PERSIST_FAILED, e).handle();
        }
    }
}

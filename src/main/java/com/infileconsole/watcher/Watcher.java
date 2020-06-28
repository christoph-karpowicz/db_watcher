package com.infileconsole.watcher;

import java.util.HashMap;
import java.util.Map.Entry;

public class Watcher implements Runnable {
    private boolean alive;
    private String dirName;
    private HashMap<String, DirectoryWatcher> dirWatchers;

    public Watcher(String dirName) {
        this.dirName = dirName;
        this.alive = true;
        this.dirWatchers = new HashMap<String, DirectoryWatcher>();
    }

    public void init() {
        DirectoryWatcher dirWatcher = new DirectoryWatcher(dirName);
        dirWatchers.put(dirName, dirWatcher);
    }

    public boolean isAlive() {
        return alive;
    }

    @Override
    public void run() {
        for (Entry<String, DirectoryWatcher> dw : dirWatchers.entrySet()) {
            Thread dirWatcherThread = new Thread(dw.getValue(), dw.getKey() + "directory watcher");
            dirWatcherThread.start();
        }
    }
}
package com.infileconsole.watcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.infileconsole.fs.DirectoryTraverser;

public class Watcher implements Runnable {
    private boolean alive;
    private Path root;
    private HashMap<String, DirectoryWatcher> dirWatchers;

    public Watcher(Path root) {
        this.root = root;
        this.alive = true;
        this.dirWatchers = new HashMap<String, DirectoryWatcher>();
    }

    public void init() {
        DirectoryTraverser dirTraverser = new DirectoryTraverser(root);
        dirTraverser.init();
        ArrayList<Path> dirs = dirTraverser.traverse();

        for (Path path : dirs) {
            DirectoryWatcher dirWatcher = new DirectoryWatcher(path);
            dirWatchers.put(path.toString(), dirWatcher);
        }
        System.out.println(dirWatchers);
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
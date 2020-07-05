package com.infileconsole.watcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.inject.Singleton;
import com.infileconsole.app.Dispatch;
import com.infileconsole.fs.DirectoryTraverser;

@Singleton
public class DirectoryTreeWatcher implements Watchable {
    private Dispatch dispatch;
    private boolean alive;
    private HashMap<String, DirectoryWatcher> dirWatchers;

    public void init() {
        setAlive(true);
        this.dirWatchers = new HashMap<String, DirectoryWatcher>();

        DirectoryTraverser dirTraverser = new DirectoryTraverser(dispatch.getRoot());
        dirTraverser.init();
        ArrayList<Path> dirs = dirTraverser.traverse();

        for (Path path : dirs) {
            DirectoryWatcher dirWatcher = new DirectoryWatcher(dispatch, path);
            dirWatchers.put(path.toString(), dirWatcher);
        }
        System.out.println(dirWatchers);
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setDispatch(Dispatch dispatch) {
        this.dispatch = dispatch;
    }

    public void startDirectoryWatchers() {
        for (Entry<String, DirectoryWatcher> dw : dirWatchers.entrySet()) {
            Thread dirWatcherThread = new Thread(dw.getValue(), dw.getKey() + "directory watcher");
            dirWatcherThread.start();
        }
    }
}
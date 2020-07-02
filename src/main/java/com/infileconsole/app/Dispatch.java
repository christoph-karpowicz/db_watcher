package com.infileconsole.app;

import java.nio.file.Path;

import com.google.inject.Inject;
import com.infileconsole.watcher.DirectoryTreeWatcher;
import com.infileconsole.watcher.Watcher;

public class Dispatch {
    private Path root;
    private boolean closeSignal;
    
    @Inject
    private Watcher watcher;

    public void init() {
        this.closeSignal = false;
        watcher.setDispatch(this);
        watcher.init();

        Thread watcherThread = new Thread((DirectoryTreeWatcher)watcher, "watcher");
        watcherThread.start();

        while (!closeSignal) {
            
        }
    }

    public Path getRoot() {
        return root;
    }

    public void setRoot(Path root) {
        this.root = root;
    }

    public void sendCloseSignal() {
        closeSignal = true;
    }
}
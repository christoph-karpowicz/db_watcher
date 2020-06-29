package com.infileconsole.app;

import com.infileconsole.watcher.Watcher;

public class Dispatch {
    private boolean closeSignal;
    private Watcher watcher;

    public Dispatch(Watcher watcher) {
        this.watcher = watcher;
        this.closeSignal = false;
    }

    public void init() {
        watcher.init();

        Thread watcherThread = new Thread(watcher, "watcher");
        watcherThread.start();

        while (!closeSignal) {
                
        }
    }

    public void sendCloseSignal() {
        closeSignal = true;
    }
}
package com.infileconsole.app;

import com.infileconsole.watcher.Watcher;

// java -cp target/infileconsole-1.0-SNAPSHOT.jar com.infileconsole.app.App

public class App {
    public static void main(String[] args) {
        String dir = "/home/chris/Documents/work/i-fc/test";

        Watcher watcher = new Watcher(dir);
        watcher.init();

        Thread watcherThread = new Thread(watcher, "watcher");
        watcherThread.start();

        while (watcher.isAlive()) {
            
        }
    }
}

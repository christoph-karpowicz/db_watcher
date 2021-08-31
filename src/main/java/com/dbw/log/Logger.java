package com.dbw.log;

import com.dbw.watcher.WatcherManager;

public class Logger {
    private static WatcherManager watcherManager;

    public static void setWatcherManager(WatcherManager watcherManager) {
        Logger.watcherManager = watcherManager;
    }

    public static void log(Level level, String dbName, String msg) {
        if (watcherManager != null && watcherManager.getWatchersSize() > 1) {
            System.out.printf("[%s] %s: %s\n", level.name(), dbName, removeNewLines(msg));
        } else {
            log(level, msg);
        }
    }

    public static void log(Level level, String msg) {
        System.out.printf("[%s] %s\n", level.name(), removeNewLines(msg));
    }

    private static String removeNewLines(String msg) {
        if (msg == null) {
            return "";
        }
        return msg.replace("\n", "");
    }
    
}

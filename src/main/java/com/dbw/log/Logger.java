package com.dbw.log;

import com.google.inject.Singleton;

@Singleton
public class Logger {

    public static void log(Level level, String msg) {
        System.out.printf("[%s] %s\n", level.name(), msg);
    }
    
}

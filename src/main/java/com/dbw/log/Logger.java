package com.dbw.log;

public class Logger {

    public static void log(Level level, String msg) {
        System.out.printf("[%s] %s\n", level.name(), msg);
    }
    
}

package com.infileconsole.app;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.infileconsole.watcher.Watcher;

// java -cp target/infileconsole-1.0-SNAPSHOT.jar com.infileconsole.app.App

public class App {
    public static void main(String[] args) {
        Path root = Paths.get("/home/chris/Documents/work/i-fc/test");
        Watcher watcher = new Watcher(root);
        Dispatch dispatch = new Dispatch(watcher);
        dispatch.init();
    }
}

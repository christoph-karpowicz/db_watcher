package com.infileconsole.app;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.inject.Guice;
import com.google.inject.Injector;

// mvn package shade:shade
// java -jar ./target/infileconsole-1.0-SNAPSHOT.jar

public class App {
    public static void main(String[] args) {
        Path root = Paths.get("/home/chris/Documents/work/i-fc/test");
        Injector injector = Guice.createInjector(new AppModule());
        Dispatch dispatch = injector.getInstance(Dispatch.class);
        dispatch.setRoot(root);
        dispatch.init();
    }
}

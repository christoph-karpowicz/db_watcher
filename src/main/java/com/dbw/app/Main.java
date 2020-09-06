package com.dbw.app;

// mvn package shade:shade
// java -Xmx5m -jar ./target/dbw-1.0-SNAPSHOT.jar -c config/example.yml

public class Main {

    public static App app = new App();
    
    public static void main(String[] args) {
        app.init(args);
        app.start();
    }
}

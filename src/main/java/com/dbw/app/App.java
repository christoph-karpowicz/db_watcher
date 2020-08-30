package com.dbw.app;

import com.dbw.cfg.Config;
import com.dbw.cfg.ConfigParser;
import com.dbw.cli.CLI;
import com.dbw.watcher.Watcher;

// mvn package shade:shade
// java -jar ./target/dbw-1.0-SNAPSHOT.jar -c config/example.yml

public class App {

    public static Watcher watcher = new Watcher();
    
    public static void main(String[] args) {
        CLI.ParsedOptions options = handleArgs(args);
        Config config = ConfigParser.fromYMLFile(options.configPath);
        watcher.setConfig(config);
        watcher.init();
        watcher.start();
        watcher.end();
    }

    private static CLI.ParsedOptions handleArgs(String[] args) {
        CLI cli = new CLI();
        cli.setArgs(args);
        cli.init();
        return cli.parseArgs();
    }
}

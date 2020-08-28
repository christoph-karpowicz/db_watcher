package com.dbw.app;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

import com.dbw.cfg.Config;
import com.dbw.cfg.DatabaseConfig;
import com.dbw.db.Database;
import com.dbw.db.DatabaseFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

// mvn package shade:shade
// java -jar ./target/dbw-1.0-SNAPSHOT.jar -c config/example.yml

public class App {
    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption("c", "config", true, "point to a configuration file");

        try {
            CommandLine line = parser.parse(options, args);
        
            if(line.hasOption("config")) {
                String configPath = line.getOptionValue("config");
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                try {
                    Config config = mapper.readValue(new File(configPath), Config.class);

                    DatabaseConfig dbConfig = config.getDatabase();
                    Database db = DatabaseFactory.getDatabase(dbConfig);
                    db.connect();

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        catch(ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
    }
}

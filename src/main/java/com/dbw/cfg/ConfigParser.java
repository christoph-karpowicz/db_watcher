package com.dbw.cfg;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ConfigParser {

    public static Config fromYMLFile(String path) throws JsonMappingException, JsonParseException, IOException {
        Config config = null;
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        config = mapper.readValue(new File(path), Config.class);
        return config;
    }

    public static void outputConfigFileListFromCurrentDir() throws IOException {
        
    }
    
    public static Set<String> getConfigFileSetFromCurrentDir() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(""))) {
            return stream
              .filter(file -> !Files.isDirectory(file))
              .map(Path::getFileName)
              .map(Path::toString)
              .collect(Collectors.toSet());
        }
    }
    
}
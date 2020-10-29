package com.dbw.cfg;

import java.io.File;
import java.io.IOException;

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
    
}
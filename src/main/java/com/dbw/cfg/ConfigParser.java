package com.dbw.cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.dbw.log.ErrorMessages;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
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

    public static String getConfigFileNameFromInput() throws IOException {
        System.out.println(LogMessages.CHOOSE_CONFIG);
        List<String> configFileList = getYMLFileListFromCurrentDir();
        outputConfigFileListFromCurrentDir(configFileList);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Integer fileNameIndex = null;
        while (Objects.isNull(fileNameIndex)) {
            String input = reader.readLine();
            try {
                fileNameIndex = Integer.parseInt(input);
                if (fileNameIndex > configFileList.size() || fileNameIndex < 1) {
                    Logger.log(Level.ERROR, ErrorMessages.INPUT_OUT_OF_BOUNDS);
                    fileNameIndex = null;
                }
            } catch (NumberFormatException e) {
                Logger.log(Level.ERROR, ErrorMessages.INPUT_NAN);
            }
        }
        return configFileList.get(fileNameIndex - 1);
    }
    
    private static void outputConfigFileListFromCurrentDir(List<String> configFileList) throws IOException {
        for (int i = 0; i < configFileList.size(); i++) {
            String outputLine = String.format("[%d] %s", i+1, configFileList.get(i));
            System.out.println(outputLine);
        }
    }
    
    private static List<String> getYMLFileListFromCurrentDir() throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(""))) {
            return stream
                .filter(file -> !Files.isDirectory(file))
                .filter(ConfigParser::isYMLFile)
                .map(Path::getFileName)
                .map(Path::toString)
                .collect(Collectors.toList());
        }
    }

    private static boolean isYMLFile(Path path) {
        String ymlFilePatternString = ".+\\.ya?ml$";
        Pattern ymlFilePattern = Pattern.compile(ymlFilePatternString);
        Matcher ymlFilePatternMatcher = ymlFilePattern.matcher(path.getFileName().toString());
        return ymlFilePatternMatcher.matches();
    }
    
}
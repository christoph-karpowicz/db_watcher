package com.dbw.cfg;

import com.dbw.err.ConfigException;
import com.dbw.log.ErrorMessages;
import com.dbw.log.Level;
import com.dbw.log.LogMessages;
import com.dbw.log.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Sets;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigParser {
    private final static String YML_PATTERN = ".+\\.ya?ml$";

    public static Config fromYMLFile(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Config config = mapper.readValue(file, Config.class);
        config.setPath(file.getPath());
        return config;
    }

    public static Set<String> getConfigFileNamesFromInput() throws IOException, ConfigException {
        List<String> configFileList = getYMLFileListFromCurrentDir();
        if (configFileList.size() == 0) {
            throw new ConfigException(ErrorMessages.CONFIG_NO_YML_FILES);
        }
        System.out.println(LogMessages.CHOOSE_CONFIG);
        outputConfigFileListFromCurrentDir(configFileList);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean inputComplete = false;
        Set<Integer> configFileIndices = Sets.newHashSet();
        while (!inputComplete) {
            String input = reader.readLine();
            try {
                String[] configFileInputIndices = input.split(",");
                Set<String> configFileRawIndices = Sets.newHashSet(configFileInputIndices);
                for (String index : configFileRawIndices) {
                    Integer fileNameIndex = Integer.parseInt(index);
                    if (fileNameIndex > configFileList.size() || fileNameIndex < 1) {
                        throw new Exception(ErrorMessages.INPUT_OUT_OF_BOUNDS);
                    }
                    configFileIndices.add(fileNameIndex);
                }
            } catch (NumberFormatException e) {
                Logger.log(Level.ERROR, ErrorMessages.INPUT_NAN);
            } catch (Exception e) {
                Logger.log(Level.ERROR, e.getMessage());
            }
            inputComplete = true;
        }
        return configFileIndices
                .stream()
                .map(index -> configFileList.get(index - 1))
                .collect(Collectors.toSet());
    }
    
    private static void outputConfigFileListFromCurrentDir(List<String> configFileList) {
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
        Pattern ymlFilePattern = Pattern.compile(YML_PATTERN);
        Matcher ymlFilePatternMatcher = ymlFilePattern.matcher(path.getFileName().toString());
        return ymlFilePatternMatcher.matches();
    }

    public static String getFileChecksum(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(file);
        byte[] byteArray = new byte[1024];
        int bytesCount;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
    
}

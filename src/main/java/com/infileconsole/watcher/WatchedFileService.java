package com.infileconsole.watcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WatchedFileService {
    private static final String IFC_ID_ATTRIBUTE = "user:watchedfileid";
    private static final String IFC_ID = "watchedfileid";

    public static Optional<String> setId(Path path) {
        final String fileId = generateFileId();
        return setId(path, fileId);
    }

    public static Optional<String> setId(Path path, String fileId) {
        try {
            Files.setAttribute(path, IFC_ID_ATTRIBUTE, fileId.getBytes("UTF-8"));
            return Optional.of(fileId);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public static Optional<String> getId(Path path) {
        UserDefinedFileAttributeView fileAttributeView = Files.getFileAttributeView(path,
                UserDefinedFileAttributeView.class);
        try {
            List<String> userAttributes = fileAttributeView.list();

            if (userAttributes.contains(IFC_ID)) {
                byte[] b = (byte[]) Files.getAttribute(path, IFC_ID_ATTRIBUTE);
                String objectId = new String(b, "UTF-8");
                return Optional.of(objectId);
            }
        } catch (IOException e) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    private static String generateFileId() {
        return UUID.randomUUID().toString();
    }
}
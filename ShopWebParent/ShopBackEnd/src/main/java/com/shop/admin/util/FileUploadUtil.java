package com.shop.admin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);

    public static void saveFile(String directory, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(directory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream();) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IOException("Could not save file " + fileName, exception);
        }
    }

    public static void cleanDirectory(String directory) {
        Path uploadPath = Paths.get(directory);
        try {
            Files.list(uploadPath).forEach(path -> {
                if (!Files.isDirectory(path)) {
                    try {
                        Files.delete(path);
                    } catch (IOException exception) {
                        LOGGER.error("Could not delete file " + path);
                    }
                }
            });
        } catch (IOException exception) {
            LOGGER.error("Could not list directory " + uploadPath);
        }
    }
}

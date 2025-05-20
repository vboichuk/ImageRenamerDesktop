package filerenamer;

import com.drew.imaging.ImageProcessingException;
import exception.CameraModelException;
import exifReader.ExifReader;
import filedata.datetime.CompositeDateTimeReader;
import filedata.md5.MD5Reader;
import fileprocessor.FileMetadata;
import fileprocessor.FileProcessor;
import fileprocessor.ProcessingResult;
import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileRenamer extends FileProcessor {

    private final CompositeDateTimeReader dateTimeReader;
    private FileNamingStrategy strategy;

    public FileRenamer() {
        this(new TemplateNamingStrategy("{date:yyyy.MM.dd}.JPG"));
    }

    public FileRenamer(FileNamingStrategy strategy) {
        this.dateTimeReader = new CompositeDateTimeReader();
        this.strategy = strategy;
    }

    public void rename(String directory, String template) {

        strategy = new TemplateNamingStrategy(template);

        try {
            Path dirPath = FileUtils.getDirectory(directory);
            logger.debug("Processing directory: {}", dirPath);
            logger.debug("Template: {}", template);

            Collection<String> images = getImageFiles(dirPath);
            if (images.isEmpty()) {
                return;
            }

            processImages(dirPath, images);
        } catch (IllegalArgumentException | IOException e) {
            logError(e.getMessage(), e);
        }
    }

    private void processImages(Path directoryPath, Collection<String> imageNames) throws IOException {
        ProcessingResult result = new ProcessingResult("Image processing");
        Map<String, String> renameMap = new LinkedHashMap<>();

        imageNames.forEach(imageName -> {
            try {
                processSingleImage(directoryPath, imageName, renameMap, result);
            } catch (Exception e) {
                result.incFailed();
                logger.error("[Failed] {} : {}", imageName, e.getMessage());
            }
        });

        logger.info(result.toString());
        executeRenaming(directoryPath, renameMap);
    }

    private void executeRenaming(Path directoryPath, Map<String, String> renameMap) {
        if (renameMap.isEmpty()) {
            logger.info("Nothing to rename");
            return;
        }

        logger.info("Files to rename ({}):", renameMap.size());
        renameMap.forEach((oldName, newName) -> logger.info("{} → {}", oldName, newName));

        if (!confirmOperation("Confirm rename?")) {
            logger.info("Operation canceled");
            return;
        }

        ProcessingResult result = new ProcessingResult("File renaming");
        renameMap.forEach((oldName, newName) -> {
            try {
                FileUtils.safeMove(directoryPath.resolve(oldName), directoryPath.resolve(newName));
                result.incProcessed();
            } catch (IOException e) {
                result.incFailed();
                logger.error("[Failed]: {} -> {}", oldName, newName);
            }
        });

        logger.info(result.toString());
    }


    private void processSingleImage(Path directoryPath, String imageName,
                                    Map<String, String> renameMap, ProcessingResult result)
            throws IOException, ImageProcessingException, DateTimeException {

        Path imagePath = directoryPath.resolve(imageName);

        if (!Files.isRegularFile(imagePath)) {
            throw new ImageProcessingException("Not a regular file", null);
        }

        // ExifReader.printAllTags(imagePath.toFile());

        FileMetadata metadata = extractFileInfo(imagePath);
        String newName = strategy.generateName(metadata);
        newName = validatePath(newName);

        if (!imageName.equals(newName)) {
            logger.info("{} -> {}", imageName, newName);
            renameMap.put(imageName, newName);
            result.incProcessed();
        } else {
            logger.info("skip {}", imageName);
            result.incSkipped();
        }
    }


    public static String validatePath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        return path.startsWith("/") ? path.substring(1) : path;
    }

    private FileMetadata extractFileInfo(Path filePath) throws IOException {
        File file = filePath.toFile();
        return new FileMetadata()
                .setMd5(MD5Reader.getMD5(file))
                .setExtension(FileUtils.ExtensionUtils.getExtension(filePath.getFileName().toString()))
                .setCameraModel(ExifReader.getCameraModel(file).orElse(null))
                .setDateTime(dateTimeReader.getDateTime(file).orElse(null));
    }
}

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
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

public class FileRenamer extends FileProcessor {

    private final CompositeDateTimeReader dateTimeReader;
    private final FileNamingStrategy strategy;

    public FileRenamer() {
        this(metadata -> {
            if (metadata.getDateTime() == null)
                throw new DateTimeException("");

//            if (metadata.getCameraModel() == null)
//                throw new CameraModelException("");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_(HH-mm)");
            String dateStr = formatter.format(metadata.getDateTime());
            String md5Prefix = metadata.getMd5().substring(0, 6);
            String camera = metadata.getCameraModel();

            return String.format("%s-%s.%s",
                    // camera,
                    dateStr,
                    md5Prefix,
                    metadata.getExtension().toUpperCase());
        });
    }

    public FileRenamer(FileNamingStrategy strategy) {
        this.dateTimeReader = new CompositeDateTimeReader();
        this.strategy = strategy;
    }

    public void rename(String directory) {

        try {
            Path dirPath = FileUtils.getDirectory(directory);
            logger.debug("Processing directory: {}", dirPath);

            Collection<String> images = getImageFiles(dirPath);
            if (images.isEmpty()) {
                return;
            }

            calculateNames(dirPath, images);
        } catch (IllegalArgumentException | IOException e) {
            logError(e.getMessage(), e);
        }
    }


    private void calculateNames(Path directoryPath, Collection<String> imageNames) throws IOException {
        ProcessingResult result = new ProcessingResult("Calculate names");
        HashMap<String, String> namesMap = new HashMap<>();

        for (String imageName : imageNames) {

            try {
                Optional<String> newName = calculateNameForFile(directoryPath, imageName);
                if (newName.isPresent()) {
                    if (imageName.equals(newName.get())) {
                        logger.info("✓ {}", imageName);
                        result.incSkipped();
                    } else {

                        namesMap.put(imageName, newName.get());
                        result.incProcessed();
                    }

                } else {
                    logger.info("⚠ {}", imageName);
                    result.incFailed();
                }
            } catch (java.io.FileNotFoundException e) {
                result.incFailed();
                logError("FileNotFoundException: " + imageName, e);
            } catch (Exception e) {
                result.incFailed();
                // ⚠
                logError("Failed processing of file " + imageName, e);
            }
        }
        logger.info(result.toString());

        if (namesMap.isEmpty()) {
            logger.info("No files need renaming");
            return;
        }

        logger.info("Files to rename:");
        namesMap.forEach((oldName, newName) -> logger.info("{} → {}", oldName, newName));

        if (confirmOperation("Confirm rename?")) {
            renameFilesInDirectory(directoryPath, namesMap);
        } else {
            logger.info("Operation canceled by user.");
        }
    }

    private void renameFilesInDirectory(Path directoryPath, HashMap<String, String> namesMap) {
        ProcessingResult result = new ProcessingResult("Rename files");
        for (String name : namesMap.keySet()) {
            try {
                Path curPath = directoryPath.resolve(name);
                Path newPath = directoryPath.resolve(namesMap.get(name));
                boolean ok = FileUtils.safeMove(curPath, newPath);
                if (ok)
                    result.incProcessed();
                else
                    result.incSkipped();
            } catch (NoSuchFileException e) {
                result.incFailed();
                logError("No such file", e);
            } catch (IOException e) {
                result.incFailed();
                logError(e.getMessage(), e);
            }
        }

        logger.info(result.toString());
    }


    private Optional<String> calculateNameForFile(Path directoryPath, String imageName)
            throws IOException, ImageProcessingException {

        Path imagePath = directoryPath.resolve(imageName);

        if (!Files.isRegularFile(imagePath)) {
            throw new ImageProcessingException("Not a regular file", null);
        }

        // ExifReader.printAllTags(imagePath.toFile());

        FileMetadata metadata = extractFileInfo(imagePath);
        String newName;
        try {
            newName = strategy.generateName(metadata);
        }
        catch (DateTimeException e) {
            logError("DateTimeException of " + imageName, e);
            return Optional.empty();
        } catch (CameraModelException e) {
            logError("CameraModelException of " + imageName, e);
            return Optional.empty();
        }

        return Optional.of(newName);
    }

    private FileMetadata extractFileInfo(Path filePath) throws IOException {
        File file = filePath.toFile();

        FileMetadata metadata = new FileMetadata();
        metadata.setMd5(MD5Reader.getMD5(file));
        metadata.setExtension(FileUtils.ExtensionUtils.getExtension(filePath.getFileName().toString()));
        metadata.setCameraModel(ExifReader.getCameraModel(file).orElse("none"));
        dateTimeReader.getDateTime(file).ifPresent(metadata::setDateTime);
        return metadata;
    }
}

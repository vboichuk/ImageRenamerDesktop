package filerenamer;

import fileprocessor.FileProcessor;
import com.drew.imaging.ImageProcessingException;
import filedata.datetime.CompositeDateTimeReader;
import filedata.md5.MD5Reader;
import fileprocessor.FileMetadata;
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_(HH-mm)");
            String dateStr = formatter.format(metadata.getDateTime());
            String md5Prefix = metadata.getMd5().substring(0, 6);

            return String.format("%s-%s.%s",
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
                    namesMap.put(imageName, newName.get());
                    result.incProcessed();
                }
                else {
                    logger.debug("✓ {}", imageName);
                    result.incSkipped();
                }
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

        FileMetadata metadata = extractFileInfo(imagePath);

        String newName = strategy.generateName(metadata);
        Path newPath = directoryPath.resolve(newName);

        boolean namesMatch = imagePath.equals(newPath);

        if (namesMatch)
            return Optional.empty();

        return Optional.of(newName);
    }

    private FileMetadata extractFileInfo(Path filePath) throws IOException {
        File file = filePath.toFile();

        return new FileMetadata(
                dateTimeReader.getDateTime(file).orElseThrow(() ->
                        new DateTimeException("Date undefined")),
                MD5Reader.getMD5(file),
                FileUtils.ExtensionUtils.getExtension(filePath.getFileName().toString())
        );
    }
}

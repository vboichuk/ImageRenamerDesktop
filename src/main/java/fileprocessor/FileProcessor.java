package fileprocessor;

import com.drew.imaging.ImageProcessingException;
import exifEditor.ExifEditor;
import filedata.datetime.CompositeDateTimeReader;
import filedata.md5.MD5Reader;
import filerenamer.FileNamingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class FileProcessor {

    private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);

    private final CompositeDateTimeReader dateTimeReader;
    private final FileNamingStrategy strategy;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_(HH-mm)");

    public FileProcessor() {
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

    public FileProcessor(FileNamingStrategy strategy) {
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

            renameFilesInDirectory(dirPath, images);
        } catch (IllegalArgumentException | IOException e) {
            logError(e.getMessage(), e);
        }
    }

    public void editExif(String directory) {
        try {
            Path dirPath = FileUtils.getDirectory(directory);
            logger.debug("Processing directory: {}", dirPath);

            Collection<String> images = getImageFiles(dirPath);
            if (images.isEmpty()) {
                return;
            }
            updateExifForFilesInDirectory(dirPath, images);
        } catch (IllegalArgumentException | IOException e) {
            logError(e.getMessage(), e);
        }
    }


    private Collection<String> getImageFiles(Path dirPath) throws IOException {

        long startTime = System.nanoTime();
        Collection<String> images = FileUtils.ImageUtils.listImageFilesFast(dirPath);
        logTime("Time for getting images list", (System.nanoTime() - startTime) / 1_000_000L);

        if (images.isEmpty()) {
            logger.info("No images found in directory: {}", dirPath);
        } else {
            logger.info("Found {} images in directory: {}", images.size(), dirPath);
        }

        return images;
    }

    @SuppressWarnings("SameParameterValue")
    protected void logTime(String title, long totalTimeMs) {
        String timeStr;
        if (totalTimeMs < 1000L)
            timeStr = totalTimeMs + " ms";
        else
            timeStr = (totalTimeMs / 1000L)  + " s";

        logger.debug(title + ": " + timeStr);
    }

    protected void logError(String message, Exception e) {
        logger.error(message, e);
    }


    private void renameFilesInDirectory(Path directoryPath, Collection<String> imageNames) throws IOException {
        ProcessingResult result = new ProcessingResult();

        for (String imageName : imageNames) {
            try {
                if (processSingleImage(directoryPath, imageName))
                    result.incProcessed();
                else
                    result.incSkipped();
            } catch (Exception e) {
                result.incFailed();
                logError("Failed processing of file " + imageName, e);
            }
        }
        logger.info(result.toString());
    }

    private void updateExifForFilesInDirectory(Path directoryPath, Collection<String> imageNames) {
        ProcessingResult result = new ProcessingResult();

        for (String imageName : imageNames) {
            try {
                Path imagePath = directoryPath.resolve(imageName);
                if (imageName.length() < 18) {
                    throw new IllegalArgumentException("File name too short: " + imageName);
                }
                String substring = imageName.substring(0, 18);

                LocalDateTime dateTime = LocalDateTime.parse(substring, formatter);
                logger.debug("{} -> {}\n", imageName, dateTime);
                ExifEditor.updateExifDateTimeOriginal(imagePath.toFile(), dateTime);
                result.incProcessed();
            }
            catch (StringIndexOutOfBoundsException e) {
                result.incFailed();
                logError(e.getMessage(), e);
            } catch (Exception e) {
                result.incFailed();
                logError("Failed processing of file " + imageName, e);
            }
        }
        logger.info(result.toString());
    }


    private boolean processSingleImage(Path directoryPath, String imageName)
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
            return false;

        logger.debug(imagePath.getFileName() + " => " + newName);
        return FileUtils.safeMove(imagePath, newPath);
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


package fileprocessor;

import exifEditor.ExifEditor;
import utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class FileExifEditor extends FileProcessor {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_(HH-mm)");

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

    private void updateExifForFilesInDirectory(Path directoryPath, Collection<String> imageNames) {
        ProcessingResult result = new ProcessingResult("Update Exif");

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

}


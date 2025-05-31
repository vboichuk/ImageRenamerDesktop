package fileprocessor;

import exception.NoExifDataException;
import exifeditor.ExifEditor;
import utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileExifEditor extends FileProcessor {

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

        Pattern pattern = Pattern.compile("(\\d{4}\\.\\d{2}\\.\\d{2}_\\(\\d{2}-\\d{2}\\))");

        for (String imageName : imageNames) {
            try {
                Path imagePath = directoryPath.resolve(imageName);

                Matcher matcher = pattern.matcher(imageName);
                if (!matcher.find()) {
                    throw new DateTimeParseException("DateTimeParseException", imageName, 1);
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_(HH-mm)");
                LocalDateTime dateTime = LocalDateTime.parse(matcher.group(1), formatter);


                // Optional<LocalDateTime> dateTimeOptional = ExifReader.getDateTime(imagePath.toFile());
                // if (dateTimeOptional.isEmpty()) {
                //    throw new NoExifDataException();
                // }
                // LocalDateTime dateTime = dateTimeOptional.get();
                 // dateTime = dateTime.plusHours(1);

                ExifEditor.updateExifDateTimeOriginal(imagePath.toFile(), dateTime);
                logger.info("{} → {}", imageName, dateTime);
                result.incProcessed();
            } catch (NoExifDataException | DateTimeParseException e) {
                result.incFailed();
                logger.warn("⚠ {} : {}", imageName, e.getMessage());
            } catch (Exception e) {
                result.incFailed();
                logError("⚠ Failed processing of file " + imageName, e);
            }
        }
        logger.info(result.toString());
    }

}


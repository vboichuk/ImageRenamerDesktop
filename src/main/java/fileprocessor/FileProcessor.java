package fileprocessor;

import com.drew.imaging.ImageProcessingException;
import filedata.datetime.CompositeDateTimeReader;
import filedata.md5.MD5Reader;
import filerenamer.FileNamingStrategy;
import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class FileProcessor {

    private static CompositeDateTimeReader resolver;
    private static FileNamingStrategy strategy;

    public FileProcessor() {
        resolver = new CompositeDateTimeReader();

        strategy = metadata -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_(HH-mm)");
            String dateStr = formatter.format(metadata.getDateTime());
            String md5Prefix = metadata.getMd5().substring(0, 6);

            return String.format("%s-%s.%s",
                    dateStr,
                    md5Prefix,
                    metadata.getExtension().toUpperCase());
        };
    }

    public void processDirectory(String path) {

        try {
            Path dirPath = FileUtils.getDirectory(path);

            long startTime = System.nanoTime();

            Collection<String> images = FileUtils.ImageUtils.listImageFilesFast1(dirPath);

            long totalTime = System.nanoTime() - startTime;
            logTime(totalTime / 1_000_000L);

            if (images.isEmpty()) {
                System.out.println("Изображений не найдено.");
                return;
            }

            System.out.println("Найдено " + images.size() + " изображений");
            processImages(dirPath, images);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка обработки файлов: " + e.getMessage());
        }
    }

    private void logTime(long totalTimeMs) {
        if (totalTimeMs < 1000L)
            System.out.println("Время выполнения: " + totalTimeMs + " мс");
        else
            System.out.println("Время выполнения: " + (totalTimeMs / 1000L)  + " с");
    }

    private static void processImages(Path directoryPath, Collection<String> imageNames) throws IOException {
        int processed = 0;
        int skipped = 0;
        int errors = 0;

        for (String imageName : imageNames) {
            try {
                if (processSingleImage(directoryPath, imageName))
                    processed++;
                else
                    skipped++;
            } catch (Exception e) {
                errors ++;
                System.err.println("Не удалось обработать файл " + imageName);
            }
        }

        System.out.println();
        System.out.println(processed + " files processed");
        System.out.println(skipped + " files skipped");
        System.out.println(errors + " files failed");
    }

    private static boolean processSingleImage(Path directoryPath, String imageName)
            throws IOException, ImageProcessingException {

        Path imagePath = directoryPath.resolve(imageName);

        if (!Files.isRegularFile(imagePath)) {
            throw new ImageProcessingException("Not a regular file", null);
        }

        FileMetadata metadata = extractFileInfo(imagePath);

        String newName = strategy.generateName(metadata);
        Path newPath = directoryPath.resolve(newName);

        boolean nameIsCorrect = imagePath.equals(newPath);

        if (nameIsCorrect)
            return false;

        System.out.println(imagePath.getFileName() + " -> " + newName);

        return FileUtils.safeMove(imagePath, newPath);
    }

    private static FileMetadata extractFileInfo(Path filePath) throws IOException {
        File file = filePath.toFile();

        @SuppressWarnings("UnnecessaryLocalVariable")
        FileMetadata info = new FileMetadata(
                resolver.getDateTime(file).orElseThrow(() ->
                        new DateTimeException("Дата не определена")),
                MD5Reader.getMD5(file),
                FileUtils.ExtensionUtils.getExtension(filePath.getFileName().toString())
        );
        return info;
    }
}

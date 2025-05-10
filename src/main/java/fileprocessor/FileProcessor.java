package fileprocessor;

import com.drew.imaging.ImageProcessingException;
import filedata.datetime.CompositeDateTimeReader;
import filedata.md5.MD5Reader;
import utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class FileProcessor {

    public void processDirectory(String path) {

        try {
            Path dirPath = validateAndGetPath(path);
            Collection<String> images = FileUtils.ImageUtils.listImageFiles(dirPath);

            if (images.isEmpty()) {
                System.out.println("Изображений не найдено.");
                return;
            }
            processImages(dirPath, images);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("Ошибка обработки файлов: " + e.getMessage());
        }
    }

    private static Path validateAndGetPath(String path) {
        Path p = Paths.get(path);
        if (!Files.exists(p)) {
            throw new IllegalArgumentException("Ошибка: путь '" + path + "' не существует.");
        }
        if (!Files.isDirectory(p)) {
            throw new IllegalArgumentException("Ошибка: '" + path + "' не является папкой.");
        }
        return p;
    }

    private static void processImages(Path directoryPath, Collection<String> imageNames) throws IOException {
        for (String imageName : imageNames) {
            try {
                processSingleImage(directoryPath, imageName);
            } catch (Exception e) {
                System.err.println("Не удалось обработать файл " + imageName);
            }
        }
    }

    private static void processSingleImage(Path directoryPath, String imageName)
            throws IOException, ImageProcessingException {

        Path imagePath = directoryPath.resolve(imageName);

        if (!Files.isRegularFile(imagePath)) {
            throw new ImageProcessingException("Not a regular file", null);
        }

        FileInfo metadata = extractFileInfo(imagePath);
        Path newPath = generateNewPath(directoryPath, metadata);

        FileUtils.safeMove(imagePath, newPath);
    }


    private static FileInfo extractFileInfo(Path filePath) throws IOException {
        File file = filePath.toFile();
        CompositeDateTimeReader resolver = new CompositeDateTimeReader();
        return new FileInfo(
                resolver.getDateTime(file).orElseThrow(() ->
                        new DateTimeException("Дата не определена")),
                MD5Reader.getMD5(file),
                FileUtils.ExtensionUtils.getExtension(filePath.getFileName().toString())
        );
    }

    private static Path generateNewPath(Path dirPath, FileInfo info) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_(HH-mm)");
        String dateStr = formatter.format(info.getDateTime());
        String md5Prefix = info.getMd5().substring(0, 6);
        String newName = String.format("%s-%s.%s",
                dateStr,
                md5Prefix,
                info.getExtension().toUpperCase());

        return dirPath.resolve(newName);
    }
}

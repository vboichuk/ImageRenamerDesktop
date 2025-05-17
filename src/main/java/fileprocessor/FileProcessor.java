package fileprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Scanner;

/**
 * Базовый класс для обработки файлов с логированием
 */
public abstract class FileProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Получает список изображений в директории
     *
     * @param dirPath путь к директории
     * @return коллекция имен файлов
     * @throws IOException если возникла ошибка чтения
     */
    protected Collection<String> getImageFiles(Path dirPath) throws IOException {

        long startTime = System.nanoTime();
        Collection<String> images = FileUtils.ImageUtils.listImageFilesFast(dirPath);
        logDuration("Getting images list", (System.nanoTime() - startTime) / 1_000_000L);

        if (images.isEmpty()) {
            logger.info("No images found in directory: {}", dirPath);
        } else {
            logger.info("Found {} images in directory: {}", images.size(), dirPath);
        }

        return images;
    }

    protected void logError(String message, Exception e) {
        logger.error(message, e);
    }

    protected void logDuration(String operationName, long durationMs) {
        logger.debug("{} execution time: {} ms", operationName, durationMs);
    }

    protected boolean confirmOperation(String prompt) {
        String characters = "y/Y/+";
        Scanner scanner = new Scanner(System.in);
        System.out.printf("%s (%s):\n", prompt, characters);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("+");
    }
}

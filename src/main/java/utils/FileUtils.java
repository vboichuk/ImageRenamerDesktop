package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileUtils {
    private FileUtils() {} // Запрет создания экземпляров

    public static boolean isDirectory(String path) {
        return Files.isDirectory(Paths.get(path));
    }

    public static Collection<String> listFilesByExtension(Path dir, String extensionPattern) {
        try (Stream<Path> list = Files.list(dir)) {
            return list
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.toLowerCase().matches(extensionPattern))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    public static boolean safeMove(Path source, Path target) {
        try {
            Files.move(source, target);
            return true;
        } catch (IOException e) {
            System.err.printf("Ошибка перемещения %s -> %s: %s%n",
                    source, target, e.getMessage());
            return false;
        }
    }

    public static class ImageUtils {
        private static final String IMAGE_PATTERN = ".*\\.(jpg|jpeg|png|gif|bmp|webp)$";

        /**
         * Получает список изображений в директории
         */
        public static Collection<String> listImageFiles(Path dir) {
            return FileUtils.listFilesByExtension(dir, IMAGE_PATTERN);
        }

        /**
         * Проверяет, является ли файл изображением
         */
        public static boolean isImageFile(Path file) {
            return file.getFileName().toString().matches(IMAGE_PATTERN);
        }

    }

    public static final class PathUtils {
        /**
         * Создает Path, добавляя к базовому пути компоненты
         */
        public static Path buildPath(Path base, String... parts) {
            Path result = base;
            for (String part : parts) {
                result = result.resolve(part);
            }
            return result;
        }

        /**
         * Нормализует путь для текущей ОС
         */
        public static String normalizePath(String path) {
            return Paths.get(path).normalize().toString();
        }
    }

    public static final class ExtensionUtils {
        /**
         * Извлекает расширение файла в нижнем регистре
         *
         * @return расширение без точки или пустую строку
         */
        public static String getExtension(String filename) {
            if (filename == null) return "";

            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex < 0 || dotIndex == filename.length() - 1) {
                return "";
            }
            return filename.substring(dotIndex + 1).toLowerCase();
        }

        /**
         * Изменяет расширение файла
         */
        public static String changeExtension(String filename, String newExtension) {
            String withoutExt = filename.replaceFirst("\\.[^.]*$", "");
            return withoutExt + "." + newExtension;
        }
    }

}

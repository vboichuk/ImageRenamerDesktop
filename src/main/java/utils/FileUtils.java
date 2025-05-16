package utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileUtils {
    private FileUtils() {} // Запрет создания экземпляров

    public static Path getDirectory(String path) {
        Path p = Paths.get(path).toAbsolutePath();

        if (!Files.exists(p)) {
            throw new IllegalArgumentException("Error: path '" + path + "' does not exists");
        }
        if (!Files.isDirectory(p)) {
            throw new IllegalArgumentException("Error: path '" + path + "' is not a folder");
        }
        return p.normalize();
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

    public static boolean safeMove(Path source, Path target) throws IOException {
        if (source.equals(target))
            return false;
        try {
            Files.move(source, target);
            return true;
        } catch (IOException e) {
            System.err.printf("Ошибка перемещения %s -> %s: %s%n",
                    source, target, e.getMessage());
            throw  e;
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

        public static Collection<String> listImageFilesFast(Path dir) {
            // jpg|jpeg|png|gif|bmp|webp

            List<String> result = new ArrayList<>();

            DirectoryStream.Filter<Path> filter = path -> {
                if (!Files.isRegularFile(path))
                    return false;
                if (Files.isHidden(path))
                    return false;
                String lowerName = path.getFileName().toString().toLowerCase();
                return lowerName.endsWith("jpg") || lowerName.endsWith("jpeg") || lowerName.endsWith("png");
            };

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, filter)) {
                for (Path entry : stream) {
                    result.add(entry.getFileName().toString());
                }
                return result;
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }

        public static Collection<String> listImageFilesFast2(Path dir) {
            // jpg|jpeg|png|gif|bmp|webp

            try (Stream<Path> list = Files.list(dir).parallel()) {
                return list
                        .filter(p -> {
                                    if (!Files.isRegularFile(p)) return false;
                                    try {
                                        return !Files.isHidden(p);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .filter(n -> {
                            String lowerCase = n.toLowerCase();
                            return  lowerCase.endsWith(".jpg")  ||
                                    lowerCase.endsWith(".jpeg") ||
                                    lowerCase.endsWith(".png");
                        })

                        .collect(Collectors.toList());
            } catch (IOException e) {
                return Collections.emptyList();
            }
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

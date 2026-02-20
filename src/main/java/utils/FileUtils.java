package utils;

import exception.InvalidFileNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public final class FileUtils {
    private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

    private FileUtils() {
    }

    public static Path getDirectory(String path) {
        Objects.requireNonNull(path, "Path cannot be null");
        Path p = Paths.get(path).toAbsolutePath().normalize();

        if (!Files.exists(p)) {
            logger.debug("path: {}", p);
            throw new IllegalArgumentException("Error: path '" + path + "' does not exist");
        }
        if (!Files.isDirectory(p)) {
            throw new IllegalArgumentException("Error: path '" + path + "' is not a folder");
        }
        return p;
    }

    /**
     * Безопасно перемещает или переименовывает файл.
     * Создаёт родительскую директорию для целевого файла, если она не существует.
     * Перемещает файл с перезаписью существующего целевого файла
     * @param source путь к исходному файлу, который нужно переместить (должен существовать)
     * @param target путь к целевому файлу
     * @return {@code true} если файл был успешно перемещён;
     *         {@code false} если source и target указывают на один и тот же файл
     * @throws IOException если произошла ошибка ввода-вывода:
     *                     <ul>
     *                       <li>исходный файл не существует</li>
     *                       <li>не удалось создать родительскую директорию</li>
     *                       <li>недостаточно прав доступа</li>
     *                       <li>диск переполнен или другая системная ошибка</li>
     *                     </ul>
     * @throws NullPointerException если source или target равны {@code null}
     * @throws SecurityException если менеджер безопасности запрещает доступ к файлам
     */
    public static boolean safeMove(Path source, Path target) throws IOException {
        Objects.requireNonNull(source, "Source path cannot be null");
        Objects.requireNonNull(target, "Target path cannot be null");

        if (source.equals(target))
            return false;

        Path parentDir = target.toAbsolutePath().getParent();
        if (parentDir != null && Files.notExists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        return true;
    }

    public static class ImageUtils {

        public static Collection<String> listJpgFiles(Path dir) {

            List<String> result = new ArrayList<>();

            DirectoryStream.Filter<Path> filter = path -> {
                if (!Files.isRegularFile(path))
                    return false;
                if (Files.isHidden(path))
                    return false;
                String lowerName = path.getFileName().toString().toLowerCase();
                return lowerName.endsWith("jpg") || lowerName.endsWith("jpeg");
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
    }

    public static final class PathUtils {

        public static final Character[] INVALID_FILENAME_CHARS = {'\000', '\\', ':', '*', '?', '"', '<', '>', '|'};

        public static String getFilenameWithoutExt(String filename) {
            return filename.replaceFirst("\\.[^.]*$", "");
        }

        /**
         * Валидирует имя файла
         *
         * @param filename имя файла для проверки
         * @throws InvalidFileNameException если имя файла недопустимо
         */
        public static void validateFileName(String filename) throws InvalidFileNameException {
            if (filename == null) {
                throw new InvalidFileNameException("Filename cannot be null", null);
            }

            if (filename.trim().isEmpty()) {
                throw new InvalidFileNameException("Filename cannot be empty or contain only whitespace", filename);
            }

            if (filename.length() > 255) {
                throw new InvalidFileNameException(
                        String.format("Filename exceeds maximum length of 255 characters (current: %d)", filename.length()),
                        filename
                );
            }

            if (!filename.equals(filename.trim())) {
                throw new InvalidFileNameException("Filename cannot start or end with whitespace: \"" + filename + "\"", filename);
            }

            for (Character invalidChar : INVALID_FILENAME_CHARS) {
                if (filename.contains(invalidChar.toString())) {
                    throw new InvalidFileNameException(
                            String.format("Filename contains invalid character: '%c' (ASCII: %d)", invalidChar, (int) invalidChar),
                            filename
                    );
                }
            }
        }

        public static String getExtension(String filename) {
            if (filename == null)
                return "";

            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex < 0 || dotIndex == filename.length() - 1) {
                return "";
            }
            return filename.substring(dotIndex + 1);
        }
    }
}

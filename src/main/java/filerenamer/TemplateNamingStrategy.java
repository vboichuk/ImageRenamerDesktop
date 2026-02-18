package filerenamer;

import com.drew.lang.annotations.NotNull;
import fileprocessor.FileMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Стратегия именования файлов на основе шаблона.
 * Поддерживает плейсхолдеры:
 * - {original[:format]} - оригинальное имя файла
 * - {date[:pattern][|default]} - дата/время (pattern опционален)
 * - {camera[:format][|default]} - модель камеры
 * - {hash[:length][|default]} - MD5 хеш
 * - {ext[:format][|default]} - расширение файла
 * Форматы: length (первые N символов), lower, upper
 */

public class TemplateNamingStrategy implements FileNamingStrategy {

    private static final Pattern ORIGINAL_NAME_PATTERN = Pattern.compile("\\{original(?::([^}|]+))?\\}");
    private static final Pattern DATETIME_PATTERN = Pattern.compile("\\{date(?::([^}]+))?\\}");
    private static final Pattern CAMERA_PATTERN = Pattern.compile("\\{camera(?::([^}|]+))?(?:\\|([^}]+))?\\}");
    private static final Pattern MD5_PATTERN = Pattern.compile("\\{hash(?::(\\d+))?(?:\\|([^}]+))?\\}");
    private static final Pattern EXT_PATTERN = Pattern.compile("\\{ext(?::([^}|]+))?(?:\\|([^}]+))?\\}");

    private static final String DEFAULT_DATETIME_FORMAT = "yyyy.MM.dd_HH-mm";
    private static final String LOWER_FORMAT = "lower";
    private static final String UPPER_FORMAT = "upper";
    private static final String NODATE = "nodate";
    private static final Logger log = LoggerFactory.getLogger(TemplateNamingStrategy.class);

    private final String template;

    public TemplateNamingStrategy(String template) {
        this.template = template;
    }

    @Override
    public String generateName(FileMetadata metadata) {
        String newName = template;
        newName = processOriginalName(newName, metadata);
        newName = processDateTime(newName, metadata);
        newName = processCamera(newName, metadata);
        newName = processHash(newName, metadata);
        newName = processExtension(newName, metadata);
        newName = validateName(newName);
        return newName;
    }

    public static String processOriginalName(String text, FileMetadata metadata) {
        Matcher matcher = ORIGINAL_NAME_PATTERN.matcher(text);
        return matcher.replaceAll(match -> {
             String originalName = metadata.getOriginalName();
             String format = match.group(1);
             return processPlaceholder(originalName, format, "");
        });
    }

    private String validateName(String newName) {
        /// TODO: implement
        return newName;
    }


    public String processDateTime(String text, FileMetadata metadata) {
        Matcher matcher = DATETIME_PATTERN.matcher(text);
        return matcher.replaceAll(match -> {
            String format = match.group(1);
            return formatDate(metadata.getDateTime(), format);
        });
    }

    protected String processCamera(String text, FileMetadata metadata) {
        Matcher matcher = CAMERA_PATTERN.matcher(text);
        return matcher.replaceAll(match -> {
            String model = metadata.getCameraModel();
            String format = match.group(1);
            String defaultValue = match.group(2);
            return processPlaceholder(model, format, defaultValue);
        });
    }

    private String processHash(String text, FileMetadata metadata) {
        Matcher matcher = MD5_PATTERN.matcher(text);
        return matcher.replaceAll(match -> {
            String hash = metadata.getMd5();
            return processPlaceholder(hash, match.group(1), match.group(2));
        });
    }

    private String processExtension(String text, FileMetadata metadata) {
        Matcher matcher = EXT_PATTERN.matcher(text);
        return matcher.replaceAll(match -> {
            String ext = metadata.getExtension();
            return processPlaceholder(ext, match.group(1), match.group(2));
        });
    }

    private static String processPlaceholder(String text, String format, String defaultValue) {
        if (text == null || text.isBlank()) {
            return defaultValue != null ? defaultValue : "";
        }

        if (format == null || format.isBlank())
            return text;

        String result = text;
        String[] split = format.split(":");
        for (String s : split) {
            result = applyFormat(result, s);
        }
        return result;
    }

    /**
     * Применяет указанный формат к заданной строке.
     * Метод поддерживает различные типы форматирования: обрезку строки по длине, преобразование регистра.
     *
     * @param text исходная строка, которую необходимо отформатировать.
     *             Не должна быть null (в противном случае будет выброшено NullPointerException).
     * @param format формат, определяющий способ преобразования строки.
     *               Поддерживаются следующие значения:
     *               <ul>
     *                 <li><b>Число</b> (например, "5", "10", "100") - возвращает первые N символов строки.
     *                     Если число превышает длину строки, возвращается вся строка целиком.</li>
     *                 <li><b>"lower"</b> - преобразует все символы строки в нижний регистр.</li>
     *                 <li><b>"upper"</b> - преобразует все символы строки в верхний регистр.</li>
     *                 <li><b>Любое другое значение</b> - возвращает исходную строку без изменений.</li>
     *               </ul>
     * @return отформатированная строка согласно указанному формату.
     * @throws NullPointerException если параметр text равен null
     *
     * @example
     * String result1 = applyFormat("Hello World", "5");    // Вернет "Hello"
     * String result2 = applyFormat("Hello World", "20");   // Вернет "Hello World" (вся строка)
     * String result3 = applyFormat("Hello World", "lower"); // Вернет "hello world"
     * String result4 = applyFormat("Hello World", "upper"); // Вернет "HELLO WORLD"
     * String result5 = applyFormat("Hello World", "reverse"); // Вернет "Hello World" (без изменений)
     *
     * @see Integer#parseInt(String)
     * @see String#substring(int, int)
     * @see String#toLowerCase()
     * @see String#toUpperCase()
     */
    private static String applyFormat(String text, String format) {
        if (format == null || text == null)
            return text;
        return switch (format.toLowerCase()) {
            case LOWER_FORMAT -> text.toLowerCase();
            case UPPER_FORMAT -> text.toUpperCase();
            default -> applyNumericFormat(text, format);
        };
    }

    private static String applyNumericFormat(@NotNull String text, @NotNull String format) {

        try {
            // Попытка интерпретировать как число (первые N символов)
            int length = Integer.parseInt(format);
            if (length > 0) {
                return text.substring(0, Math.min(length, text.length()));
            }
        } catch (NumberFormatException ignored) { }
        return text;
    }

    private String formatDate(LocalDateTime date, String pattern) {
        if (date == null)
            return NODATE;
        return DateTimeFormatter.ofPattern(pattern != null ? pattern : DEFAULT_DATETIME_FORMAT).format(date);
    }
}

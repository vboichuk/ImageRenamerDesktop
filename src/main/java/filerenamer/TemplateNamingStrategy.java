package filerenamer;

import fileprocessor.FileMetadata;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateNamingStrategy implements FileNamingStrategy {

    private static final Pattern DATETIME_PATTERN = Pattern.compile("\\{date(?::([^}]+))?\\}");
    private static final Pattern CAMERA_PATTERN = Pattern.compile("\\{camera(?::([^}|]+))?(?:\\|([^}]+))?\\}");
    private static final Pattern MD5_PATTERN = Pattern.compile("\\{hash(?::(\\d+))?(?:\\|([^}]+))?\\}");
    private static final Pattern EXT_PATTERN = Pattern.compile("\\{ext(?::([^}|]+))?(?:\\|([^}]+))?\\}");

    private static final String DEFAULT_DATETIME_FORMAT = "yyyy.MM.dd_HH-mm";

    private final String template;

    public TemplateNamingStrategy(String template) {
        this.template = template;
    }

    @Override
    public String generateName(FileMetadata metadata) {
        String newName = template;
        newName = processDateTime(newName, metadata);
        newName = processCamera(newName, metadata);
        newName = processHash(newName, metadata);
        newName = processExtension(newName, metadata);
        newName = validateName(newName);
        return newName;
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
            return processPlaceholder(model, match.group(1), match.group(2));
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

    private String processPlaceholder(String text, String format, String defaultValue) {
        if (text == null || text.isBlank()) {
            return defaultValue != null ? defaultValue : "";
        }

        if (format != null) {
            return applyFormat(text, format);
        }

        return text;
    }

    private String applyFormat(String text, String format) {
        try {
            // Попытка интерпретировать как число (первые N символов)
            int length = Integer.parseInt(format);
            return text.substring(0, Math.min(length, text.length()));
        } catch (NumberFormatException ignored) { }

        return switch (format.toLowerCase()) {
            case "lower" -> text.toLowerCase();
            case "upper" -> text.toUpperCase();
            default -> text;
        };
    }

    private String formatDate(LocalDateTime date, String pattern) {
        if (date == null)
            return "nodate";
        return DateTimeFormatter.ofPattern(pattern != null ? pattern : DEFAULT_DATETIME_FORMAT).format(date);
    }
}

package filerenamer;

import fileprocessor.FileMetadata;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateNamingStrategy implements FileNamingStrategy {

    private static final Pattern dateTimePattern = Pattern.compile("\\{date(?::([^}]+))?\\}");
    private static final Pattern cameraPattern = Pattern.compile("\\{camera(?::([^}|]+))?(?:\\|([^}]+))?\\}");
    private static final Pattern md5Pattern = Pattern.compile("\\{hash(?::(\\d+))?(?:\\|([^}]+))?\\}");
    private static final Pattern extPattern = Pattern.compile("\\{ext(?::([^}|]+))?(?:\\|([^}]+))?\\}");

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
        Matcher matcher = dateTimePattern.matcher(text);
        return matcher.replaceAll(match -> {
            String format = match.group(1);
            return formatDate(metadata.getDateTime(), format);
        });
    }

    protected String processCamera(String text, FileMetadata metadata) {
        Matcher matcher = cameraPattern.matcher(text);
        return matcher.replaceAll(match -> {
            String model = metadata.getCameraModel();
            return processPlaceholder(model, match.group(1), match.group(2));
        });
    }

    private String processHash(String text, FileMetadata metadata) {
        Matcher matcher = md5Pattern.matcher(text);
        return matcher.replaceAll(match -> {
            String hash = metadata.getMd5();
            return processPlaceholder(hash, match.group(1), match.group(2));
        });
    }

    private String processExtension(String text, FileMetadata metadata) {
        Matcher matcher = extPattern.matcher(text);
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
            // Обработка формата (первые N символов или регистр)
            text = applyFormat(text, format);
        }

        return text;
    }

    private String applyFormat(String text, String format) {
        try {
            // Попытка интерпретировать как число (первые N символов)
            int length = Integer.parseInt(format);
            return text.substring(0, Math.min(length, text.length()));
        } catch (NumberFormatException ignored) {}

        return switch (format.toLowerCase()) {
            case "lower" -> text.toLowerCase();
            case "upper" -> text.toUpperCase();
            default -> text;
        };
    }

    private String formatDate(LocalDateTime date, String pattern) {
        if (date == null) return "nodate";
        return DateTimeFormatter.ofPattern(pattern != null ? pattern : "yyyy.MM.dd_HH-mm").format(date);
    }
}

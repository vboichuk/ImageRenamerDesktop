package filedata.datetime;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;

public class FileNameDateTimeReader implements DateTimeReader {

    @Override
    public Optional<LocalDateTime> getDateTime(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        String filename = file.getName();
        filename = filename.substring(0, 18);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_(HH-mm)", Locale.getDefault());
        try {
            return Optional.of(LocalDateTime.parse(filename, formatter));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
}

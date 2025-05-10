package filedata.datetime;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public class FileSystemDateTimeReader implements DateTimeReader {
    @Override
    public Optional<LocalDateTime> getDateTime(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        try {
            BasicFileAttributes attrs = Files.readAttributes(
                    file.toPath(),
                    BasicFileAttributes.class
            );
            return Optional.of(
                    LocalDateTime.ofInstant(
                            attrs.creationTime().toInstant(),
                            ZoneId.systemDefault()
                    )
            );
        } catch (IOException | SecurityException e) {
            return Optional.empty();
        }
    }
}

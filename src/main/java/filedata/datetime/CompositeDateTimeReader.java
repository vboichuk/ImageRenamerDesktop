package filedata.datetime;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CompositeDateTimeReader implements DateTimeReader {
    Collection<DateTimeReader> readers;

    public CompositeDateTimeReader() {
        this.readers = List.of(
                new DateTimeExifReader(),
                new FileNameDateTimeReader()
                // new FileSystemDateTimeReader(ZoneId.of("UTC+3"))
        );
    }

    public CompositeDateTimeReader(Collection<DateTimeReader> readers) {
        if (readers == null || readers.isEmpty()) {
            throw new IllegalArgumentException("Readers collection cannot be null or empty");
        }
        this.readers = new ArrayList<>(readers);
    }

    @Override
    public Optional<LocalDateTime> getDateTime(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        for (DateTimeReader reader : readers) {
            Optional<LocalDateTime> result = reader.getDateTime(file);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

}

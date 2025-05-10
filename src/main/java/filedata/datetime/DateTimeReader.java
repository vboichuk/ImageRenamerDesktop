package filedata.datetime;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

public interface DateTimeReader {
    Optional<LocalDateTime> getDateTime(File file) throws IOException;
}

package filedata.datetime;

import exifreader.ExifReader;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Optional;


public class DateTimeExifReader implements DateTimeReader {

    @Override
    public Optional<LocalDateTime> getDateTime(File imageFile) {
        return ExifReader.getDateTime(imageFile);
    }
}
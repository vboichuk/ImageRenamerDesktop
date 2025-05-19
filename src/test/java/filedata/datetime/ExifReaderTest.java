package filedata.datetime;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;

import static org.junit.Assert.*;

public class ExifReaderTest extends BaseDatetimeReaderTest {

    public ExifReaderTest() {
        reader = new DateTimeExifReader();
    }


}
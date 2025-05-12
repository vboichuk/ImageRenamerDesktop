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
        reader = new ExifReader();
    }

    @Test
    public void getDateTime() throws IOException {
        File file = null;
        try {
            file = getFile("2023.07.25_(19-05)-a34a3b.JPG");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Optional<LocalDateTime> dateTimeOpt = reader.getDateTime(file);

        assertTrue(dateTimeOpt.isPresent());

        assertEquals(2023, dateTimeOpt.get().getYear());
        assertEquals(Month.JULY, dateTimeOpt.get().getMonth());
        assertEquals(25, dateTimeOpt.get().getDayOfMonth());
    }

    @Test
    public void getDateTimeNoExif() {
        File file;
        try {
            file = getFile("2025.05.11_(12-50)-9b3d70.png");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Optional<LocalDateTime> dateTimeOpt = reader.getDateTime(file);

        assertFalse(dateTimeOpt.isPresent());
    }

    @Test
    public void getDateTimeNoFile() {
        File file = dir.resolve("Screenshot_100500.png").toFile();

        Optional<LocalDateTime> dateTimeOpt = reader.getDateTime(file);

        assertFalse(dateTimeOpt.isPresent());
    }
}
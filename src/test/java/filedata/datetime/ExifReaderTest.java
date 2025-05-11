package filedata.datetime;

import org.junit.Test;

import java.io.File;
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
    public void getDateTime() {
        File file = getFile("2023.07.25_(19-05)-a34a3b.JPG");

        Optional<LocalDateTime> dateTimeOpt;
        try {
            dateTimeOpt = reader.getDateTime(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertTrue(dateTimeOpt.isPresent());

        assertEquals(2023, dateTimeOpt.get().getYear());
        assertEquals(Month.JULY, dateTimeOpt.get().getMonth());
        assertEquals(25, dateTimeOpt.get().getDayOfMonth());
    }

    @Test
    public void getDateTimeNoExif() {
        File file = getFile("Screenshot_2025-05-11.png");

        Optional<LocalDateTime> dateTimeOpt;
        try {
            dateTimeOpt = reader.getDateTime(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertFalse(dateTimeOpt.isPresent());
    }
}
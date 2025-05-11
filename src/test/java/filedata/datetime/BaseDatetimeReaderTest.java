package filedata.datetime;

import org.junit.Assert;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseDatetimeReaderTest {
    final static Path dir = Paths.get("/Users/valentina/Downloads/image_renamer_test_files");

    protected DateTimeReader reader;

    protected File getFile(String name) {
        Path p = dir.resolve(name);
        File file = p.toFile();
        Assert.assertTrue(file.exists());
        Assert.assertFalse(file.isDirectory());
        return file;
    }
}

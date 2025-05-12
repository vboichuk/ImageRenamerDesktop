package filedata.datetime;

import org.junit.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseDatetimeReaderTest {
    final static Path dir = Paths.get("/Users/valentina/Downloads/image_renamer_test_files");

    protected DateTimeReader reader;

    protected File getFile(String name) throws FileNotFoundException {
        Path p = dir.resolve(name);
        File file = p.toFile();
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException(name);
        }
        return file;
    }
}

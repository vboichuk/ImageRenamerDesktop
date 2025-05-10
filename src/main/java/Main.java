import fileprocessor.FileProcessor;
import filedata.datetime.DateTimeReader;
import filedata.datetime.DateTimeResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Main {
    private static final DateTimeReader resolver = new DateTimeResolver();

    public static void main(String[] args) {

        if (args.length == 0) {
            System.err.println("Usage: java Main <directory-path>");
            return;
        }
        try {
            new FileProcessor().processDirectory(args[0]);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

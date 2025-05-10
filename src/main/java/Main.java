import fileprocessor.FileProcessor;
import filedata.datetime.DateTimeReader;
import filedata.datetime.CompositeDateTimeReader;


public class Main {
    private static final DateTimeReader resolver = new CompositeDateTimeReader();

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

import fileprocessor.FileProcessor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@CommandLine.Command(
        name = "FileRenamerDesktop",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "Tool for renaming files and editing EXIF data"
)
public class Main {

    @Option(names = {"-d", "--directory"}, description = "Directory", defaultValue = ".")
    private String directory;

    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }

    @Command(description = "Rename files in the specified directory")
    public int rename() {
        try {
            new FileProcessor().rename(directory);
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    @Command(description = "Edit EXIF data of files in the specified directory")
    public int editexif() {
        try {
            new FileProcessor().editExif(directory);
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }
}

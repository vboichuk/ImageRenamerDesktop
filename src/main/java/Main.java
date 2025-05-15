import fileprocessor.FileProcessor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@CommandLine.Command(
        name = "FileRenamerDesktop",
        mixinStandardHelpOptions = true,
        version = "1.0",
        description = "FileRenamerDesktop"
)
public class Main {

    @Option(names = {"-d", "--directory"}, description = "Directory")
    private String directory;

    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }

    @Command
    public void rename() {
        if (directory == null) {
            System.out.println("Directory is not set");
            return;
        }
        System.out.println("Directory: " + directory);
        try {
            new FileProcessor().processDirectory(directory);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

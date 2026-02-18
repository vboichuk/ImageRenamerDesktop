import fileprocessor.FileExifEditor;
import filerenamer.FileRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/*
Список задач
1. Добавить сортировку списка изображений
*/

@CommandLine.Command(
        name = "FileRenamerDesktop",
        mixinStandardHelpOptions = true,
        version = "1.2",
        description = "Tool for renaming images and editing EXIF data"
)
public class Main {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("unused")
    @Option(names = {"-d", "--directory"}, description = "Directory", defaultValue = ".")
    private String directory;

    @SuppressWarnings("unused")
    @Option(names = {"-t", "--template"},
            description = "Naming pattern (e.g. \"{date:yyyyMMdd}_{model}.{ext}\")",
            defaultValue = "{date:yyyy.MM.dd}_({date:HH-mm})-{hash:6}.{ext:upper}"
    )
    private String template;

    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }

    @SuppressWarnings("unused")
    @Command(description = "Rename files in the specified directory")
    public int rename() {

        try {
            new FileRenamer().rename(directory, template);
            return 0;
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
            return 1;
        }
    }

    @SuppressWarnings("unused")
    @Command(description = "Edit EXIF data of files in the specified directory")
    public int editexif() {
        try {
            new FileExifEditor().editExif(directory);
            return 0;
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage(), e);
            return 1;
        }
    }
}

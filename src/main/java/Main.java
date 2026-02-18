import fileprocessor.FileExifEditor;
import filerenamer.FileRenamer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/*
Список задач
1. Если в файле нет никакой exif-информации - не удастся добавить DateTimeOriginal
2. Добавить возможность задавать формат имени с командной строки
3. Добавить сортировку списка изображений
 */

@CommandLine.Command(
        name = "FileRenamerDesktop",
        mixinStandardHelpOptions = true,
        version = "1.1",
        description = "Tool for renaming images and editing EXIF data"
)
public class Main {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Option(names = {"-d", "--directory"}, description = "Directory", defaultValue = ".")
    private String directory;

    @Option(names = {"-t", "--template"},
            description = "Naming pattern (e.g. \"{date:yyyyMMdd}_{model}.{ext}\")",
            defaultValue = "{date:yyyy.MM.dd}_({date:HH-mm})-{hash:6}.{ext:upper}"
    )
    private String template;

    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }

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

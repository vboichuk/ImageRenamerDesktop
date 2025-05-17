import fileprocessor.FileExifEditor;
import filerenamer.FileRenamer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/*
Список проблем
1. Если в файле нет никакой exif-информации - не удастся добавить DateTimeOriginal
2. Сначала просчет - затем переименование
3. Добавить возможность задавать формат имени с командной строки
 */


@CommandLine.Command(
        name = "FileRenamerDesktop",
        mixinStandardHelpOptions = true,
        version = "1.1",
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
            new FileRenamer().rename(directory);
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }

    @Command(description = "Edit EXIF data of files in the specified directory")
    public int editexif() {
        try {
            new FileExifEditor().editExif(directory);
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }
}

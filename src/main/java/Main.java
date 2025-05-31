import fileprocessor.FileExifEditor;
import filerenamer.FileRenamer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/*
Список проблем
1. Если в файле нет никакой exif-информации - не удастся добавить DateTimeOriginal
2. Добавить возможность задавать формат имени с командной строки
3. Добавить сортировку списка изображений
4. Добавить паттерн "originalName"
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

    @Option(names = {"-t", "--template"},
            description = "Naming pattern (e.g. \"{date:yyyyMMdd}_{model}.{ext}\")",
            defaultValue = "{date:yyyy.MM.dd}_({date:HH-mm})-{hash:6}.JPG"
            // defaultValue = "{date:yyyy.MM.dd_HH-mm}-{hash}.{ext:upper}"
    )
    // defaultValue = "{camera}/{date:yyyy.MM.dd_HH-mm}-{md5}.{ext}"
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

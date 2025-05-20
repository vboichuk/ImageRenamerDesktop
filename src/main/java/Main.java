import fileprocessor.FileExifEditor;
import filerenamer.FileRenamer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Список проблем
1. Если в файле нет никакой exif-информации - не удастся добавить DateTimeOriginal
2. Добавить возможность задавать формат имени с командной строки
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
            defaultValue = "{date:yyyy.MM.dd_HH-mm}-{md5}.{ext}"
    )
    // defaultValue = "{model}/{date:yyyy.MM.dd_HH-mm}-{md5}.{ext}"
    private String template;

    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }

    @Command(description = "Rename files in the specified directory")
    public int rename() {

        String text = "{camera:jpg|default}, {camera:png}, {camera|backup}, {camera}";
        Pattern pattern = Pattern.compile("\\{camera(:[^}|]+)?(\\|[^|}]+)?\\}");
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            System.out.println("groups:" + matcher.groupCount());
            System.out.println("Full match: " + matcher.group(0));
            System.out.println("format: " + matcher.group(1));
            System.out.println("default: " + matcher.group(2));
            System.out.println();
        }
//        if (true)
//            return 0;

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

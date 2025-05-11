package fileprocessor;

import java.time.LocalDateTime;

public class FileMetadata {
    private final LocalDateTime dateTime;
    private final String md5;
    private final String extension;

    public FileMetadata(LocalDateTime dateTime, String md5, String extension) {
        this.dateTime = dateTime;
        this.md5 = md5;
        this.extension = extension;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getMd5() {
        return md5;
    }

    public String getExtension() {
        return extension;
    }
}

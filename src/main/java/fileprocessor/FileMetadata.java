package fileprocessor;

import java.time.LocalDateTime;

public class FileMetadata {
    private LocalDateTime dateTime;
    private String md5;
    private String extension;
    private String cameraModel;
    private String originalName;

    public FileMetadata(LocalDateTime dateTime, String md5, String extension) {
        this.dateTime = dateTime;
        this.md5 = md5;
        this.extension = extension;
    }

    public FileMetadata() {

    }


    public String getOriginalName() {
        return originalName;
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

    public String getCameraModel() {
        return cameraModel;
    }


    public FileMetadata setOriginalName(String originalName) {
        this.originalName = originalName;
        return this;
    }

    public FileMetadata setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
        return this;
    }

    public FileMetadata setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public FileMetadata setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public FileMetadata setExtension(String extension) {
        this.extension = extension;
        return this;
    }
}

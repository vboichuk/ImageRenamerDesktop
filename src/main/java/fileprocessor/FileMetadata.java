package fileprocessor;

import java.time.LocalDateTime;

public class FileMetadata {
    private LocalDateTime dateTime;
    private String md5;
    private String extension;
    private String cameraModel;

    public FileMetadata(LocalDateTime dateTime, String md5, String extension) {
        this.dateTime = dateTime;
        this.md5 = md5;
        this.extension = extension;
    }

    public FileMetadata() {

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

    public void setCameraModel(String cameraModel) {
        this.cameraModel = cameraModel;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}

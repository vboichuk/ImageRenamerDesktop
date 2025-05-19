package filerenamer;

import exception.CameraModelException;
import fileprocessor.FileMetadata;

public interface FileNamingStrategy {
    String generateName(FileMetadata metadata) throws CameraModelException;
}

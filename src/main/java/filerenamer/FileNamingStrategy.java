package filerenamer;

import fileprocessor.FileMetadata;

public interface FileNamingStrategy {
    String generateName(FileMetadata metadata);
}

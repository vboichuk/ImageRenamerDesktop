package exception;

public class NoExifDataException extends Exception {
    public NoExifDataException() {
        super("The file does not have EXIF-data");
    }
}

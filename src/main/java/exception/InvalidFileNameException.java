package exception;

public class InvalidFileNameException extends Exception {
    private final String fileName;

    public InvalidFileNameException(String message, String fileName) {
        super(message);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

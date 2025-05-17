package fileprocessor;

public class ProcessingResult {

    private final String operationName;
    private int processed;
    private int skipped;
    private int failed;

    public ProcessingResult(String operationName) {
        this.operationName = operationName;
    }

    @Override
    public String toString() {
        return String.format(
                "\n%s result:\nProcessed: %d\nSkipped: %d\nFailed: %d",
                operationName,
                processed, skipped, failed);
    }

    public void incProcessed() {
        this.processed++;
    }

    public void incSkipped() {
        this.skipped++;
    }

    public void incFailed() {
        this.failed++;
    }
}

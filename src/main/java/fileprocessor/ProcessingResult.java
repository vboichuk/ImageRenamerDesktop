package fileprocessor;

public class ProcessingResult {
    private int processed;
    private int skipped;
    private int failed;


    @Override
    public String toString() {
        return String.format(
                "\nProcessing result:\nProcessed: %d\nSkipped: %d\nFailed: %d",
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

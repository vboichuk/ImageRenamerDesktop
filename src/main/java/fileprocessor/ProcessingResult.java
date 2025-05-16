package fileprocessor;

public class ProcessingResult {
    private int processed;
    private int skipped;
    private int failed;


    @Override
    public String toString() {
//        System.out.println(processed + " files processed");
//        System.out.println(skipped + " files skipped");
//        System.out.println(errors + " files failed");
        return "ProcessingResult{" +
                "processed=" + processed +
                ", skipped=" + skipped +
                ", failed=" + failed +
                '}';
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

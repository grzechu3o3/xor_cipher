import javax.swing.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class Cipher implements Runnable {
    private final File[] files;
    private final JProgressBar progressBar;
    private final AtomicInteger processedCount;

    public Cipher(File[] files, JProgressBar progressBar, AtomicInteger pC) {
        this.files = files;
        this.progressBar = progressBar;
        this.processedCount = pC;
        this.progressBar.setValue(0);
    }

    @Override
    public void run() {
        for(File f : files) {
            String data = FileHandling.readFile(f);
            StringBuilder cipherData = new StringBuilder();

            int key = 9;

            for(int i=0; i<data.length();i++) {
                cipherData.append((char)(data.charAt(i)^key));

                final int progress = (i + 1) * 100 / data.length();
                progressBar.setValue(progress);
            }
            FileHandling.writeFile(f, String.valueOf(cipherData));

            processedCount.incrementAndGet();
        }
    }
}

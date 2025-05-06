import javax.swing.*;
import java.io.File;

public class Cipher implements Runnable {
    private final File[] files;
    private final JProgressBar progressBar;

    public Cipher(File[] files, JProgressBar progressBar) {
        this.files = files;
        this.progressBar = progressBar;
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

                progressBar.setValue((i+1)*100 / data.length());
            }
            FileHandling.writeFile(f, String.valueOf(cipherData));
        }
    }
}

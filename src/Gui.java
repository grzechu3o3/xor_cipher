import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

class Gui extends JFrame {
    private static AtomicInteger processedCount = new AtomicInteger(0);

    public static void main(String[] args) {
        JFrame frame = new JFrame("Szyfrowanie plików");

        // Sprawdza czy argument istnieje
        if(args.length != 1) {
            JOptionPane.showMessageDialog(null, "Brak scieżki do zaszyfrowania! Powinna być argumentem programu!", "Błąd", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Tworzy listę plików w zadanym folderze
        File[] files = FileHandling.listFolder(new File(args[0]));

        // Liczy ilość plików do przydzielenia
        int file_count = files.length;
        int thread_count = 4;
        int files_per_one_thread = (int) Math.ceil((double)file_count / thread_count);


        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JProgressBar[] progressBars = new JProgressBar[thread_count];
        for (int i = 0; i < thread_count; i++) {
            progressBars[i] = new JProgressBar(0, 100);
            progressBars[i].setStringPainted(true);
            progressBars[i].setAlignmentX(Component.CENTER_ALIGNMENT);
            progressBars[i].setForeground(new Color(16,197,0));

            p.add(progressBars[i]);
            p.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        JButton start = new JButton("Start");
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(start);
        p.add(Box.createRigidArea(new Dimension(0, 5)));

        JProgressBar duzy = new JProgressBar(0,100);
        duzy.setStringPainted(true);
        duzy.setForeground(Color.RED);
        p.add(duzy);


        Thread[] threads = new Thread[thread_count];
        start.addActionListener(e -> {
            for(int i=0; i<thread_count; i++) {
                int start_i = i * files_per_one_thread;
                int end = Math.min(start_i+files_per_one_thread, file_count);

                File[] thread_files = new File[end-start_i];
                System.arraycopy(files, start_i, thread_files, 0, end-start_i);

                threads[i] = updateProgress(progressBars[i], thread_files);
            }

            // Wątek do kontroli "dużego" progress bara
            new Thread(() -> {
                while(true) {
                    int processed = processedCount.get();
                    int progress = (int) ((processed / (double) file_count) * 100);
                    duzy.setValue(progress);
                }
            }).start();

            // Wątek czekający na wykonanie i wyświetlający komunikat
            new Thread(() -> {
                try {
                    for(Thread t : threads) {
                        t.join();
                    }
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "Szyfrowanie zakończone!", "Informacja", JOptionPane.INFORMATION_MESSAGE);
                    });
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }).start();
        });


        frame.add(p);
        frame.setSize(480,240);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static Thread updateProgress(JProgressBar progressBar, File[] files) {
        try {
            Thread thread = new Thread(new Cipher(files, progressBar, processedCount));
            thread.start();
            return thread;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Thread.currentThread();
    }
}
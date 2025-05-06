import javax.swing.*;
import java.io.File;

class Gui extends JFrame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Szyfrowanie plików");

        // Sprawdza czy argument istnieje
        if(args.length != 1) {
            JOptionPane.showMessageDialog(null, "Brak scieżki do zaszyfrowania! Powinna być argumentem programu!", "Błąd", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Tworzy listę plików w zadanym folderze
        File[] files = FileHandling.listFolder(new File(args[0]));

        int file_count = files.length;
        int thread_count = 4;
        int files_per_one_thread = (int) Math.ceil((double)file_count / thread_count);
        System.out.println(files_per_one_thread);

        JPanel p = new JPanel();

        JProgressBar duzy = new JProgressBar(0,100);

        JProgressBar[] progressBars = new JProgressBar[thread_count];
        for (int i = 0; i < thread_count; i++) {
            progressBars[i] = new JProgressBar(0, 100);
            p.add(progressBars[i]);
        }


        JButton start = new JButton("Start");
        p.add(start);

        p.add(duzy);


        Thread[] threads = new Thread[thread_count];

        start.addActionListener(e -> {
            for(int i=0; i<thread_count; i++) {
                int start_i = i * files_per_one_thread;
                int end = Math.min(start_i+files_per_one_thread, file_count);

                File[] thread_files = new File[end-start_i];
                System.arraycopy(files, start_i, thread_files, 0, end-start_i);

                int finalI = i;


                threads[i] = updateProgress(progressBars[finalI], thread_files);

            }

            new Thread(() -> {
                while(true) {
                    int total = 0;
                    for(JProgressBar prg : progressBars) {
                        total += prg.getValue();
                    }
                    duzy.setValue(total/thread_count);

                }
            }).start();


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

        frame.setSize(800,320);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static Thread updateProgress(JProgressBar progressBar, File[] files) {
        try {
            Thread thread = new Thread(new Cipher(files, progressBar));
            thread.start();
            return thread;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Thread.currentThread();
    }
}
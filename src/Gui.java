import javax.swing.*;
import java.io.File;

class Gui extends JFrame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Szyfrowanie plików");

        if(args.length != 1) {
            System.out.println("Brak sciezki do zaszyfrowania! Powinna byc argumentem programu");
            System.exit(1);
        }

        File[] files = FileHandling.listFolder(new File(args[0]));
        System.out.println(args[0]);

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

        start.addActionListener(e -> {
            for(int i=0; i<thread_count; i++) {
                int start_i = i * files_per_one_thread;
                int end = Math.min(start_i+files_per_one_thread, file_count);

                File[] thread_files = new File[end-start_i];
                System.arraycopy(files, start_i, thread_files, 0, end-start_i);

                int finalI = i;

                new Thread(() -> updateProgress(progressBars[finalI], thread_files)).start();


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
        });

        frame.add(p);

        frame.setSize(800,600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void updateProgress(JProgressBar progressBar, File[] files) {
        try {
            Thread thread = new Thread(new Cipher(files, progressBar));
            System.out.println(thread.getName());
            thread.start();
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
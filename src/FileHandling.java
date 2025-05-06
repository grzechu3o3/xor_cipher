import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileHandling
{
    public static File[] listFolder(File path) {
        return path.listFiles();
    }

    public static double getFileSizeMb(File file) {
        if(file.exists() && file.isFile()) {
            return (double) file.length() / (1024*1024);
        } else {
            return 0;
        }
    }
    public static double getFileSizeKb(File file) {
        if(file.exists() && file.isFile()) {
            return (double) file.length();
        } else {
            return 0;
        }
    }
    public static void writeFile(File path, String data) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFile(File path) {
        try {
            Scanner reader = new Scanner(path);
            StringBuilder data = new StringBuilder();

            while(reader.hasNextLine()) {
                data.append(reader.nextLine());
            }
            return String.valueOf(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}

import java.io.*;
import org.apache.commons.io.IOUtils;

public class Find {

    public static void find(File file) throws Exception {
        if (file.isFile()) {   
            check(file);
        } else {   
            for (File child: file.listFiles()) {
                find(child);
            }
        }
    }

    public static void check(File file) throws Exception {
        if (file.getName().endsWith(".java")) {
            if (!IOUtils.toString(new FileInputStream(file)).startsWith("/* The")) {
                System.out.println(file.getPath());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        find(new File(args[0]));
    }
}

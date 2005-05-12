package fedora.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Utility class for executing commands and sending the command's output
 * to a given OutputStream.
 * @author Edwin Shin
 */
public class ExecUtility {
    public static Process exec(String cmd) {
        return exec(cmd, null, System.out);
    }
    
    public static Process exec(String cmd, OutputStream out) {
        return exec(cmd, null, out);
    }
    
    public static Process exec(String cmd, File dir) {
        return exec(cmd, dir, System.out);
    }
    
    public static Process exec(String cmd, File dir, OutputStream out) {
        Process cp = null;
        try {
            if (dir == null) {
                cp = Runtime.getRuntime().exec(cmd, null);
            } else {
                cp = Runtime.getRuntime().exec(cmd, null, dir);
            }
    
            // Print stdio of cmd
            if (out != null) {
                PrintStream pout = new PrintStream(out);
                String line;
                BufferedReader input = new BufferedReader(new InputStreamReader(cp
                        .getInputStream()));
                while ((line = input.readLine()) != null) {
                    pout.println(line);
                }
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cp;
    }
}

import java.io.*;
import java.util.*;

/** ##name##        BeerGlass
##title##       Smiley Beer Glass
##description## Blah deh blah
*/

public class Make {

    public static String readTemplate(String filename) throws Exception {
        StringBuffer out = new StringBuffer();
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename))));
        String line = in.readLine();
        while (line != null) {
            out.append(line);
            line = in.readLine();
            if (line != null) out.append("\n");
        }
        in.close();
        return out.toString();
    }

    public static String getTitle(String in) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < in.length(); i++) {
            if (i == 0) {
                out.append(in.charAt(i));
            } else {
                String ch = in.substring(i, i+1);
                String uc = ch.toUpperCase();
                if (ch == uc) {
                    out.append(' ');
                }
                out.append(ch);
            }
        }
        return out.toString();
    }

    public static void main(String[] args) throws Exception {
        String template = readTemplate("demo_SmileyTEMPLATE.xml");
        File dir = new File("\\work\\mellon\\src\\demo-content\\image-collection-demo");
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            String path = files[i].toString();
            if (path.indexOf("-FullSize.jpg") != -1) {
                String name = path.substring(path.lastIndexOf("Smiley") + 6).replaceAll("-FullSize.jpg", "");
                String title = getTitle(name);
                //String description = getDescription(name);
                System.out.println("name = " + name + ", title = " + title);
            }
        }
    }

}
package fedora.utilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	public static final int BUFFER = 2048;
	
	public static void copy(InputStream source, OutputStream destination) throws IOException {
    	int count;
    	byte data[] = new byte[BUFFER];
    	BufferedOutputStream dest = new BufferedOutputStream(destination, BUFFER);
    	while ((count = source.read(data, 0, BUFFER)) != -1) {
              dest.write(data, 0, count);
    	}
       dest.flush();
       dest.close();
    }
	
	public static void copy(File source, File destination) throws IOException {
		if (!destination.exists() && destination.isDirectory()) {
			destination.mkdirs();
		}
		if (source.isDirectory()) {
			File[] children = source.listFiles();
			for (int i = 0; i < children.length; i++) {
				copy(new File(source, children[i].getName()), 
						new File(destination, children[i].getName()));
			}
		} else {
			InputStream in = new FileInputStream(source);
	    	OutputStream out = new FileOutputStream(destination);
	    	copy(in, out);
	    	out.close();
	    	in.close();
		}
    }
	
	public static boolean deleteDirectory(String directory) {
        boolean result = false;

        if (directory != null) {
            File file = new File(directory);
            if (file.exists() && file.isDirectory()) {
                // 1. delete content of directory:
                File[] files = file.listFiles();
                result = true; //init result flag
                int count = files.length;
                for (int i = 0; i < count; i++) { //for each file:
                    File f = files[i];
                    if (f.isFile()) {
                        result = result && f.delete();
                    } else if (f.isDirectory()) {
                        result = result && deleteDirectory(f.getAbsolutePath());
                    }
                }//next file

                file.delete(); //finally delete (empty) input directory
            }//else: input directory does not exist or is not a directory
        }//else: no input value

        return result;
    }//deleteDirectory()
}

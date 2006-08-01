package fedora.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Zip and GZip utilities.
 * 
 * @author Edwin Shin
 *
 */
public class Zip {
	private static final int BUFFER = 2048;
	
	public static void zip(File destination, File source) throws FileNotFoundException, IOException {
		FileOutputStream dest = new FileOutputStream(destination);
		ZipOutputStream zout = new ZipOutputStream(new BufferedOutputStream(dest));
		zip(null, source, zout);
		zout.close();
	}
	
	public static void unzip(InputStream is, File destDir) throws FileNotFoundException, IOException {		
		BufferedOutputStream dest = null;
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry entry;
        while((entry = zis.getNextEntry()) != null) {
           System.out.println("Extracting: " + entry);
           if (entry.isDirectory()) {
        	   // Otherwise, empty directories do not get created
        	   (new File(destDir, entry.getName())).mkdirs();
           } else {
               File f = new File(destDir, entry.getName());
               f.getParentFile().mkdirs();
               int count;
               byte data[] = new byte[BUFFER];
               // write the files to the disk
               FileOutputStream fos = new FileOutputStream(f);
               dest = new BufferedOutputStream(fos, BUFFER);
               while ((count = zis.read(data, 0, BUFFER)) != -1) {
                  dest.write(data, 0, count);
               }
               dest.flush();
               dest.close();
           }
        }
        zis.close();
    }
	
	public static void gzip() {
		// TODO
	}
	
	public static void gunzip() {
		// TODO
	}
	
	// Convenience methods
	public static void zip(String destination, String source) throws FileNotFoundException, IOException {
		zip(new File(destination), new File(source));
	}
	
	public static void unzip(InputStream is, String destDir) throws FileNotFoundException, IOException {
		unzip(is, new File(destDir));
	}
	
	public static void main(String[] args) {
		// 2 arguments: zipfile, source/destination
		// whether or not the zipfile exists, i.e.:
		// if zipfile exists
		// 		if zipfile extension == zip, then unzip
		//		if zipfile extension == gz, then gunzip
		// else
		// 		if zipfile extension == zip, then zip
		//		if zipfile extension == gz, then gzip
		
		// valid actions are: zip, unzip, gzip, and gunzip
		
		// should consider making source/destination optional (i.e., assume current directory)
		// might consider taking a filefilter
	}
	
	private static void zip(String baseDir, File source, ZipOutputStream zout) throws IOException {
		ZipEntry entry = null;
		if (baseDir == null || baseDir.equals(".") || baseDir.equals("./")) {
			baseDir = "";
		}
		
		if (source.isDirectory()) {
			// If there's a "better" way to indicate a directory, go ahead :)
			// Perhaps at least use File.separator and not "/"
			entry = new ZipEntry(baseDir + source.getName() + "/");
		} else {
			entry = new ZipEntry(baseDir + source.getName());
		}
		zout.putNextEntry(entry);
		System.out.println("Adding " + entry.getName());
		
		if (!source.isDirectory()) {
			byte data[] = new byte[BUFFER];
			FileInputStream fis = new FileInputStream(source);
			BufferedInputStream origin = new BufferedInputStream(fis, BUFFER);
			
			int count;
			while((count = origin.read(data, 0, BUFFER)) != -1) {
			   zout.write(data, 0, count);
			}
			fis.close();
			origin.close();
		} else {
			File files[] = source.listFiles();
			for (int i = 0; i < files.length; i++) {
				zip(entry.getName(), files[i], zout);
			}
		}
	}
	
	protected static boolean deleteDirectory(String directory) {
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

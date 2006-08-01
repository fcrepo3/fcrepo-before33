package fedora.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;

import junit.framework.TestCase;

public class TestZip extends TestCase {
	private final String TMP_DIR = System.getProperty("java.io.tmpdir");
	private final String ZIP_FILE = TMP_DIR + File.separator + "test.zip";
	private final String TEST_DIR = TMP_DIR + File.separator + "test";

	protected void setUp() throws Exception {
        super.setUp();
        Zip.deleteDirectory(TEST_DIR);
        File testDir = new File(TEST_DIR);
		File foo = new File(testDir, "foo");
		File bar = new File(testDir, "bar");
		File baz = new File(bar, "baz");
		File footxt = new File(foo, "foo.txt");
		File bartxt = new File(bar, "bar.txt");
		
		foo.mkdirs();
		baz.mkdirs();
		FileWriter fw = new FileWriter(footxt);
		fw.write("foo");
		fw.flush();
		fw.close();
		
		FileWriter bw = new FileWriter(bartxt);
		bw.write("bar");
		bw.flush();
		bw.close();
    }
	
	protected void tearDown() throws Exception {
        super.tearDown();
        Zip.deleteDirectory(TEST_DIR);
    }
	
	public void testZip() throws Exception {
		Zip.zip(ZIP_FILE, TMP_DIR + File.separator + "test");
	}
	
	public void testUnzip() throws Exception {
		FileInputStream fis = new FileInputStream(ZIP_FILE);
		Zip.unzip(fis, TEST_DIR);
		
		FileReader fr = new FileReader(TEST_DIR + File.separator + "foo" + File.separator + "foo.txt");
		BufferedReader buff = new BufferedReader(fr);
		boolean eof = false;
		while (!eof) {
			String line = buff.readLine();
		    if (line == null)
		    	eof = true;
		    else
		    	assertEquals("foo", line);
		}
	}
}

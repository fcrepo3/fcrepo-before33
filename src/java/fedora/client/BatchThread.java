package fedora.client;
import java.util.Properties;
import java.io.PrintStream;
import javax.swing.JTextArea;
import fedora.client.BatchOutput;
import fedora.client.batch.BatchTool;

/**
 *
 * <p><b>Title:</b> BatchThread.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class BatchThread extends Thread {
	private String leadText = "";
	private Properties properties = null;
	private Properties nullProperties = null;
	private BatchOutput batchOutput = null;
	private JTextArea jTextArea = null;
	private PrintStream originalOut = null;
	private PrintStream originalErr = null;
	private PrintStream printStream = null;

	public BatchThread(BatchOutput batchOutput, JTextArea jTextArea, String leadText) throws Exception {
		this.batchOutput = batchOutput;
		this.jTextArea = jTextArea;
		this.leadText = leadText;
		BatchOutputCatcher batchOutputCatcher = new BatchOutputCatcher(jTextArea);
		//BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(batchOutputCatcher);
		printStream = new PrintStream(batchOutputCatcher, true); //bufferedOutputStream
	}

	public void setProperties (Properties properties) {
		this.properties = properties;
	}

	public void run() {
		try {
			jTextArea.setText(leadText + "\n");
			originalOut = System.out;
			originalErr = System.err;
			System.setOut(printStream);
			System.setErr(printStream);
			batchOutput.setVisible(true);
			BatchTool batchTool = new BatchTool(properties,nullProperties,nullProperties);
			batchTool.prep();
			batchTool.process();
		} catch (Exception e) {
		} finally {
			System.setOut(originalOut);
			originalOut = null;
			System.setErr(originalErr);
			originalErr = null;
			batchOutput.flush2file(); //2003.12.03 niebel -- duplicate output to file
		}
	}
}


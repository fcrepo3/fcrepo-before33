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


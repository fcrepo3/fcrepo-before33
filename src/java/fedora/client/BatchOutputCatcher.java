package fedora.client;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import javax.swing.JTextArea;

public class BatchOutputCatcher extends OutputStream {
        
	private JTextArea jTextArea = null;

	public void write(int b) {
		byte bv = (new Integer(b)).byteValue();
	jTextArea.append(new String(new byte[] {bv} ));
	}
	
	public BatchOutputCatcher(JTextArea jTextArea) {
		this.jTextArea = jTextArea;
	}

}

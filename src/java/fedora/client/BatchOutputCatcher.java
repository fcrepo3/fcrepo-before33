/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client;
import java.io.OutputStream;
import javax.swing.JTextArea;

/**
 *
 * <p><b>Title:</b> BatchOutputCatcher.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
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

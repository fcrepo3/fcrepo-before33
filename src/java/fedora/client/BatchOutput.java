package fedora.client;
import javax.swing.ImageIcon;
//import java.io.BufferedOutputStream;
//import java.io.PrintStream;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

/**
 *
 * <p><b>Title:</b> BatchOutput.java</p>
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
public class BatchOutput extends JInternalFrame {
	JTextArea jTextArea = null;

	public BatchOutput(String title) {
		super(title,
			true, //resizable
			true, //TITLE_PROPERTY
			true, //maximizable
			true);//iconifiable
		setVisible(false);
		jTextArea = new JTextArea();
		jTextArea.setEditable(false);
		JScrollPane jScrollPane = new JScrollPane(jTextArea);
		getContentPane().add(jScrollPane);
		setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/New16.gif")));
		setSize(400,400);
	}

	public JTextArea getJTextArea() {
		return jTextArea;
	}

}

package fedora.client;
import javax.swing.ImageIcon;
//import java.io.BufferedOutputStream;
//import java.io.PrintStream;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

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

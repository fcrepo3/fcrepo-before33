package fedora.client.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import fedora.server.utilities.StreamUtility;

public class ByteArrayInputPanel
        extends InputPanel 
        implements ActionListener {

    private JTextField m_textField;
    private JTextField m_fileField;
    private JFileChooser m_browse;
    private JRadioButton m_fromTextRadioButton;
        
    public ByteArrayInputPanel(boolean primitive) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel fromText=new JPanel();
        fromText.setLayout(new BoxLayout(fromText, BoxLayout.X_AXIS));
        m_fromTextRadioButton=new JRadioButton("Text: ");
        m_fromTextRadioButton.setSelected(true);
        fromText.add(m_fromTextRadioButton);
        m_textField=new JTextField(10);
        fromText.add(m_textField);
        add(fromText);

        JPanel fromFile=new JPanel();
        fromFile.setLayout(new BoxLayout(fromFile, BoxLayout.X_AXIS));
        JRadioButton fromFileRadioButton=new JRadioButton("File: ");
        fromFile.add(fromFileRadioButton);
        m_fileField=new JTextField(10);
        fromFile.add(m_fileField);
        JButton browseButton=new JButton("Browse...");
        browseButton.addActionListener(this);
        fromFile.add(browseButton);
        
        ButtonGroup g=new ButtonGroup();
        g.add(m_fromTextRadioButton);
        g.add(fromFileRadioButton);
        add(fromFile);
        
        m_browse=new JFileChooser();
        
    }
    
    public void actionPerformed(ActionEvent e) {
        int returnVal = m_browse.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = m_browse.getSelectedFile();
            m_fileField.setText(file.getAbsolutePath());
        }
    }
    
    public Object getValue() {
        if (m_fromTextRadioButton.isSelected()) {
            return m_textField.getText().getBytes();
        } else {
            File f=new File(m_fileField.getText());
            if (!f.exists() || f.isDirectory()) {
                System.out.println("returning null..file doesnt exist");
                return null;
            }
            try {
                ByteArrayOutputStream out=new ByteArrayOutputStream();
                FileInputStream in=new FileInputStream(f);
                StreamUtility.pipeStream(in, out, 4096);
                return out.toByteArray();
            } catch (IOException ioe) {
                System.out.println("ioexecption getting filestream: " + ioe.getMessage());
                return null;
            }
        }
    }

}
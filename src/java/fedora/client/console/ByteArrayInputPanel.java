package fedora.client.console;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class ByteArrayInputPanel
        extends InputPanel {

    private JTextField m_textField;
        
    public ByteArrayInputPanel(boolean primitive) {
        m_textField=new JTextField(10);
        add(m_textField);
    }
    
    public Object getValue() {
        return m_textField.getText().getBytes();
    }

}
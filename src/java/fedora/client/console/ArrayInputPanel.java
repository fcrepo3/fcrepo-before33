package fedora.client.console;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.ArrayList;

public class ArrayInputPanel
        extends InputPanel {
        
    private ArrayList m_inputPanels;

    public ArrayInputPanel(Class cl) {
        m_inputPanels=new ArrayList();
        add(new JLabel("Array handler not implemented, will be null."));
    }
    
    public Object getValue() {
        Object[] out=new Object[m_inputPanels.size()];
        
        return out;
    }

}
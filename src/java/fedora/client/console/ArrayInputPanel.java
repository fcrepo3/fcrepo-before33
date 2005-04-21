package fedora.client.console;

import javax.swing.JLabel;
import java.util.ArrayList;

/**
 *
 * <p><b>Title:</b> ArrayInputPanel.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ArrayInputPanel
        extends InputPanel {

    private ArrayList m_inputPanels;

    public ArrayInputPanel(Class cl) {
        m_inputPanels=new ArrayList();
        add(new JLabel("Array handler not implemented, will be null."));
    }

    public Object getValue() {
        Object[] out = null;
        if (m_inputPanels.size() > 0)
        {
          out=new Object[m_inputPanels.size()];
        }
        return out;
    }

}
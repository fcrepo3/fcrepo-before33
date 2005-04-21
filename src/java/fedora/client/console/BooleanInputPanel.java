package fedora.client.console;

import javax.swing.JCheckBox;

/**
 *
 * <p><b>Title:</b> BooleanInputPanel.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class BooleanInputPanel
        extends InputPanel {

    private JCheckBox m_checkBox;

    public BooleanInputPanel(boolean primitive) {
        m_checkBox=new JCheckBox();
        m_checkBox.setSelected(false);
        add(m_checkBox);
    }

    public Object getValue() {
        return new Boolean(m_checkBox.isSelected());
    }

}
/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

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

	private static final long serialVersionUID = 1L;
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
package fedora.client.console;

import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * <p><b>Title:</b> StringInputPanel.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class StringInputPanel
        extends InputPanel {

    private JRadioButton m_nullRadioButton;
    private JTextField m_textField;

    public StringInputPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel nullPanel=new JPanel();
        nullPanel.setLayout(new BorderLayout());
        m_nullRadioButton=new JRadioButton("Use null");
        nullPanel.add(m_nullRadioButton, BorderLayout.WEST);
        add(nullPanel);

        JPanel textPanel=new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
        JRadioButton textRadioButton=new JRadioButton("Use text: ");
        textRadioButton.setSelected(true);
        textPanel.add(textRadioButton);
        m_textField=new JTextField(10);
        textPanel.add(m_textField);
        add(textPanel);

        ButtonGroup g=new ButtonGroup();
        g.add(m_nullRadioButton);
        g.add(textRadioButton);

    }

    public Object getValue() {
        if (m_nullRadioButton.isSelected()) {
            return null;
        } else {
            return m_textField.getText();
        }
    }

}
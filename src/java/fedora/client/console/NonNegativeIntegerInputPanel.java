package fedora.client.console;

import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.axis.types.NonNegativeInteger;

/**
 *
 * <p><b>Title:</b> NonNegativeIntegerInputPanel.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornel.edu
 * @author rlw@virginia.edu (adapted from StringInputPanel)
 * @version $Id$
 */
public class NonNegativeIntegerInputPanel
        extends InputPanel {

    private JRadioButton m_nullRadioButton;
    private JTextField m_textField;

    public NonNegativeIntegerInputPanel() {
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
          return new NonNegativeInteger(m_textField.getText());
        }
    }

}
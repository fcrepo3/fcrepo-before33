/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.utilities.policyEditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class EditUserClassDialog
        extends JDialog
        implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JPanel paramPanel = null;

    private JButton editClass = null;

    private GroupRuleInfo template = null;

    private GroupRuleInfo group = null;

    private boolean changed = false;

    /**
     * @param owner
     * @param title
     * @throws java.awt.HeadlessException
     */
    public EditUserClassDialog(Frame owner,
                               GroupRuleInfo template,
                               GroupRuleInfo group,
                               String title)
            throws HeadlessException {
        super(owner, title, true);
        this.template = template;
        this.group = group;
        getContentPane().setLayout(new BorderLayout());
        JPanel botPanel = new JPanel();
        paramPanel = new JPanel() {

            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(500, 150);
            }
        };
        paramPanel.setLayout(new ParagraphLayout());
        paramPanel
                .setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10),
                                              new TitledBorder("Enter Values For Parameters:")));
        getContentPane().add(paramPanel, BorderLayout.CENTER);
        botPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        botPanel.add(editClass = makeButton("Change Parameters", true));
        botPanel.add(makeButton("Cancel", true));
        getContentPane().add(botPanel, BorderLayout.SOUTH);
        for (int i = 0; i < template.getNumParms(); i++) {
            JLabel label = new JLabel(template.getParmValue(i));
            label.setName(template.getParmName(i));
            paramPanel.add(label, ParagraphLayout.NEW_PARAGRAPH);
            JTextField field = new JTextField(35);
            field.setName(template.getParmName(i));
            field.setText(group.getParmValue(i));
            field.addActionListener(this);
            paramPanel.add(field);
        }
        pack();
        setVisible(true);
    }

    private JButton makeButton(String label, boolean enabled) {
        JButton button = new JButton(label);
        button.addActionListener(this);
        button.setEnabled(enabled);
        return button;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Cancel")) {
            setVisible(false);
            dispose();
        } else if (e.getActionCommand().equals("Change Parameters")) {
            String parmString = "";
            for (int i = 0; i < paramPanel.getComponentCount(); i++) {
                if (paramPanel.getComponent(i) instanceof JTextField) {
                    JTextField field = (JTextField) paramPanel.getComponent(i);
                    parmString =
                            parmString + (parmString.length() > 0 ? ";" : "")
                                    + field.getName() + "=" + field.getText();
                }
            }

            group.rebuildFromTemplate(template, parmString);
            changed = true;
        }
        setVisible(false);
        dispose();

    }

    public boolean isChanged() {
        return changed;
    }

}

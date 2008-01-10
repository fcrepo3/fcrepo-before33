/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.bmech;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;

import fedora.client.Administrator;

/**
 * @author Sandy Payette
 */
public class DCElementDialog
        extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField dcName;

    private JTextField dcValue;

    private final GeneralPane parent;

    public DCElementDialog(GeneralPane parent, String title, boolean modal) {
        super(JOptionPane.getFrameForComponent(Administrator.getDesktop()),
              "Add DC Element",
              true);
        setLocationRelativeTo(parent);
        this.parent = parent;
        setTitle(title);
        setModal(modal);
        setSize(300, 200);
        render();
        setVisible(true);

    }

    public DCElementDialog(GeneralPane parent,
                           String title,
                           boolean modal,
                           String txt_dcElement,
                           String txt_dcValue) {
        super(JOptionPane.getFrameForComponent(Administrator.getDesktop()),
              "Edit DC Element",
              true);
        setLocationRelativeTo(parent);
        this.parent = parent;
        setTitle(title);
        setModal(modal);
        setSize(300, 200);
        render();
        dcName.setText(txt_dcElement);
        dcValue.setText(txt_dcValue);
        setVisible(true);
    }

    private void render() {
        getContentPane().setLayout(new BorderLayout());

        // Text Fields Panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(3, 2));
        textPanel.add(new JLabel("DC Element Name: "));
        textPanel.add(dcName = new JTextField());
        textPanel.add(new JLabel("Value: "));
        textPanel.add(dcValue = new JTextField());
        textPanel.setName("text");

        // Dialog Buttons Panel
        JButton done = new JButton("OK");
        done.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveFields();
            }
        });
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });
        JPanel mainButtonPanel = new JPanel();
        mainButtonPanel.add(done);
        mainButtonPanel.add(cancel);

        getContentPane().add(textPanel, BorderLayout.CENTER);
        getContentPane().add(mainButtonPanel, BorderLayout.SOUTH);
        JRootPane root = getRootPane();
        root.setDefaultButton(done);
        return;
    }

    private void saveFields() {
        if (validDC()) {
            parent.setDCElement(dcName.getText().trim(), dcValue.getText()
                    .trim());
            setVisible(false);
            dispose();
        }
    }

    private void cancel() {
        setVisible(false);
        dispose();
    }

    private boolean validDC() {
        if (dcName.getText() == null || dcName.getText().trim().equals("")) {
            parent.assertInvalidDCMsg("You must enter a DC element name");
            return false;
        } else if (!dcName.getText().equalsIgnoreCase("title")
                && !dcName.getText().equalsIgnoreCase("creator")
                && !dcName.getText().equalsIgnoreCase("subject")
                && !dcName.getText().equalsIgnoreCase("publisher")
                && !dcName.getText().equalsIgnoreCase("contributor")
                && !dcName.getText().equalsIgnoreCase("date")
                && !dcName.getText().equalsIgnoreCase("format")
                && !dcName.getText().equalsIgnoreCase("identifier")
                && !dcName.getText().equalsIgnoreCase("source")
                && !dcName.getText().equalsIgnoreCase("language")
                && !dcName.getText().equalsIgnoreCase("relation")
                && !dcName.getText().equalsIgnoreCase("coverage")
                && !dcName.getText().equalsIgnoreCase("rights")) {
            parent
                    .assertInvalidDCMsg("You have entered an invalid DC element name: "
                            + dcName.getText());
            return false;
        } else if (dcValue.getText() == null
                || dcValue.getText().trim().equals("")) {
            parent.assertInvalidDCMsg("You must enter a DC element value");
            return false;
        }
        return true;
    }
}

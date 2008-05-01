/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.deployment;

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
public class MethodDialog
        extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField methodName;

    private JTextField methodDescription;

    private final MethodsPane parent;

    public MethodDialog(MethodsPane parent, String title, boolean modal) {
        super(JOptionPane.getFrameForComponent(Administrator.getDesktop()),
              "Add Method",
              true);
        setLocationRelativeTo(parent);
        this.parent = parent;
        setTitle(title);
        setModal(modal);
        setSize(300, 200);
        render();
        setVisible(true);

    }

    public MethodDialog(MethodsPane parent,
                        String title,
                        boolean modal,
                        String txt_methodName,
                        String txt_methodDesc) {
        //super();
        super(JOptionPane.getFrameForComponent(Administrator.getDesktop()),
              "Edit Method",
              true);
        setLocationRelativeTo(parent);
        this.parent = parent;
        setTitle(title);
        setModal(modal);
        setSize(300, 200);
        render();
        methodName.setText(txt_methodName);
        methodDescription.setText(txt_methodDesc);
        setVisible(true);
    }

    private void render() {
        getContentPane().setLayout(new BorderLayout());

        // Text Fields Panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(3, 2));
        textPanel.add(new JLabel("Method Name: "));
        textPanel.add(methodName = new JTextField());
        textPanel.add(new JLabel("Method Description: "));
        textPanel.add(methodDescription = new JTextField());
        textPanel.setName("text");

        // Dialog Buttons Panel
        JButton done = new JButton("OK");
        done.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveMethodFields();
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

    private void saveMethodFields() {
        if (validMethod()) {
            try {
                parent.setSDepMethod(methodName.getText().trim(),
                                      methodDescription.getText().trim());
                setVisible(false);
                dispose();
            } catch (DeploymentBuilderException e) {
                parent.assertMethodExistsMsg("The method name already exists.");
            }
        }
    }

    private void cancel() {
        setVisible(false);
        dispose();
    }

    private boolean validMethod() {
        if (methodName.getText() == null
                || methodName.getText().trim().equals("")) {
            parent.assertNoMethodMsg("You must enter a method name");
            return false;
        } else if (methodDescription.getText() == null
                || methodDescription.getText().trim().equals("")) {
            parent.assertNoMethodMsg("You must enter a method description");
            return false;
        }
        return true;
    }
}

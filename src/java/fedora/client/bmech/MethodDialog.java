package fedora.client.bmech;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import fedora.client.Administrator;

/**
 *
 * <p><b>Title:</b> MethodDialog.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class MethodDialog extends JDialog {

    private JTextField methodName;
    private JTextField methodDescription;
    private MethodsPane parent;

    public MethodDialog(MethodsPane parent, String title, boolean modal)
    {
		super(JOptionPane.getFrameForComponent(Administrator.getDesktop()), "Add Method", true);
		setLocationRelativeTo(parent);
        this.parent = parent;
        setTitle(title);
        setModal(modal);
        setSize(300, 200);
        render();
        setVisible(true);

    }
    public MethodDialog(MethodsPane parent, String title, boolean modal,
      String txt_methodName, String txt_methodDesc)
    {
        //super();
		super(JOptionPane.getFrameForComponent(Administrator.getDesktop()), "Edit Method", true);
		setLocationRelativeTo(parent);
        this.parent = parent;
        setTitle(title);
        setModal(modal);
        setSize(300, 200);
        render();
        methodName.setText(txt_methodName);
        methodDescription.setText(txt_methodDesc);
        this.setVisible(true);
    }

    private void render()
    {
      getContentPane().setLayout(new BorderLayout());

      // Text Fields Panel
      JPanel textPanel = new JPanel();
      textPanel.setLayout(new GridLayout(3,2));
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
      } );
      JButton cancel = new JButton("Cancel");
      cancel.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          cancel();
        }
      } );
      JPanel mainButtonPanel = new JPanel();
      mainButtonPanel.add(done);
      mainButtonPanel.add(cancel);

      getContentPane().add(textPanel, BorderLayout.CENTER);
      getContentPane().add(mainButtonPanel, BorderLayout.SOUTH);
	  JRootPane root = getRootPane();
	  root.setDefaultButton(done);
      return;
    }

    private void saveMethodFields()
    {
      if (validMethod())
      {
        try
        {
          parent.setBMechMethod(
            methodName.getText().trim(), methodDescription.getText().trim());
          setVisible(false);
          dispose();
        }
        catch (BMechBuilderException e)
        {
          parent.assertMethodExistsMsg("The method name already exists.");
        }
      }
    }

    private void cancel()
    {
      setVisible(false);
      dispose();
    }

    private boolean validMethod()
    {
      if (methodName.getText() == null || methodName.getText().trim().equals(""))
      {
        parent.assertNoMethodMsg("You must enter a method name");
        return false;
      }
      else if (methodDescription.getText() == null ||
               methodDescription.getText().trim().equals(""))
      {
        parent.assertNoMethodMsg("You must enter a method description");
        return false;
      }
      return true;
    }
}
package fedora.client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import fedora.client.Administrator;

/**
 * Launch a dialog for selecting which object type(s) the user is interested in:
 * bdefs, bmechs, data objects (any combination).  Result will come
 * back as string consisting of combination of "D", "M", and "O" characters,
 * respectively, if selected.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */

public class FTypeDialog
        extends JDialog {
        
    private String selections;
    
    private JCheckBox dButton;
    private JCheckBox mButton;
    private JCheckBox oButton;

    public FTypeDialog() {
        super(JOptionPane.getFrameForComponent(Administrator.getDesktop()), "Select Object Type(s)", true);

        JPanel inputPane=new JPanel();
        inputPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6, 6, 6, 6),
                    BorderFactory.createEtchedBorder()
                ),
                BorderFactory.createEmptyBorder(6,6,6,6)
                ));
                
        inputPane.setLayout(new GridLayout(0, 1));
        dButton=new JCheckBox("Behavior Definitions");
        dButton.setMnemonic(KeyEvent.VK_D);
        mButton=new JCheckBox("Behavior Mechanisms");
        mButton.setMnemonic(KeyEvent.VK_M);
        oButton=new JCheckBox("Data Objects");
        oButton.setMnemonic(KeyEvent.VK_O);
        oButton.setSelected(true);
        inputPane.add(dButton);
        inputPane.add(mButton);
        inputPane.add(oButton);

        JButton okButton=new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                selections="";
                if (dButton.isSelected()) {
                    selections+="D";
                }
                if (mButton.isSelected()) {
                    selections+="M";
                }
                if (oButton.isSelected()) {
                    selections+="O";
                }
                if (selections.equals("")) selections=null; 
                dispose();
            }
        });
        okButton.setText("OK");
        JButton cancelButton=new JButton(new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                dispose();
            }
        });
        cancelButton.setText("Cancel");
        JPanel buttonPane=new JPanel();
        buttonPane.add(okButton);
        buttonPane.add(cancelButton);
        Container contentPane=getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(inputPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);
        
        pack();
        setLocation(Administrator.INSTANCE.getCenteredPos(getWidth(), getHeight()));
        setVisible(true);
    }

    // null means nothing selected or selection canceled
    public String getResult() {
        return selections;
    }

}
package fedora.client.objecteditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import fedora.client.Administrator;

/**
 * An abstract JPanel for panes that support Save and Undo operations, which
 * also includes some utility methods to make constructing the UI easier
 * for implementers.
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
public abstract class EditingPane
        extends JPanel
        implements PotentiallyDirty {

    private JButton m_saveButton;
    private JButton m_undoButton;

    /** 
     * Implementers can register this to listen to any events that resulted
     * in a change, and it will automatically call updateButtonVisibility.
     */
    public DataChangeListener dataChangeListener;

    /**
     * The pane that implementers set the layout of and add components to.  
     * This pane will already have a standard border.
     */
    public JPanel mainPane;

    private TabDrawer m_td;
    private String m_itemId;
    private ObjectEditorFrame m_owner;
    private boolean m_isValid;

    /**
     * Build the pane.
     */
    public EditingPane(ObjectEditorFrame owner, TabDrawer td, String itemId)
            throws Exception {

        m_owner=owner;
        m_td=td;
        m_itemId=itemId;
        m_isValid=true;

        dataChangeListener=new DataChangeListener(this);

        // this(saveUndoPane, mainPane)

            // SOUTH: saveUndoPane(saveButton, undoButton)

                m_saveButton=new JButton(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        String logMessage=
                            JOptionPane.showInputDialog("Enter a log message.");
                        if (logMessage!=null) {
                            try {
                                saveChanges(logMessage);
                                changesSaved();
                            } catch (Exception ex) {
                                String msg=ex.getMessage();
                                if (msg==null) msg=ex.getClass().getName();
                                JOptionPane.showMessageDialog(
                                        Administrator.getDesktop(), 
                                        msg, "Save Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            updateButtonVisibility();
                        }
                    }
                });
                m_saveButton.setText("Save Changes...");
                Administrator.constrainHeight(m_saveButton);
                m_saveButton.setEnabled(false);
                m_undoButton=new JButton(new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        undoChanges();
                        updateButtonVisibility();
                    }
                });
                m_undoButton.setText("Undo Changes");
                Administrator.constrainHeight(m_undoButton);
                m_undoButton.setEnabled(false);

            JPanel saveUndoPane=new JPanel();
            saveUndoPane.setLayout(new FlowLayout());
            saveUndoPane.setBorder(BorderFactory.createEmptyBorder(4,0,0,0));
            saveUndoPane.add(m_saveButton);
            saveUndoPane.add(m_undoButton);

            // NORTH: mainPane(implementers will add to)

            mainPane=new JPanel();
            mainPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(4,4,4,4)
                    ));

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        add(mainPane, BorderLayout.CENTER);
        add(saveUndoPane, BorderLayout.SOUTH);

    }

    /**
     * Disables save and undo buttons if any changes have occurred,
     * enables them otherwise.
     *
     * This is called whenever dataChangeListener recieves an event,
     * and should be called manually at the end of save() and undo().
     */
    public void updateButtonVisibility() {
        if (isDirty()) {
            if (m_isValid) m_saveButton.setEnabled(true);
            m_undoButton.setEnabled(true);
            if (m_td!=null) m_td.setDirty(m_itemId, true);
            m_owner.indicateDirtiness();
        } else {
            m_saveButton.setEnabled(false);
            m_undoButton.setEnabled(false);
            if (m_td!=null) m_td.setDirty(m_itemId, false);
            m_owner.indicateDirtiness();
        }
        if (!m_isValid) m_saveButton.setEnabled(false);
    }

    /**
     * Tell whether the content being edited is valid or invalid.
     * During future change events, the save button will never be shown while it is set to invalid.
     * Before this is ever called on an EditingPane, the content is assumed
     * to be valid.
     */
    public void setValid(boolean isValid) {
        m_isValid=isValid;
    }

    /**
     * Commit changes to the server.
     */
    public abstract void saveChanges(String logMessage) throws Exception;

    /**
     * Called when changes to the server succeeded.
     * This method can do anything, but it should at least ensure that the
     * model and view are in-sync with each other (accurately reflecting the
     * current state of the server).
     */
    public abstract void changesSaved() throws Exception;

    /**
     * Revert to original values, then call updateButtonVisibility.
     */
    public abstract void undoChanges();

    public void addLabelValueRows(JLabel[] labels, JComponent[] values,
            GridBagLayout gridBag, Container container) {
        GridBagConstraints c=new GridBagConstraints();
        c.insets=new Insets(0, 4, 4, 4);
        for (int i=0; i<labels.length; i++) {
            c.anchor=GridBagConstraints.WEST;
            c.gridwidth=GridBagConstraints.RELATIVE; //next-to-last
            c.fill=GridBagConstraints.NONE;      //reset to default
            c.weightx=0.0;                       //reset to default
            gridBag.setConstraints(labels[i], c);
            container.add(labels[i]);

            c.gridwidth=GridBagConstraints.REMAINDER;     //end row
            if ((values[i] instanceof JComboBox) || (values[i] instanceof JButton)) {
                c.anchor=GridBagConstraints.WEST;
            } else {
                c.anchor=GridBagConstraints.EAST;
                c.fill=GridBagConstraints.HORIZONTAL;
            }
            c.weightx=1.0;
            gridBag.setConstraints(values[i], c);
            container.add(values[i]);
        }

    }

    /**
     * Updates the EditingPane's button visibility upon recieving any event.
     */
    public class DataChangeListener
            implements ActionListener, DocumentListener {

        private EditingPane m_editingPane;

        public DataChangeListener(EditingPane editingPane) {
            m_editingPane=editingPane;
        }

        public void actionPerformed(ActionEvent e) {
            dataChanged();
        }

        public void changedUpdate(DocumentEvent e) {
            dataChanged();
        }

        public void insertUpdate(DocumentEvent e) {
            dataChanged();
        }

        public void removeUpdate(DocumentEvent e) {
            dataChanged();
        }

        public void dataChanged() {
            m_editingPane.updateButtonVisibility();
        }
    }

}
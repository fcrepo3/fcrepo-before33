package fedora.client.objecteditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import fedora.client.Administrator;
import fedora.server.types.gen.Disseminator;

/**
 * Shows a tabbed pane, one for each disseminator in the object.
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
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DisseminatorsPane
        extends JPanel
        implements PotentiallyDirty, TabDrawer {

    static ImageIcon newIcon=new ImageIcon(Administrator.cl.getResource("images/standard/general/New16.gif"));

    private String m_pid;
    private JTabbedPane m_tabbedPane;
    private ObjectEditorFrame m_owner;
    private DisseminatorPane[] m_disseminatorPanes;

    /**
     * Build the pane.
     */
    public DisseminatorsPane(ObjectEditorFrame owner, String pid)
            throws Exception {
        m_pid=pid;
        m_owner=owner;

        m_tabbedPane=new JTabbedPane(SwingConstants.LEFT);
        Disseminator[] currentVersions=Administrator.APIM.
                    getDisseminators(pid, null, null);
        m_disseminatorPanes=new DisseminatorPane[currentVersions.length];
        for (int i=0; i<currentVersions.length; i++) {
            Disseminator[] versions=Administrator.APIM.getDisseminatorHistory(
                    pid, currentVersions[i].getID());
            m_disseminatorPanes[i]=new DisseminatorPane(owner, m_pid, versions, this);
            m_tabbedPane.add(currentVersions[i].getID(), m_disseminatorPanes[i]);
            m_tabbedPane.setToolTipTextAt(i, currentVersions[i].getBDefPID()
                    + " - " + currentVersions[i].getLabel());
            colorTabForState(currentVersions[i].getID(), currentVersions[i].getState());
        }
        m_tabbedPane.add("New...", new JLabel("not implemented"));

        setLayout(new BorderLayout());
        add(m_tabbedPane, BorderLayout.CENTER);
        doNew(false);
    }

    public void setDirty(String id, boolean isDirty) {
        int i=getTabIndex(id);
        if (isDirty) {
            System.out.println("Setting " + id + " tab to '" + id + "*'");
            m_tabbedPane.setTitleAt(i, id + "*");
        } else {
            System.out.println("Setting " + id + " tab to '" + id + "'");
            m_tabbedPane.setTitleAt(i, id);
        }
    }

    protected void refresh(String dissID) {
        int i=getTabIndex(dissID);
        try {
            Disseminator[] versions=Administrator.APIM.getDisseminatorHistory(m_pid, dissID);
            DisseminatorPane replacement=new DisseminatorPane(m_owner, m_pid, versions, this);
            m_disseminatorPanes[i]=replacement;
            m_tabbedPane.setComponentAt(i, replacement);
            m_tabbedPane.setToolTipTextAt(i, versions[0].getBDefPID()
                    + " - " + versions[0].getLabel());
            colorTabForState(dissID, versions[0].getState());
            setDirty(dissID, false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(Administrator.getDesktop(),
                    e.getMessage() + "\nTry re-opening the object viewer.", 
                    "Error while refreshing",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void colorTabForState(String id, String s) {
        int i=getTabIndex(id);
        if (s.equals("I")) {
            m_tabbedPane.setBackgroundAt(i, Administrator.INACTIVE_COLOR);
        } else if (s.equals("D")) {
            m_tabbedPane.setBackgroundAt(i, Administrator.DELETED_COLOR);
        } else {
            m_tabbedPane.setBackgroundAt(i, Administrator.ACTIVE_COLOR);
        }
    }

    private int getTabIndex(String id) {
        int i=m_tabbedPane.indexOfTab(id);
        if (i!=-1) return i;
        return m_tabbedPane.indexOfTab(id+"*");
    }

    protected void remove(String dissID) {
        int i=getTabIndex(dissID);
        m_tabbedPane.remove(i);
        // also remove it from the array
        DisseminatorPane[] newArray=new DisseminatorPane[m_disseminatorPanes.length-1];
        for (int x=0; x<m_disseminatorPanes.length; x++) {
            if (x<i) {
                newArray[x]=m_disseminatorPanes[x];
            } else if (x>i) {
                newArray[x-1]=m_disseminatorPanes[x-1]; 
            }
        }
        m_disseminatorPanes=newArray;
        // then make sure dirtiness indicators are corrent
        m_owner.indicateDirtiness();
    }

    public boolean isDirty() {
        for (int i=0; i<m_disseminatorPanes.length; i++) {
            if (m_disseminatorPanes[i].isDirty()) return true;
        }
        return false;
    }

    /**
     * Set the content of the "New..." JPanel to a fresh new disseminator
     * entry panel, and switch to it, if needed.
     */
    public void doNew(boolean makeSelected) {
        int i=getTabIndex("New...");
        m_tabbedPane.setComponentAt(i, new NewDisseminatorPane());
        m_tabbedPane.setToolTipTextAt(i, "Add a new disseminator to this object");
        m_tabbedPane.setIconAt(i, newIcon);
        m_tabbedPane.setBackgroundAt(i, Administrator.DEFAULT_COLOR);
        if (makeSelected) {
            m_tabbedPane.setSelectedIndex(i);
        }
    }

    public class NewDisseminatorPane 
            extends JPanel 
            implements ActionListener {

        public NewDisseminatorPane() {
            JPanel entryPane=new JPanel(new BorderLayout());
            entryPane.add(new JLabel("Not implemented"), BorderLayout.CENTER);
            
            JButton saveButton=new JButton("Save");
            Administrator.constrainHeight(saveButton);
            saveButton.setActionCommand("Save");
            saveButton.addActionListener(this);

            JPanel buttonPane=new JPanel();
            buttonPane.setLayout(new FlowLayout());
            buttonPane.add(saveButton);

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
            add(entryPane, BorderLayout.CENTER);
            add(buttonPane, BorderLayout.SOUTH);
        }

        public void actionPerformed(ActionEvent evt) {
            String cmd=evt.getActionCommand();
            if (cmd.equals("Save")) {
            }
        }

    }

}
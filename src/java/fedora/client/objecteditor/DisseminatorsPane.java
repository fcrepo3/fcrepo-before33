package fedora.client.objecteditor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import fedora.client.Administrator;
import fedora.client.actions.ViewObject;

import fedora.server.types.gen.DatastreamBindingMap;
import fedora.server.types.gen.Disseminator;

/**
 * Shows a tabbed pane, one for each disseminator in the object.
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
    private Map m_currentVersionMap;
	public Map allBDefLabels;

    /**
     * Build the pane.
     */
    public DisseminatorsPane(ObjectEditorFrame owner, String pid)
            throws Exception {
        m_pid=pid;
        m_owner=owner;
        m_currentVersionMap=new HashMap();
        m_tabbedPane=new JTabbedPane(SwingConstants.LEFT);
		allBDefLabels=Util.getBDefLabelMap();
		
        Disseminator[] currentVersions=Administrator.APIM.
                    getDisseminators(pid, null, null);
        m_disseminatorPanes=new DisseminatorPane[currentVersions.length];
        for (int i=0; i<currentVersions.length; i++) {
            Disseminator[] versions=Administrator.APIM.getDisseminatorHistory(
                    pid, currentVersions[i].getID());
            m_currentVersionMap.put(currentVersions[i].getBDefPID(), currentVersions[i]);
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

    // unlike the datastream currentVersionMap, this one is keyed by bdefpid
    public Map getCurrentVersionMap() {
        return m_currentVersionMap;
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
            m_currentVersionMap.put(versions[0].getBDefPID(), versions[0]);
            DisseminatorPane replacement=new DisseminatorPane(m_owner, m_pid, versions, this);
            m_disseminatorPanes[i]=replacement;
            m_tabbedPane.setComponentAt(i, replacement);
            m_tabbedPane.setToolTipTextAt(i, versions[0].getBDefPID()
                    + " - " + versions[0].getLabel());
            colorTabForState(dissID, versions[0].getState());
            setDirty(dissID, false);
        } catch (Exception e) {
        	Administrator.showErrorDialog(Administrator.getDesktop(), "Error while refreshing", 
        			e.getMessage() + "\nTry re-opening the object viewer.", e);
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

    /**
     * Add a new tab with a new disseminator
     */
    protected void addDisseminatorTab(String dissID) throws Exception {
        DisseminatorPane[] newArray=new DisseminatorPane[m_disseminatorPanes.length+1];
        for (int i=0; i<m_disseminatorPanes.length; i++) {
            newArray[i]=m_disseminatorPanes[i];
        }
        Disseminator[] versions=Administrator.APIM.getDisseminatorHistory(m_pid, dissID);
        m_currentVersionMap.put(versions[0].getBDefPID(), versions[0]);
        newArray[m_disseminatorPanes.length]=new DisseminatorPane(m_owner,
                        m_pid, versions, this);
        // swap the arrays
        m_disseminatorPanes=newArray;
        int newIndex=getTabIndex("New...");
        m_tabbedPane.add(m_disseminatorPanes[m_disseminatorPanes.length-1], newIndex);
        m_tabbedPane.setTitleAt(newIndex, dissID);
        m_tabbedPane.setToolTipTextAt(newIndex, versions[0].getBDefPID() 
                + " - " + versions[0].getLabel());

        colorTabForState(dissID, versions[0].getState());
        m_tabbedPane.setSelectedIndex(newIndex);
        doNew(false);
    }

    protected void remove(String dissID) {
        int i=getTabIndex(dissID);
        m_tabbedPane.remove(i);
        String bDefPID=null;
        Iterator iter=m_currentVersionMap.values().iterator();
        while (iter.hasNext()) {
            Disseminator diss=(Disseminator) iter.next();
            if (diss.getID().equals(dissID)) {
                bDefPID=diss.getBDefPID();
            }
        }
        m_currentVersionMap.remove(bDefPID);
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
    public void doNew(boolean makeSelected) throws IOException {
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
            implements ValidityListener {

        private JComboBox m_stateComboBox;
        private String m_initialState;
        private JTextField m_labelTextField;
        private JComboBox m_behaviorComboBox;
        private BehaviorDescriptionPanel m_behaviorDescriptionPane;
        private JButton m_openBDefButton;
        private JButton m_saveButton;

        private String m_bDefPID;
        private String m_bDefLabel;

        public NewDisseminatorPane() throws IOException {

            // helps with spacing
            Dimension labelDims=new JLabel("Mechansim").getPreferredSize();

            JLabel behaviorLabel=new JLabel("Behavior");
            behaviorLabel.setMinimumSize(labelDims);
            behaviorLabel.setPreferredSize(labelDims);
            // static labels on the left side
            JLabel[] left=new JLabel[] {new JLabel("State"),
                                        new JLabel("Label"),
                                        behaviorLabel};

            //
            // State
            //
            m_stateComboBox=new JComboBox(new String[] {"Active",
                                                        "Inactive",
                                                        "Deleted"});
            m_initialState="A";
            m_stateComboBox.setBackground(Administrator.ACTIVE_COLOR);
            Administrator.constrainHeight(m_stateComboBox);
            m_stateComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    m_initialState=((String) m_stateComboBox.getSelectedItem()).substring(0,1);
                    if (m_initialState.equals("A")) {
                        m_stateComboBox.setBackground(Administrator.ACTIVE_COLOR);
                    } else if (m_initialState.equals("I")) {
                        m_stateComboBox.setBackground(Administrator.INACTIVE_COLOR);
                    } else if (m_initialState.equals("D")) {
                        m_stateComboBox.setBackground(Administrator.DELETED_COLOR);
                    }
                }
            });

            //
            // Label
            //
            m_labelTextField=new JTextField("Enter a label here.");

            //
            // Behaviors
            //
            // NORTH: "Defined by..." JComboBox of bDefs ("pid - label") [OPEN]
            // SOUTH: BehaviorDescriptionPanel describing selected bDef
            final MechanismInputPanel mechInputPanel=
                    new MechanismInputPanel(null, 
                                            this, 
                                            labelDims, 
                                            m_owner.getCurrentDatastreamVersions(),
                                            m_owner,
                                            this);
            //
            // build dropdown of possible behaviors
            //
            boolean noDisseminators=(m_currentVersionMap.keySet().size()==0);
            Map bDefLabels=new HashMap();
            Iterator iter=allBDefLabels.keySet().iterator();
            while (iter.hasNext()) {
                String pid=(String) iter.next();
                if (!m_currentVersionMap.containsKey(pid)) {
                    bDefLabels.put(pid, (String) allBDefLabels.get(pid));
                }
            }
            // set up the combobox 
            String[] bDefOptions=new String[bDefLabels.keySet().size() + 1];
            if (bDefOptions.length==1) {
                if (noDisseminators) {
                    bDefOptions[0]="No behavior definitions in repository!";
                } else {
                    bDefOptions[0]="No additional behavior definitions in repository!";
                }
            } else {
                bDefOptions[0]="[Select a Behavior Definition]";
            }
            iter=bDefLabels.keySet().iterator();
            int i=1;
            while (iter.hasNext()) {
                String pid=(String) iter.next();
                String label=(String) bDefLabels.get(pid);
                bDefOptions[i++]=pid + " - " + label;
            }
            m_behaviorComboBox=new JComboBox(bDefOptions);
            m_behaviorComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        String[] parts=
                                ((String) m_behaviorComboBox.getSelectedItem()).
                                        split(" - ");
                        if (parts.length==1) {
                            m_bDefPID=null;
                            m_bDefLabel=null;
                            m_openBDefButton.setEnabled(false);
                        } else {
                            m_bDefPID=parts[0];
                            m_bDefLabel=parts[1];
                            m_openBDefButton.setEnabled(true);
                        }
                        mechInputPanel.setBDef(m_bDefPID);
                        m_behaviorDescriptionPane.setBDef(m_bDefPID);
                    } catch (Exception e) {
                    	Administrator.showErrorDialog(Administrator.getDesktop(), "Error getting behavior definition", 
                    			e.getMessage(), e);
                    }
                }
            });
            Administrator.constrainHeight(m_behaviorComboBox);
            BorderLayout bDefSelectionRowLayout=new BorderLayout();
            bDefSelectionRowLayout.setHgap(4);
            JPanel bDefSelectionRow=new JPanel(bDefSelectionRowLayout);
            JTextArea definedBy=new JTextArea("defined by...");
            definedBy.setLineWrap(false);
            definedBy.setEditable(false);
            definedBy.setBackground(Administrator.BACKGROUND_COLOR);
            m_openBDefButton=new JButton("Open");
            Administrator.constrainHeight(m_openBDefButton);
            m_openBDefButton.setEnabled(false);
            m_openBDefButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    new ViewObject(m_bDefPID).launch();
                }
            });
            bDefSelectionRow.add(definedBy, BorderLayout.WEST);
            bDefSelectionRow.add(m_behaviorComboBox, BorderLayout.CENTER);
            bDefSelectionRow.add(m_openBDefButton, BorderLayout.EAST);
            // populated on bdef combobox changes
            m_behaviorDescriptionPane=new BehaviorDescriptionPanel(null, this);
            JPanel behaviorPane=new JPanel(new BorderLayout());
            behaviorPane.add(bDefSelectionRow, BorderLayout.NORTH);
            behaviorPane.add(m_behaviorDescriptionPane, BorderLayout.SOUTH);

            // put the top layout stuff together
            JComponent[] right=new JComponent[] {m_stateComboBox,
                                                 m_labelTextField,
                                                 behaviorPane};

            GridBagLayout gb=new GridBagLayout();
            JPanel entryPane=new JPanel(gb);
            Util.addRows(left, right, gb, entryPane, true, false);

            JPanel topPane=new JPanel(new BorderLayout());
            topPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEtchedBorder(),
                    BorderFactory.createEmptyBorder(4,4,4,4)
                    ));
            topPane.add(entryPane, BorderLayout.NORTH);
            topPane.add(mechInputPanel, BorderLayout.CENTER);

            //
            // Save button
            //
            m_saveButton=new JButton("Save Disseminator");
            setValid(false);
            Administrator.constrainHeight(m_saveButton);
            m_saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        String bDefPID=mechInputPanel.getBDefPID();
                        String bMechPID=mechInputPanel.getBMechPID();
                        String label=m_labelTextField.getText();
    
                        DatastreamBindingMap bindingMap=new DatastreamBindingMap();
                        bindingMap.setDsBindMapID("set by server!"); // unnecessary
                        bindingMap.setDsBindMechanismPID(bMechPID);
                        bindingMap.setDsBindMapLabel("Binding map for bMech object: " 
                                + bMechPID);
                        bindingMap.setState("A");  // unnecessary...
                        bindingMap.setDsBindings(mechInputPanel.getBindings());
                        String state=((String) m_stateComboBox.
                                getSelectedItem()).substring(0,1);
                        String newID=
                                Administrator.APIM.addDisseminator(m_pid,
                                                                   bDefPID,
                                                                   bMechPID,
                                                                   label,
                                                                   bindingMap,
                                                                   state,
                                                                   "DisseminatorsPane generated this logMessage.");
                        addDisseminatorTab(newID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    	Administrator.showErrorDialog(Administrator.getDesktop(), "Error saving new disseminator", 
                    			e.getMessage(), e);
                    }
                }
            });

            JPanel buttonPane=new JPanel();
            buttonPane.setLayout(new FlowLayout());
            buttonPane.add(m_saveButton);

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
            add(topPane, BorderLayout.CENTER);
            add(buttonPane, BorderLayout.SOUTH);
        }

        public void setValid(boolean isValid) {
            m_saveButton.setEnabled(isValid);
        }

    }

}
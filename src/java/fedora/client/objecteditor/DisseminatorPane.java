package fedora.client.objecteditor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import fedora.client.Administrator;
import fedora.client.actions.ViewObject;
import fedora.client.objecteditor.types.DatastreamInputSpec;
import fedora.client.objecteditor.types.MethodDefinition;
import fedora.client.objecteditor.types.ParameterDefinition;

import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.DatastreamBinding;
import fedora.server.types.gen.Disseminator;

public class DisseminatorPane
        extends EditingPane 
        implements ChangeListener {

    private static SimpleDateFormat s_formatter=
            new SimpleDateFormat("yyyy-MM-dd' at 'hh:mm:ss");

    private String m_pid;
    private Disseminator[] m_versions;
    private DisseminatorsPane m_owner;
    private Disseminator m_mostRecent;
    private JComboBox m_stateComboBox;
    private JComboBox m_methodComboBox;
    private boolean m_done;
    private JSlider m_versionSlider;
    private Hashtable[] m_labelTables;
    private CardLayout m_versionCardLayout;
    private CurrentVersionPane m_currentVersionPane;
    private PurgeButtonListener m_purgeButtonListener;
    private JPanel m_valuePane; 
    private JPanel m_methodCard;
    private CardLayout m_methodCardLayout;
    private JPanel m_methodDescCard;
    private CardLayout m_methodDescCardLayout;
    private JTextField m_bDefLabelTextField;
    private Dimension m_labelDims;
    private ObjectEditorFrame m_gramps;

    public DisseminatorPane(ObjectEditorFrame gramps, String pid, 
            Disseminator[] versions, DisseminatorsPane owner)
            throws Exception {
        super(gramps, owner, versions[0].getID());
        m_gramps=gramps;
        m_pid=pid;
        m_owner=owner;
        m_versions=versions;
        m_mostRecent=versions[0];

        // set up the common pane
        // first get width and height we'll use for the labels on the left
        m_labelDims=new JLabel("Datastream Bindings").getPreferredSize();
        JLabel label1=new JLabel("State"); 
        label1.setPreferredSize(m_labelDims);
        JLabel label2=new JLabel("Behavior Definition");
        label2.setPreferredSize(m_labelDims);
        JLabel label3=new JLabel("Method Definitions");
        label3.setPreferredSize(m_labelDims);
        JLabel[] left=new JLabel[] {label1, label2, label3};
        m_stateComboBox=new JComboBox(new String[] {"Active", "Inactive", "Deleted"});
        if (m_mostRecent.getState().equals("A")) {
            m_stateComboBox.setSelectedIndex(0);
            m_stateComboBox.setBackground(Administrator.ACTIVE_COLOR);
        } else if (m_mostRecent.getState().equals("I")) {
            m_stateComboBox.setSelectedIndex(1);
            m_stateComboBox.setBackground(Administrator.INACTIVE_COLOR);
        } else {
            m_stateComboBox.setSelectedIndex(2);
            m_stateComboBox.setBackground(Administrator.DELETED_COLOR);
        }
        m_stateComboBox.addActionListener(dataChangeListener);
        m_stateComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String curState;
                if (m_stateComboBox.getSelectedIndex()==1) {
                    curState="I";
                    m_stateComboBox.setBackground(Administrator.INACTIVE_COLOR);
                } else if (m_stateComboBox.getSelectedIndex()==2) {
                    curState="D";
                    m_stateComboBox.setBackground(Administrator.DELETED_COLOR);
                } else {
                    curState="A";
                    m_stateComboBox.setBackground(Administrator.ACTIVE_COLOR);
                }
                m_owner.colorTabForState(m_mostRecent.getID(), curState);
            }
        });
        JButton bDefButton=new JButton(m_mostRecent.getBDefPID());
        bDefButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                new ViewObject(m_mostRecent.getBDefPID()).launch();
            }
        });
        JPanel bDefInfo=new JPanel(new BorderLayout());
        bDefInfo.add(bDefButton, BorderLayout.WEST);
        m_bDefLabelTextField=new JTextField(m_mostRecent.getBDefLabel());
        m_bDefLabelTextField.getDocument().addDocumentListener(dataChangeListener);
        if (m_mostRecent.getState().equals("D")) {
            m_bDefLabelTextField.setEnabled(false);
        }
        bDefInfo.add(m_bDefLabelTextField, BorderLayout.CENTER);

        // get the method map info from the behavior definition
        HashMap parms=new HashMap();
        parms.put("itemID", "METHODMAP");
        java.util.List mDefs=MethodDefinition.parse(
                Administrator.DOWNLOADER.getDissemination(
                        m_mostRecent.getBDefPID(), 
                        "fedora-system:3",
                        "getItem", parms, null));
        String[] mNames=new String[mDefs.size()];
        for (int i=0; i<mDefs.size(); i++) {
            MethodDefinition mDef=(MethodDefinition) mDefs.get(i);
            mNames[i]=mDef.getName();
        }
        m_methodComboBox=new JComboBox(mNames);
        m_methodComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String mName=(String) m_methodComboBox.getSelectedItem();
                m_methodCardLayout.show(m_methodCard, mName);
                m_methodDescCardLayout.show(m_methodDescCard, mName);
            }
        });
        JPanel methodPanel=new JPanel(new BorderLayout());
        JPanel northComboBoxPanel=new JPanel(new BorderLayout());
        northComboBoxPanel.add(m_methodComboBox, BorderLayout.NORTH);
        m_methodDescCard=new JPanel();
        m_methodDescCardLayout=new CardLayout();
        m_methodDescCard.setLayout(m_methodDescCardLayout);
        northComboBoxPanel.add(m_methodDescCard, BorderLayout.CENTER);
        methodPanel.add(northComboBoxPanel, BorderLayout.WEST);
        m_methodCard=new JPanel();
        m_methodCardLayout=new CardLayout();
        m_methodCard.setLayout(m_methodCardLayout);
        m_methodCard.setBorder(BorderFactory.createEmptyBorder(0,6,0,6));
        // add each methodDetailPane to the m_methodCard using the cardlayout
        for (int i=0; i<mDefs.size(); i++) {
            MethodDefinition mDef=(MethodDefinition) mDefs.get(i);
            JPanel methodDetailPane=new JPanel(new BorderLayout());
            // label of the method at top, or "(no description)"
            JTextArea methodLabelTextArea=new JTextArea();
            methodLabelTextArea.setLineWrap(true);
            methodLabelTextArea.setEditable(false);
            methodLabelTextArea.setWrapStyleWord(true);
            methodLabelTextArea.setBackground(methodDetailPane.getBackground());
            StringBuffer text=new StringBuffer();
            if (mDef.getLabel()!=null && mDef.getLabel().length()>0) {
                text.append(mDef.getLabel());
            }
            methodLabelTextArea.setText(text.toString());
            m_methodDescCard.add(methodLabelTextArea, mNames[i]);
            if (mDef.parameterDefinitions().size()>0) {
                // do parameter tabs
                JTabbedPane parmsTabbedPane=new JTabbedPane();
                for (int j=0; j<mDef.parameterDefinitions().size(); j++) {
                    ParameterDefinition pDef=(ParameterDefinition) 
                            mDef.parameterDefinitions().get(j);
                    JPanel parmPanel=new JPanel(new BorderLayout());
                    parmPanel.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
                    // Optional parameter. [(Default value is "")]
                    // [description]
                    // Valid Values:
                    JTextArea parmDescTextArea=new JTextArea();
                    parmDescTextArea.setLineWrap(true);
                    parmDescTextArea.setEditable(false);
                    parmDescTextArea.setWrapStyleWord(true);
                    parmDescTextArea.setBackground(methodDetailPane.getBackground());
                    StringBuffer pText=new StringBuffer();
                    String pDefTitle=pDef.getName();
                    if (pDef.isRequired()) {
                        pText.append("Required parameter.");
                    } else {
                        pText.append("Optional parameter. ");
                        if (pDef.getDefaultValue()!=null && pDef.getDefaultValue().length()>0) {
                            pText.append("(Defaults to \"" + pDef.getDefaultValue() + "\")");
                        }
                    }
                    if (pDef.getLabel()!=null) {
                        pText.append("\n" + pDef.getLabel());
                    }
                    if (pDef.validValues().size()>0) {
                        pText.append("\nValid values: ");
                        for (int k=0; k<pDef.validValues().size(); k++) {
                            if (k>0) pText.append(", ");
                            pText.append((String) pDef.validValues().get(k));
                        }
                    }
                    parmDescTextArea.setText(pText.toString());
                    parmPanel.add(parmDescTextArea, BorderLayout.NORTH);
                    parmsTabbedPane.add(pDefTitle, parmPanel);
                    parmsTabbedPane.setBackgroundAt(j, Administrator.DEFAULT_COLOR);
                }
                methodDetailPane.add(parmsTabbedPane, BorderLayout.CENTER);
            }
            // then add it to the card, by name
            m_methodCard.add(methodDetailPane, mNames[i]);
        }
        // then add the card to the methodPanel, to the right of the dropdown
        methodPanel.add(m_methodCard, BorderLayout.CENTER);

        // top common
        JComponent[] right=new JComponent[] {m_stateComboBox, bDefInfo, methodPanel};
        GridBagLayout topCommonLayout=new GridBagLayout();
        JPanel topCommonPane=new JPanel(topCommonLayout);
        addRows(left, right, topCommonLayout, topCommonPane);

        // ahh well
        JPanel commonPane=new JPanel(new BorderLayout());
        commonPane.add(topCommonPane, BorderLayout.NORTH);

        // set up the version pane, with the slider
        m_purgeButtonListener=new PurgeButtonListener(versions);

        // do the slider if needed
        if (versions.length>1) {
            m_versionSlider=new JSlider(JSlider.HORIZONTAL, 0, versions.length-1, 0);
            m_versionSlider.addChangeListener(this);
            m_versionSlider.setMajorTickSpacing(1);
            m_versionSlider.setSnapToTicks(true);
            m_versionSlider.setPaintTicks(true);
            m_labelTables=new Hashtable[versions.length];
            for (int i=0; i<versions.length; i++) {
                Hashtable thisTable=new Hashtable();
                thisTable.put(new Integer(i), new JLabel("Created " 
                        + s_formatter.format(versions[i].getCreateDate().getTime())));
                m_labelTables[i]=thisTable;
            }
            m_versionSlider.setLabelTable(m_labelTables[0]);
            m_versionSlider.setPaintLabels(true);
        }

        // CENTER: m_valuePane(one card for each version)
        m_valuePane=new JPanel();
        m_versionCardLayout=new CardLayout();
        m_valuePane.setLayout(m_versionCardLayout);
        JPanel[] valuePanes=new JPanel[versions.length];
        // CARD: valuePanes[0](versionValuePane, versionActionPane)
        m_currentVersionPane=new CurrentVersionPane(m_mostRecent);
        valuePanes[0]=m_currentVersionPane;
        m_valuePane.add(valuePanes[0], "0");
        // CARD: valuePanes[1 to i](versionValuePane, versionActionPane)
        for (int i=1; i<versions.length; i++) {
            valuePanes[i]=new PriorVersionPane(versions[i]);
            m_valuePane.add(valuePanes[i], "" + i);
        }
        JPanel versionPane=new JPanel();
        // add it all to the versionPane, putting slider at north if needed
        versionPane.setLayout(new BorderLayout());
        if (versions.length>1) {
            versionPane.add(m_versionSlider, BorderLayout.NORTH);
        }
        versionPane.add(m_valuePane, BorderLayout.CENTER);

        // finally, put the panes on the mainPane
        mainPane.setLayout(new BorderLayout());
        mainPane.add(commonPane, BorderLayout.NORTH);
        mainPane.add(versionPane, BorderLayout.CENTER);
    }

    public void stateChanged(ChangeEvent e) {
       JSlider source=(JSlider)e.getSource();
       if (!source.getValueIsAdjusting()) {
           m_versionSlider.setLabelTable(m_labelTables[source.getValue()]);
           m_versionCardLayout.show(m_valuePane, "" + source.getValue());
           // set the new text of m_bDefLabelTextField
           m_bDefLabelTextField.setText(m_versions[source.getValue()].getBDefLabel());
           // and disable or enable it as necessary
           if (source.getValue()==0) {
               m_bDefLabelTextField.setEditable(true);
           } else {
               m_bDefLabelTextField.setEditable(false);
           }
       }
    }
 
    public boolean isDirty() {
        if (m_done) return false;
        int stateIndex=0;
        if (m_mostRecent.getState().equals("I")) {
            stateIndex=1;
        }
        if (m_mostRecent.getState().equals("D")) {
            stateIndex=2;
        }
        if (stateIndex!=m_stateComboBox.getSelectedIndex()) {
            return true;
        }
        if (m_currentVersionPane.isDirty()) return true;
        return false;
    }

    public void saveChanges(String logMessage) throws Exception {
        String state=null;
        int i=m_stateComboBox.getSelectedIndex();
        if (i==0)
           state="A";
        if (i==1)
           state="I";
        if (i==2)
           state="D";
        if (m_currentVersionPane.isDirty()) {
            m_currentVersionPane.saveChanges(state, logMessage);
        } else {
            Administrator.APIM.setDisseminatorState(m_pid, m_mostRecent.getID(),
                    state, logMessage);
        }
    }

    /**
     * Called when changes to the server succeeded.
     * This method can do anything, but it should at least ensure that the
     * model and view are in-sync with each other (accurately reflecting the
     * current state of the server).
     */
    public void changesSaved() {
        m_owner.refresh(m_mostRecent.getID());
        m_done=true; 
    }

    public void undoChanges() {
        if (m_mostRecent.getState().equals("A")) {
            m_stateComboBox.setSelectedIndex(0);
            m_stateComboBox.setBackground(Administrator.ACTIVE_COLOR);
        } else if (m_mostRecent.getState().equals("I")) {
            m_stateComboBox.setSelectedIndex(1);
            m_stateComboBox.setBackground(Administrator.INACTIVE_COLOR);
        } else if (m_mostRecent.getState().equals("D")) {
            m_stateComboBox.setSelectedIndex(2);
            m_stateComboBox.setBackground(Administrator.DELETED_COLOR);
        }
        m_owner.colorTabForState(m_mostRecent.getID(), m_mostRecent.getState());
        m_currentVersionPane.undoChanges();
    }

    public class CurrentVersionPane
            extends JPanel
            implements PotentiallyDirty {

        private Disseminator m_diss;
        private JTextField m_labelTextField;
        private JTextField m_bMechLabelTextField;
        public CurrentVersionPane(Disseminator diss) throws IOException {
            m_diss=diss;
            // top panel is for labels and such
            JLabel label1=new JLabel("Disseminator Label");
            label1.setPreferredSize(m_labelDims);
            JLabel label2=new JLabel("Behavior Mechanism");
            label2.setPreferredSize(m_labelDims);
            JLabel[] left=new JLabel[] {label1, label2};
            m_labelTextField=new JTextField(m_diss.getLabel());
            m_labelTextField.getDocument().addDocumentListener(dataChangeListener);
            m_labelTextField.setEditable(true);
            m_labelTextField.setEnabled(!m_diss.getState().equals("D"));
            // bmechButton, m_bMechLabelTextField
            JButton bMechButton=new JButton(m_diss.getBMechPID());
            bMechButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    new ViewObject(m_diss.getBMechPID()).launch();
                }
            });
            m_bMechLabelTextField=new JTextField(m_diss.getBMechLabel());
            m_bMechLabelTextField.getDocument().addDocumentListener(dataChangeListener);
            m_bMechLabelTextField.setEditable(true);
            m_labelTextField.setEnabled(!m_diss.getState().equals("D"));
            JPanel bMechInfo=new JPanel(new BorderLayout());
            bMechInfo.add(bMechButton, BorderLayout.WEST);
            bMechInfo.add(m_bMechLabelTextField, BorderLayout.CENTER);
            JComponent[] right=new JComponent[] {m_labelTextField, bMechInfo};
            GridBagLayout topGridBag=new GridBagLayout();
            JPanel topPanel=new JPanel(topGridBag);
            addLabelValueRows(left, right, topGridBag, topPanel);
            // middle panel is for displaying the datastream binding
            JPanel middlePanel=new JPanel(new BorderLayout());

            JPanel dsBindingsLabelPane=new JPanel(new BorderLayout());
            dsBindingsLabelPane.setBorder(BorderFactory.createEmptyBorder(0,6,0,6));
            JLabel datastreamBindingsLabel=new JLabel("Datastream Bindings");
            datastreamBindingsLabel.setPreferredSize(m_labelDims);
            dsBindingsLabelPane.add(datastreamBindingsLabel, BorderLayout.NORTH);
            middlePanel.add(dsBindingsLabelPane, BorderLayout.WEST);

// initialize one of these panels for each bmech implementing the bdef...
// later, undoChanges will undo each... similarly, isDirty() will use
// the list of them to check whether the currently displayed one is dirty




            HashMap hash=new HashMap();
            hash.put("itemID", "DSINPUTSPEC");
            DatastreamInputSpec spec=DatastreamInputSpec.parse(
                    Administrator.DOWNLOADER.getDissemination(
                            m_diss.getBMechPID(),
                            "fedora-system:3",
                            "getItem",
                            hash,
                            null)
                    );
            DatastreamBindingPane dsBindingPane=new DatastreamBindingPane(
                    m_gramps.getInitialCurrentDatastreamVersions(),
                    m_diss, spec);
            m_gramps.addDatastreamListener(dsBindingPane);
            middlePanel.add(dsBindingPane, BorderLayout.CENTER);
           // bottom panel is for the purge button
            JPanel bottomPanel=new JPanel(new FlowLayout());
            JButton purgeButton=new JButton("Purge...");
            purgeButton.setActionCommand(s_formatter.format(m_diss.getCreateDate().getTime()));
            purgeButton.addActionListener(m_purgeButtonListener);
            bottomPanel.add(purgeButton);

            setLayout(new BorderLayout());
            add(topPanel, BorderLayout.NORTH);
            add(middlePanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
        }

        public boolean isDirty() {
            if (!m_bDefLabelTextField.getText().equals(m_diss.getBDefLabel())) {
                return true;
            }
            if (!m_labelTextField.getText().equals(m_diss.getLabel())) {
                return true;
            }
            if (!m_bMechLabelTextField.getText().equals(m_diss.getBMechLabel())) {
                return true;
            }
            // is the bmech the same?

                // if so, is the datastreambindingpane dirty?
            return false;
        }

        public void saveChanges(String state, String logMessage)
                throws Exception {
            throw new IOException("Only state changes can be saved at this time.");
        }

        public void undoChanges() {
            m_labelTextField.setText(m_diss.getLabel());
            m_bDefLabelTextField.setText(m_diss.getBDefLabel());
            m_bMechLabelTextField.setText(m_diss.getBMechLabel());
        }

    }


    public class PriorVersionPane
            extends JPanel {

        private Disseminator m_diss;

        private JTextField m_labelTextField;
        private JTextField m_bMechLabelTextField;

        public PriorVersionPane(Disseminator diss) {
            m_diss=diss;
            // top panel is for labels and such
            JLabel label1=new JLabel("Disseminator Label");
            label1.setPreferredSize(m_labelDims);
            JLabel label2=new JLabel("Behavior Mechanism");
            label2.setPreferredSize(m_labelDims);
            JLabel[] left=new JLabel[] {label1, label2};
            m_labelTextField=new JTextField(m_diss.getLabel());
            m_labelTextField.setEditable(false);
            // bmechButton, m_bMechLabelTextField
            JButton bMechButton=new JButton(m_diss.getBMechPID());
            bMechButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    new ViewObject(m_diss.getBMechPID()).launch();
                }
            });
            m_bMechLabelTextField=new JTextField(m_diss.getBMechLabel());
            m_bMechLabelTextField.setEditable(false);
            JPanel bMechInfo=new JPanel(new BorderLayout());
            bMechInfo.add(bMechButton, BorderLayout.WEST);
            bMechInfo.add(m_bMechLabelTextField, BorderLayout.CENTER);
            JComponent[] right=new JComponent[] {m_labelTextField, bMechInfo};
            GridBagLayout topGridBag=new GridBagLayout();
            JPanel topPanel=new JPanel(topGridBag);
            addLabelValueRows(left, right, topGridBag, topPanel);
            // middle panel is for displaying the datastream binding
            JPanel middlePanel=new JPanel(new BorderLayout());
            // DatastreamBinding[] bindArray=m_diss.getDsBindMap().getDsBindings()
            //                       getBindKeyName
            //                       getBindLabel
            //                       getDatastreamID
            //                       getSeqNo
            SortedMap dsBindingMap=DatastreamBindingPane.getSortedBindingMap(
                    m_diss.getDsBindMap().getDsBindings());
            // one tab per binding key
            JTabbedPane bindingTabbedPane=new JTabbedPane();
            bindingTabbedPane.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
            Iterator keys=dsBindingMap.keySet().iterator();
            int tabNum=-1;
            while (keys.hasNext()) {
                tabNum++;
                String key=(String) keys.next();
                Set values=(Set) dsBindingMap.get(key);
                Iterator valueIter=values.iterator();
                Object[][] rowData=new Object[values.size()][2];
                int i=-1;
                while (valueIter.hasNext()) {
                    i++;
                    DatastreamBinding binding=(DatastreamBinding) valueIter.next();
                    rowData[i][0]=binding.getDatastreamID();
                    rowData[i][1]=binding.getBindLabel();
                }
                JTable table=new JTable(rowData,
                                        new String[] {"Datastream", "Binding Label"});
                table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
                table.getColumnModel().getColumn(0).setMinWidth(90);
                table.getColumnModel().getColumn(0).setMaxWidth(90);
                table.setEnabled(false);
                JPanel bindingTab=new JPanel(new BorderLayout());
                bindingTab.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
                bindingTab.add(new JScrollPane(table), BorderLayout.CENTER);
                bindingTabbedPane.add(key, bindingTab);
                bindingTabbedPane.setBackgroundAt(tabNum, Administrator.DEFAULT_COLOR);
            }
            JPanel dsBindingsLabelPane=new JPanel(new BorderLayout());
            dsBindingsLabelPane.setBorder(BorderFactory.createEmptyBorder(0,6,0,6));
            JLabel datastreamBindingsLabel=new JLabel("Datastream Bindings");
            datastreamBindingsLabel.setPreferredSize(m_labelDims);
            dsBindingsLabelPane.add(datastreamBindingsLabel, BorderLayout.NORTH);
            middlePanel.add(dsBindingsLabelPane, BorderLayout.WEST);
            middlePanel.add(bindingTabbedPane, BorderLayout.CENTER);
            // bottom panel is for the purge button
            JPanel bottomPanel=new JPanel(new FlowLayout());
            JButton purgeButton=new JButton("Purge...");
            purgeButton.setActionCommand(s_formatter.format(m_diss.getCreateDate().getTime()));
            purgeButton.addActionListener(m_purgeButtonListener);
            bottomPanel.add(purgeButton);

            setLayout(new BorderLayout());
            add(topPanel, BorderLayout.NORTH);
            add(middlePanel, BorderLayout.CENTER);
            add(bottomPanel, BorderLayout.SOUTH);
        }

    }

    protected class PurgeButtonListener
            implements ActionListener {

        Disseminator[] m_versions;
        Object[] m_dateStrings;
        HashMap m_dissIndex;

        public PurgeButtonListener(Disseminator[] versions) {
            m_versions=versions;
            m_dateStrings=new Object[versions.length];
            m_dissIndex=new HashMap();
            for (int i=0; i<versions.length; i++) {
                String dateAsString=s_formatter.format(versions[i].getCreateDate().getTime());
                m_dateStrings[i]=dateAsString;
                m_dissIndex.put(dateAsString, new Integer(i));
            }
        }

        public void actionPerformed(ActionEvent evt) {
            int sIndex=0;
            boolean canceled=false;
            if (m_versions.length>1) {
                String defaultValue=evt.getActionCommand(); // default date string
                String selected=(String) JOptionPane.showInputDialog(
                        Administrator.getDesktop(),
                        "Choose the latest version to purge:",
                        "Purge version(s) from disseminator " + m_versions[0].getID(),
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        m_dateStrings,
                        defaultValue);
                if (selected==null) {
                    canceled=true;
                } else {
                    sIndex=((Integer) m_dissIndex.get(selected)).intValue();
                }
            }
            if (!canceled) {
                // do warning
                boolean removeAll=false;
                String detail;
                if (sIndex==0) {
                    detail="the entire disseminator.";
                    removeAll=true;
                } else if (sIndex==m_versions.length-1) {
                    detail="the oldest version of the disseminator.";
                } else {
                    int num=m_versions.length-sIndex;
                    detail="the oldest " + num + " versions of the disseminator.";
                }
                int n = JOptionPane.showOptionDialog(Administrator.getDesktop(),
                        "This will permanently remove " + detail + "\n"
                        + "Are you sure you want to do this?",
                        "Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,     //don't use a custom Icon
                        new Object[] {"Yes", "No"},  //the titles of buttons
                        "Yes"); //default button title
                if (n==0) {
                    try {
                        Administrator.APIM.purgeDisseminator(m_pid, 
                                m_versions[sIndex].getID(),
                                m_versions[sIndex].getCreateDate());
                        if (removeAll) {
                            m_owner.remove(m_versions[0].getID());
                            m_done=true;
                        } else {
                            m_owner.refresh(m_versions[0].getID());
                            m_done=true;
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                e.getMessage(), "Purge error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    public void addRows(JComponent[] left, JComponent[] right,
            GridBagLayout gridBag, Container container) {
        GridBagConstraints c=new GridBagConstraints();
        c.insets=new Insets(0, 6, 6, 6);
        for (int i=0; i<left.length; i++) {
            c.anchor=GridBagConstraints.NORTHWEST;
            c.gridwidth=GridBagConstraints.RELATIVE; //next-to-last
            c.fill=GridBagConstraints.NONE;      //reset to default
            c.weightx=0.0;                       //reset to default
            gridBag.setConstraints(left[i], c);
            container.add(left[i]);

            c.gridwidth=GridBagConstraints.REMAINDER;     //end row
            if (!(right[i] instanceof JComboBox) && !(right[i] instanceof JButton)) {
                c.fill=GridBagConstraints.HORIZONTAL;
            } else {
                c.anchor=GridBagConstraints.WEST;
            }
            c.weightx=1.0;
            gridBag.setConstraints(right[i], c);
            container.add(right[i]);
        }

    }

}

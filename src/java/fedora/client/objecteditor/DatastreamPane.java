package fedora.client.objecteditor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fedora.client.Administrator;

import fedora.server.types.gen.Datastream;

/**
 * Displays a datastream's attributes, allowing the editing of its state,
 * and some of the most recent version's attributes.  Also provides buttons
 * for working with the content of the datastream, depending on its type.
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
public class DatastreamPane
        extends EditingPane 
        implements ChangeListener {

    private static SimpleDateFormat s_formatter=
            new SimpleDateFormat("yyyy-MM-dd' at 'hh:mm:ss");

    private Datastream m_mostRecent;
    private JComboBox m_stateComboBox;
    private JSlider m_versionSlider;
    private JPanel m_valuePane;
    private CardLayout m_versionCardLayout;
    private CurrentVersionPane m_currentVersionPane;

    /**
     * Build the pane.
     */
    public DatastreamPane(String pid, Datastream mostRecent)
            throws Exception {
        m_mostRecent=mostRecent;

        // mainPane(commonPane, versionPane)

            // NORTH: commonPane(state, mimeType, controlGroup, infoType)

                    // LEFT: labels
                    JLabel stateLabel=new JLabel("State");
                    JLabel mimeTypeLabel=new JLabel("MIME Type");
                    JLabel controlGroupLabel=new JLabel("Control Group");
                    JLabel infoTypeLabel=new JLabel("Info Type");
                    JLabel[] leftCommonLabels=new JLabel[] {stateLabel, mimeTypeLabel};
                    JLabel[] rightCommonLabels=new JLabel[] {controlGroupLabel, infoTypeLabel};

                    // RIGHT: values
                    String[] comboBoxStrings={"Active", "Inactive", "Deleted"};
                    m_stateComboBox=new JComboBox(comboBoxStrings);
                    if (mostRecent.getState().equals("A")) {
                        m_stateComboBox.setSelectedIndex(0);
                    } else if (mostRecent.getState().equals("I")) {
                        m_stateComboBox.setSelectedIndex(1);
                    } else {
                        m_stateComboBox.setSelectedIndex(2);
                    }
                    m_stateComboBox.addActionListener(dataChangeListener);
                    JLabel mimeTypeValueLabel=new JLabel(mostRecent.getMIMEType());
                    JLabel controlGroupValueLabel=new JLabel(
                            getControlGroupString(
                                    mostRecent.getControlGroup().toString())
                            );
                    JLabel infoTypeValueLabel=new JLabel(mostRecent.getInfoType());
                    JComponent[] leftCommonValues=new JComponent[] {m_stateComboBox, mimeTypeValueLabel};
                    JComponent[] rightCommonValues=new JComponent[] {controlGroupValueLabel, infoTypeValueLabel};
    
                JPanel leftCommonPane=new JPanel();
                GridBagLayout leftCommonGridBag=new GridBagLayout();
                leftCommonPane.setLayout(leftCommonGridBag);
                addLabelValueRows(leftCommonLabels, leftCommonValues, 
                        leftCommonGridBag, leftCommonPane);
            
                JPanel rightCommonPane=new JPanel();
                GridBagLayout rightCommonGridBag=new GridBagLayout();
                rightCommonPane.setLayout(rightCommonGridBag);
                addLabelValueRows(rightCommonLabels, rightCommonValues, 
                        rightCommonGridBag, rightCommonPane);

            JPanel commonPane=new JPanel();
            commonPane.setLayout(new FlowLayout());
            commonPane.add(leftCommonPane);
            commonPane.add(rightCommonPane);

            // CENTER: versionPane(m_versionSlider, m_valuePane)

                // NORTH: m_versionSlider

                Calendar[] dates=Administrator.APIM.getDatastreamHistory(pid, 
                        mostRecent.getID());
                Datastream[] versions=new Datastream[dates.length];

                // retrieve and add in descending order, by date
                Arrays.sort(dates, new CalendarComparator());
                versions[0]=mostRecent;
                for (int i=1; i<dates.length; i++) {
                    versions[i]=Administrator.APIM.getDatastream(pid,
                            mostRecent.getID(), dates[dates.length-i-1]);
                }
                if (versions.length>1) {
                    m_versionSlider=new JSlider(JSlider.HORIZONTAL,
                            0, versions.length-1, 0);
                    m_versionSlider.addChangeListener(this);
                    m_versionSlider.setMajorTickSpacing(1);
                    m_versionSlider.setSnapToTicks(true);
                    m_versionSlider.setPaintTicks(true);
                    Hashtable labelTable=new Hashtable();
                    labelTable.put(new Integer(0), new JLabel("Current"));
                    if (versions.length>1) {
                        labelTable.put(new Integer(versions.length-1), 
                                new JLabel("First"));
                    }
                    m_versionSlider.setLabelTable(labelTable);
                    m_versionSlider.setPaintLabels(true);
                }

                // CENTER: m_valuePane(one card for each version)

                m_valuePane=new JPanel();
                m_versionCardLayout=new CardLayout();
                m_valuePane.setLayout(m_versionCardLayout);
                JPanel[] valuePanes=new JPanel[versions.length];

                    // CARD: valuePanes[0](versionValuePane, versionActionPane)

                    m_currentVersionPane=new CurrentVersionPane(mostRecent);
                    valuePanes[0]=m_currentVersionPane;

                m_valuePane.add(valuePanes[0], "0");

                    // CARD: valuePanes[1 to i](versionValuePane, versionActionPane)

                    for (int i=1; i<versions.length; i++) {
                        valuePanes[i]=new PriorVersionPane(versions[i]);
    
                        m_valuePane.add(valuePanes[i], "" + i);
                    }

            JPanel versionPane=new JPanel();
            versionPane.setLayout(new BorderLayout());
            String versionPaneLabel="1 Version";
            if (versions.length>1) {
                versionPaneLabel=versions.length + " Versions";
            }
            versionPane.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6,0,0,0),
                    BorderFactory.createTitledBorder(
                            BorderFactory.createEtchedBorder(),
                            versionPaneLabel
                            )
                    ));
            if (versions.length>1) {
                versionPane.add(m_versionSlider, BorderLayout.NORTH);
            }
            versionPane.add(m_valuePane, BorderLayout.CENTER);

        mainPane.setLayout(new BorderLayout());
        mainPane.add(commonPane, BorderLayout.NORTH);
        mainPane.add(versionPane, BorderLayout.CENTER);
    }

    public void stateChanged(ChangeEvent e) {
       JSlider source=(JSlider)e.getSource();
       if (!source.getValueIsAdjusting()) {
           m_versionCardLayout.show(m_valuePane, "" + source.getValue());
       }
    }

    public boolean isDirty() {
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
        if (m_currentVersionPane.isDirty()) {
            return true;
        }
        return false;
    }

    private String getControlGroupString(String abbrev) {
        if (abbrev.equals("M")) {
            return "Internal Managed Content";
        } else if (abbrev.equals("X")) {
            return "Internal XML Metadata";
        } else if (abbrev.equals("R")) {
            return "External Redirect";
        } else {
            return "External Reference";
        }
    }

    public void saveChanges(String logMessage) 
            throws Exception {
        String state=null;
        int i=m_stateComboBox.getSelectedIndex();
        if (i==0)
           state="A";
        if (i==1)
           state="I";
        if (i==2)
           state="D";
        // how we save it depends on the control group
        // and whether the content has been edited
        // X = by value
//        Administrator.APIM.modifyObject(m_pid, state, 
//                m_labelTextField.getText(), logMessage);
    }

    public void changesSaved() {
        int i=m_stateComboBox.getSelectedIndex();
        if (i==0)
           m_mostRecent.setState("A");
        if (i==1)
           m_mostRecent.setState("I");
        if (i==2)
           m_mostRecent.setState("D");
        m_currentVersionPane.changesSaved();
    }

    public void undoChanges() {
        if (m_mostRecent.getState().equals("A"))
            m_stateComboBox.setSelectedIndex(0);
        if (m_mostRecent.getState().equals("I"))
            m_stateComboBox.setSelectedIndex(1);
        if (m_mostRecent.getState().equals("D"))
            m_stateComboBox.setSelectedIndex(2);
        m_currentVersionPane.undoChanges();
    }


    public class CalendarComparator
            implements Comparator {

        public int compare(Object o1, Object o2) {
            long ms1=((Calendar) o1).getTime().getTime();
            long ms2=((Calendar) o1).getTime().getTime();
            if (ms1<ms2) return -1;
            if (ms1>ms2) return 1;
            return 0;
        }
    }

    public class CurrentVersionPane
            extends JPanel
            implements PotentiallyDirty {

        private Datastream m_ds;
        private JTextField m_labelTextField;

private ContentEditor ed;

        public CurrentVersionPane(Datastream ds) {
            m_ds=ds;
            // How we set this JPanel up depends on:
            // Its datastream control group
            // Its mime type
/*            m_labelTextField=new JTextField(ds.getLabel());
            m_labelTextField.getDocument().addDocumentListener(
                    dataChangeListener);
            add(m_labelTextField);
                    */
            setLayout(new BorderLayout());
            if (ds.getControlGroup().toString().equals("X")) {
                // try an editor!
                try {
                new TextContentEditor();  // causes it to be registered if not already
                if (ContentHandlerFactory.hasEditor("text/xml")) {
                    ed=ContentHandlerFactory.getEditor("text/xml", new ByteArrayInputStream(ds.getContentStream()));
                    ed.setContentChangeListener(dataChangeListener);
                    add(ed.getComponent(), BorderLayout.CENTER);
                } else {
                    if (ContentHandlerFactory.hasViewer("text/xml")) {
                        ContentViewer viewer=ContentHandlerFactory.getViewer("text/xml", new ByteArrayInputStream(ds.getContentStream()));
                        add(viewer.getComponent(), BorderLayout.CENTER);
                    } else {
                        System.out.println("ERROR: No xml editor or viewer");
                    }
                }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR: " + e.getMessage());
                }
            }
        }

        public Datastream getDatastream() {
            return m_ds;
        }

        public boolean isDirty() {
   //         if (!m_ds.getLabel().equals(m_labelTextField.getText())) {
    //            return true;
     //       }
            return ed.isDirty();
        }

        public void changesSaved() {
          //  m_ds.setLabel(m_labelTextField.getText());
            ed.changesSaved();
        }

        public void undoChanges() {
          //  m_labelTextField.setText(m_ds.getLabel());
            ed.undoChanges();
        }
    }

    public class PriorVersionPane
            extends JPanel {

        private Datastream m_ds;

        public PriorVersionPane(Datastream ds) {
            m_ds=ds;
            add(new JButton(ds.getVersionID()));
        }

        public Datastream getDatastream() {
            return m_ds;
        }

    }
}
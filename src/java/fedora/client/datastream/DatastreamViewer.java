package fedora.client.datastream;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.ListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import fedora.client.Administrator;
import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.FieldSearchQuery;

/**
 *
 * <p><b>Title:</b> DatastreamViewer.java</p>
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
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DatastreamViewer
        extends JInternalFrame {

    public static SimpleDateFormat FORMATTER=new SimpleDateFormat("yyyy-MM-dd' at 'hh:mm:ss");
    public static DatastreamConduit CONDUIT;
    public static String[] datastreamState={"A","D","I","W"};

    public DatastreamViewer(String pid)
            throws Exception {
        super("Datastreams of " + pid,
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable

        CONDUIT=new DatastreamConduit(Administrator.getHost(),
                Administrator.getPort(), Administrator.getUser(), Administrator.getPass());

        // outerPane(tabbedPane, closeButtonPane)

            // CENTER: tabbedPane(datastream panels)

            JTabbedPane tabbedPane=new JTabbedPane();
            String[] dsIDs=CONDUIT.listDatastreamIDs(pid, null);
            if ( (dsIDs==null) || (dsIDs.length==0) ) {
                JPanel noDatastreamsPanel=new JPanel();
                noDatastreamsPanel.setLayout(new BorderLayout());
                noDatastreamsPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
                noDatastreamsPanel.add(new JLabel("There are no datastreams in this object."), BorderLayout.CENTER);
                tabbedPane.addTab("NO DATASTREAMS", noDatastreamsPanel);
            } else {
                for (int i=0; i<dsIDs.length; i++) {
                    tabbedPane.addTab(dsIDs[i], new DatastreamPanel(pid,
                            CONDUIT.getDatastream(pid, dsIDs[i], null)));
                }
            }
            tabbedPane.setSelectedIndex(0);

            // SOUTH: closeButtonPane(closeButton)

                // FLOW: closeButton

                JButton closeButton=new JButton("Close");
                closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        doDefaultCloseAction();
                    }
                });

            JPanel closeButtonPane=new JPanel();
            closeButtonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
            closeButtonPane.add(closeButton);

        JPanel outerPane=new JPanel();
        outerPane.setLayout(new BorderLayout());
        outerPane.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        outerPane.add(tabbedPane, BorderLayout.CENTER);
        outerPane.add(closeButtonPane, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(outerPane, BorderLayout.CENTER);

        setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Open16.gif")));

        setSize(720,520);
    }

    public class DatastreamPanel
            extends JPanel {

        public DatastreamPanel(String pid, Datastream ds) {
            boolean xml=false;
            if (ds.getControlGroup().toString().equals("X")) {
                xml=true;
            }

            // this(outerLabelPanel, valuePanel, saveButtonPanel)

                // WEST: outerLabelPanel

                    // NORTH: labelPanel

                        // GRID: vertical labels
                        JLabel modifiedLabel=new JLabel("Last Modified");
                        JLabel mimeTypeLabel=new JLabel("MIME Type");
                        JLabel infoTypeLabel=new JLabel("Info Type");
                        JLabel controlGroupLabel=new JLabel("Control Group");
                        JLabel labelLabel=new JLabel("Label");
                        JLabel contentLabel;
                        if (xml) {
                            contentLabel=new JLabel("Content");
                        } else {
                            contentLabel=new JLabel("Location");
                        }
                        JLabel stateLabel=new JLabel("State");

                    JPanel labelPanel=new JPanel();
                    labelPanel.setLayout(new GridLayout(7, 1, 0, 3));
                    labelPanel.setBorder(BorderFactory.createEmptyBorder(0,6,0,12));
                    labelPanel.add(modifiedLabel);
                    labelPanel.add(mimeTypeLabel);
                    labelPanel.add(infoTypeLabel);
                    labelPanel.add(controlGroupLabel);
                    labelPanel.add(labelLabel);

                JPanel outerLabelPanel=new JPanel();
                outerLabelPanel.setLayout(new BorderLayout());
                outerLabelPanel.add(labelPanel, BorderLayout.NORTH);

                // CENTER: valuePanel(singleLineValuePanel [, multiLineValuePanel])

                    // NORTH: singleLineValuePanel

                        // GRID: vertical values

                        JLabel modifiedValueLabel=new JLabel(FORMATTER.format(ds.getCreateDate().getTime()));
                        JLabel mimeTypeValueLabel=new JLabel(ds.getMIMEType());
                        JLabel infoTypeValueLabel=new JLabel(ds.getInfoType());
                        String group=ds.getControlGroup().toString();
                        String controlGroupString;
                        if (group.equals("X")) {
                            controlGroupString="Internal XML";
                        } else if (group.equals("M")) {
                            controlGroupString="Internal";
                        } else if (group.equals("E")) {
                            controlGroupString="External";
                        } else if (group.equals("R")) {
                            controlGroupString="Redirect";
                        } else {
                            controlGroupString=group;
                        }
                        JLabel controlGroupValueLabel=new JLabel(controlGroupString);

                    JPanel singleLineValuePanel=new JPanel();
                    singleLineValuePanel.setLayout(new GridLayout(4, 1, 0, 3));
                    singleLineValuePanel.add(modifiedValueLabel);
                    singleLineValuePanel.add(mimeTypeValueLabel);
                    singleLineValuePanel.add(infoTypeValueLabel);
                    singleLineValuePanel.add(controlGroupValueLabel);

                    // CENTER: multiLineValuePanel(fieldGrid [, xmlEditor])

                        // NORTH: fieldGrid

                            JTextField labelValueField=new JTextField(ds.getLabel(), 15);
                            labelValueField.setCaretPosition(0);

                        JPanel fieldGrid=new JPanel();
                        GridBagLayout gridbag = new GridBagLayout();
                        GridBagConstraints c = new GridBagConstraints();
                        fieldGrid.setLayout(gridbag);
                        int numRows=1;
                        if (!xml) numRows++;
                        //fieldGrid.setLayout(new GridLayout(numRows, 1));
                        c.gridx=0; // column 1
                        c.gridy=0; // row 1
                        c.weightx=1.0; // use any available horizontal space
                        c.gridwidth=GridBagConstraints.REMAINDER; // last row
                        c.insets = new Insets(3,0,0,0); // pad top
                        c.fill=GridBagConstraints.HORIZONTAL; // fill horizontally
                        c.anchor=GridBagConstraints.WEST; // align left
                        gridbag.setConstraints(labelValueField, c);
                        fieldGrid.add(labelValueField);
                        JTextField locationValueField=null;
                        if (!xml) {
                            String locationValue;
                            if (group.equals("M")) {
                                locationValue="";
                            } else {
                                locationValue=ds.getLocation();
                            }
                            locationValueField=new JTextField(locationValue, 15);
                            locationValueField.setCaretPosition(0);
                            c.gridx=0; // column 1
                            c.gridy=1; // row 2
                            c.insets = new Insets(0,0,0,0); // reset to default
                            gridbag.setConstraints(locationValueField, c);
                            fieldGrid.add(locationValueField);
                            labelPanel.add(contentLabel);
                            labelPanel.add(stateLabel);
                        }

                    JPanel multiLineValuePanel=new JPanel();
                    multiLineValuePanel.setLayout(new BorderLayout());
                    multiLineValuePanel.add(fieldGrid, BorderLayout.NORTH);
                    JTextArea xmlEditor=null;
                    if (xml) {
                        // CENTER: [JScrollPane(xmlEditor)]
                        xmlEditor=new JTextArea();
                        xmlEditor.setFont(new Font("monospaced", Font.PLAIN, 12));
                        try {
                            // use xerces to pretty print the xml to the editor
                            OutputFormat fmt=new OutputFormat("XML", "UTF-8", true);
                            fmt.setOmitXMLDeclaration(true);
                            fmt.setIndent(2);
                            fmt.setLineWidth(120);
                            fmt.setPreserveSpace(false);
                            ByteArrayOutputStream buf=new ByteArrayOutputStream();
                            XMLSerializer ser=new XMLSerializer(buf, fmt);
                            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
                            factory.setNamespaceAware(true);
                            DocumentBuilder builder=factory.newDocumentBuilder();
                            Document doc=builder.parse(new ByteArrayInputStream(ds.getContentStream()));
                            ser.serialize(doc);
                            xmlEditor.setText(new String(buf.toByteArray(), "UTF-8"));
                        } catch (Exception e) {
                            System.out.println("ERROR: " + e.getClass().getName() + " : " + e.getMessage());
                        }
                        xmlEditor.setCaretPosition(0);
                        multiLineValuePanel.add(new JScrollPane(xmlEditor), BorderLayout.CENTER);
                        labelPanel.add(stateLabel);
                        labelPanel.add(contentLabel);
                    }

                    JComboBox stateValueField = new JComboBox(datastreamState);
                    stateValueField.setEditable(true);
                    String dsState = ds.getState();
                    stateValueField.setSelectedItem(dsState);
                    Dimension dt = stateValueField.getPreferredSize();
                    stateValueField.setPreferredSize(new Dimension((int)dt.getWidth()-90, (int)
dt.getHeight()-5));
                    c.gridx=0; // column 1
                    c.gridy=2; // row 3
                    c.gridheight=GridBagConstraints.REMAINDER; // last in column
                    c.insets=new Insets(0,0,0,0); // reset to default
                    c.fill=GridBagConstraints.NONE; // reset to default
                    gridbag.setConstraints(stateValueField, c);
                    fieldGrid.add(stateValueField);

                JPanel valuePanel=new JPanel();
                valuePanel.setLayout(new BorderLayout());
                valuePanel.add(singleLineValuePanel, BorderLayout.NORTH);
                valuePanel.add(multiLineValuePanel, BorderLayout.CENTER);

                // EAST: saveButtonPanel(saveButton)

                    // NORTH: saveButton
                    JButton saveButton=new JButton("Save");
                    /*JButton deleteButton=new JButton("Delete");
                    JButton withdrawButton=new JButton("Withdraw");*/
                    if (xml) {
                        if (ds.getID().equals("METHODMAP")
                                || ds.getID().equals("DSINPUTSPEC")
                                || ds.getID().equals("WSDL") ) {
                           saveButton.setText("Note");
                           /*deleteButton.setText("Delete - Note");
                           withdrawButton.setText("Withdraw - Note");*/
                           saveButton.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent ae) {
                                   JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                       "METHODMAP, DSINPUTSPEC, and WSDL datastreams cannot be modified at this time.",
                                       "Note: Unmodifiable Datastream",
                                       JOptionPane.INFORMATION_MESSAGE);
                               }
                           });
                           /*deleteButton.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent ae) {
                                   JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                       "METHODMAP, DSINPUTSPEC, and WSDL datastreams cannot be modified at this time.",
                                       "Note: Unmodifiable Datastream",
                                       JOptionPane.INFORMATION_MESSAGE);
                               }
                           });
                           withdrawButton.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent ae) {
                                   JOptionPane.showMessageDialog(Administrator.getDesktop(),
                                       "METHODMAP, DSINPUTSPEC, and WSDL datastreams cannot be modified at this time.",
                                       "Note: Unmodifiable Datastream",
                                       JOptionPane.INFORMATION_MESSAGE);
                               }
                           });*/
                        } else {
                           saveButton.addActionListener(
                                   new SaveDatastreamByValueListener(pid,
                                   ds.getID(), labelValueField,
                                   modifiedValueLabel, xmlEditor, stateValueField));
                        }
                    } else {
                        saveButton.addActionListener(
                                new SaveDatastreamByReferenceListener(pid,
                                ds.getID(), labelValueField,
                                modifiedValueLabel, locationValueField, stateValueField));
                        /*withdrawButton.addActionListener(
                                new WithdrawDatastreamListener(pid,
                                ds.getID(), modifiedValueLabel));
                        deleteButton.addActionListener(
                                new DeleteDatastreamListener(pid,
                                ds.getID(), modifiedValueLabel));*/
                    }

            JPanel saveButtonPanel=new JPanel();
            saveButtonPanel.setLayout(new BorderLayout());
            saveButtonPanel.add(saveButton, BorderLayout.NORTH);
            /*gridbag = new GridBagLayout();
            c = new GridBagConstraints();
            saveButtonPanel.setLayout(gridbag);

                    // CENTER: saveButton
            saveButtonPanel.add(saveButton, BorderLayout.CENTER);
            c.gridx=0; // column 1
            c.gridy=0; // row 1
            c.gridwidth=GridBagConstraints.REMAINDER; // one column wide
            c.insets = new Insets(10,10,10,0); // pad top and bottom
            c.fill=GridBagConstraints.HORIZONTAL;
            gridbag.setConstraints(saveButton, c);
            saveButtonPanel.add(saveButton);

                    // NORTH: withdrawButton
            c.gridx=0; // column 1
            c.gridy=1; // row 2
            gridbag.setConstraints(withdrawButton, c);
            saveButtonPanel.add(withdrawButton);

                    // SOUTH: deleteButton
            c.gridx=0; // column 1
            c.gridy=2; // row 3
            gridbag.setConstraints(deleteButton, c);
            saveButtonPanel.add(deleteButton);*/

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
            add(outerLabelPanel, BorderLayout.WEST);
            add(valuePanel, BorderLayout.CENTER);
            add(saveButtonPanel, BorderLayout.EAST);
        }

    }

    public class SaveDatastreamByReferenceListener
            implements ActionListener {

        private String m_pid;
        private String m_dsId;
        private JTextField m_labelField;
        private JLabel m_modifiedDateLabel;
        private JTextField m_locationField;
        private JComboBox m_stateValueField;

        public SaveDatastreamByReferenceListener(String pid, String dsId,
                JTextField labelField, JLabel modifiedDateLabel,
                JTextField locationField, JComboBox stateValueField) {
            m_pid=pid;
            m_dsId=dsId;
            m_labelField=labelField;
            m_modifiedDateLabel=modifiedDateLabel;
            m_locationField=locationField;
            m_stateValueField=stateValueField;
        }

        public void actionPerformed(ActionEvent ae) {
            String logMessage=JOptionPane.showInputDialog("Enter a log message.");
            if (logMessage==null) return;
            try {
                DatastreamViewer.CONDUIT.modifyDatastreamByReference(m_pid, m_dsId,
                        m_labelField.getText(), logMessage,
                        m_locationField.getText(), (String)m_stateValueField.getSelectedItem());
                Date dt=DatastreamViewer.CONDUIT.getDatastream(m_pid, m_dsId, null).
                        getCreateDate().getTime();
                m_modifiedDateLabel.setText(DatastreamViewer.FORMATTER.format(dt));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Administrator.getDesktop(),
                    e.getClass().getName() + ": " + e.getMessage(),
                    "Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
/*
//
    public class DeleteDatastreamListener
            implements ActionListener {

        private String m_pid;
        private String m_dsId;
        private JTextField m_labelField;
        private JLabel m_modifiedDateLabel;
        private JTextField m_locationField;

        public DeleteDatastreamListener(String pid, String dsId, JLabel modifiedDateLabel) {
            m_pid=pid;
            m_dsId=dsId;
            m_modifiedDateLabel=modifiedDateLabel;
        }

        public void actionPerformed(ActionEvent ae) {
            String logMessage=JOptionPane.showInputDialog("Enter a log message.");
            if (logMessage==null) return;
            try {
                DatastreamViewer.CONDUIT.deleteDatastream(m_pid, m_dsId, logMessage);
                Date dt=DatastreamViewer.CONDUIT.getDatastream(m_pid, m_dsId, null).
                        getCreateDate().getTime();
                m_modifiedDateLabel.setText(DatastreamViewer.FORMATTER.format(dt));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Administrator.getDesktop(),
                    e.getClass().getName() + ": " + e.getMessage(),
                    "Failed",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
//

//
    public class WithdrawDatastreamListener
            implements ActionListener {

        private String m_pid;
        private String m_dsId;
        private JTextField m_labelField;
        private JLabel m_modifiedDateLabel;
        private JTextField m_locationField;

        public WithdrawDatastreamListener(String pid, String dsId, JLabel modifiedDateLabel) {
            m_pid=pid;
            m_dsId=dsId;
            m_modifiedDateLabel=modifiedDateLabel;
        }

        public void actionPerformed(ActionEvent ae) {
            String logMessage=JOptionPane.showInputDialog("Enter a log message.");
            if (logMessage==null) return;
            try {
                DatastreamViewer.CONDUIT.withdrawDatastream(m_pid, m_dsId, logMessage);
                Date dt=DatastreamViewer.CONDUIT.getDatastream(m_pid, m_dsId, null).
                        getCreateDate().getTime();
                m_modifiedDateLabel.setText(DatastreamViewer.FORMATTER.format(dt));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Administrator.getDesktop(),
                    e.getClass().getName() + ": " + e.getMessage(),
                    "Failed",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
//
*/
    public class SaveDatastreamByValueListener
            implements ActionListener {

        private String m_pid;
        private String m_dsId;
        private JTextField m_labelField;
        private JLabel m_modifiedDateLabel;
        private JTextArea m_textArea;
        private JComboBox m_stateValueField;

        public SaveDatastreamByValueListener(String pid, String dsId,
                JTextField labelField, JLabel modifiedDateLabel,
                JTextArea textArea, JComboBox stateValueField) {
            m_pid=pid;
            m_dsId=dsId;
            m_labelField=labelField;
            m_modifiedDateLabel=modifiedDateLabel;
            m_textArea=textArea;
            m_stateValueField=stateValueField;
        }

        public void actionPerformed(ActionEvent ae) {
            String logMessage=JOptionPane.showInputDialog("Enter a log message.");
            if (logMessage==null) return;
            try {
                DatastreamViewer.CONDUIT.modifyDatastreamByValue(m_pid, m_dsId,
                        m_labelField.getText(), logMessage,
                        m_textArea.getText().getBytes("UTF-8"), (String)m_stateValueField.getSelectedItem());
                Date dt=DatastreamViewer.CONDUIT.getDatastream(m_pid, m_dsId, null).
                        getCreateDate().getTime();
                m_modifiedDateLabel.setText(DatastreamViewer.FORMATTER.format(dt));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Administrator.getDesktop(),
                    e.getClass().getName() + ": " + e.getMessage(),
                    "Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

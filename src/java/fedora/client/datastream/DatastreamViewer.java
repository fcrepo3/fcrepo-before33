package fedora.client.datastream;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 */
public class DatastreamViewer
        extends JInternalFrame {
        
    public static SimpleDateFormat FORMATTER=new SimpleDateFormat("yyyy-MM-dd' at 'hh:mm:ss");

    public DatastreamViewer(String pid) 
            throws Exception {
        super("Datastreams of " + pid,
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable

        // outerPane(tabbedPane, closeButtonPane)
        
            // CENTER: tabbedPane(datastream panels)
            
            JTabbedPane tabbedPane=new JTabbedPane();
            DatastreamConduit c=new DatastreamConduit(Administrator.getHost(),
                    Administrator.getPort(), Administrator.getUser(), Administrator.getPass());
            String[] dsIDs=c.listDatastreamIDs(pid, "A");
            if ( (dsIDs==null) || (dsIDs.length==0) ) {
                JPanel noDatastreamsPanel=new JPanel();
                noDatastreamsPanel.setLayout(new BorderLayout());
                noDatastreamsPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
                noDatastreamsPanel.add(new JLabel("There are no datastreams in this object."), BorderLayout.CENTER);
                tabbedPane.addTab("NO DATASTREAMS", noDatastreamsPanel);
            } else {
                for (int i=0; i<dsIDs.length; i++) {
                    tabbedPane.addTab(dsIDs[i], new DatastreamPanel(c.getDatastream(pid, dsIDs[i], null)));
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

        setSize(400,400);
    }
    
    public class DatastreamPanel
            extends JPanel {
            
        private Datastream m_ds;
        
        public DatastreamPanel(Datastream ds) {
            m_ds=ds;
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
                    
                    JPanel labelPanel=new JPanel();
                    labelPanel.setLayout(new GridLayout(6, 1));
                    labelPanel.setBorder(BorderFactory.createEmptyBorder(0,6,0,12));
                    labelPanel.add(modifiedLabel);
                    labelPanel.add(mimeTypeLabel);
                    labelPanel.add(infoTypeLabel);
                    labelPanel.add(controlGroupLabel);
                    labelPanel.add(labelLabel);
                    labelPanel.add(contentLabel);
                    
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
                    singleLineValuePanel.setLayout(new GridLayout(4, 1));
                    singleLineValuePanel.add(modifiedValueLabel);
                    singleLineValuePanel.add(mimeTypeValueLabel);
                    singleLineValuePanel.add(infoTypeValueLabel);
                    singleLineValuePanel.add(controlGroupValueLabel);
                        
                    // CENTER: multiLineValuePanel(fieldGrid [, xmlEditor])
                    
                        // NORTH: fieldGrid
                    
                            JTextField labelValueField=new JTextField(ds.getLabel(), 15);
                            labelValueField.setCaretPosition(0);
                            
                        JPanel fieldGrid=new JPanel();
                        int numRows=1;
                        if (!xml) numRows++;
                        fieldGrid.setLayout(new GridLayout(numRows, 1));
                        fieldGrid.add(labelValueField);
                        if (!xml) {
                            String locationValue;
                            if (group.equals("M")) {
                                locationValue="";
                            } else {
                                locationValue=ds.getLocation();
                            }
                            JTextField locationValueField=new JTextField(locationValue, 15);
                            locationValueField.setCaretPosition(0);
                            fieldGrid.add(locationValueField);
                        }
                        
                        
                    JPanel multiLineValuePanel=new JPanel();
                    multiLineValuePanel.setLayout(new BorderLayout());
                    multiLineValuePanel.add(fieldGrid, BorderLayout.NORTH);
                    if (xml) {
                        // CENTER: [JScrollPane(xmlEditor)]
                        JTextArea xmlEditor=new JTextArea();
                        xmlEditor.setFont(new Font("monospaced", Font.PLAIN, 12));
                        try {
                            // use xerces to pretty print the xml to the editor
                            OutputFormat fmt=new OutputFormat("XML", "UTF-8", true);
                            //fmt.setIndent(2);
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
                    }
                
                JPanel valuePanel=new JPanel();
                valuePanel.setLayout(new BorderLayout());
                valuePanel.add(singleLineValuePanel, BorderLayout.NORTH);
                valuePanel.add(multiLineValuePanel, BorderLayout.CENTER);
                
                // EAST: saveButtonPanel(saveButton)
                
                    // NORTH: saveButton
                    JButton saveButton=new JButton("Save");
                
                JPanel saveButtonPanel=new JPanel();
                saveButtonPanel.setLayout(new BorderLayout());
                saveButtonPanel.add(saveButton, BorderLayout.NORTH);
                
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
            add(outerLabelPanel, BorderLayout.WEST);
            add(valuePanel, BorderLayout.CENTER);
            add(saveButtonPanel, BorderLayout.EAST);
        }
        
    }

/*
            // NORTH: fieldsPanel(selectedFieldsLabel, modifySelectedFieldsButtonPanel)

                // CENTER: selectedFieldsLabel
                JLabel selectedFieldsLabel=new JLabel();
                StringBuffer text=new StringBuffer();
                text.append("<html><i>");
                for (int i=0; i<m_displayFields.size(); i++) {
                    if (i>0) text.append(", ");
                    text.append((String) m_displayFields.get(i));
                }
                text.append("</i></html>");
                selectedFieldsLabel.setText(text.toString());

                // EAST: modifySelectedFieldsButton
                JButton modifySelectedFieldsButton=new JButton("Change..");
                ChangeFieldsButtonListener cfbl=
                        new ChangeFieldsButtonListener(selectedFieldsLabel,
                        m_displayFields);
                modifySelectedFieldsButton.addActionListener(cfbl);

            JPanel fieldsPanel=new JPanel();
            fieldsPanel.setLayout(new BorderLayout());
            fieldsPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), "Fields to Display"),
                    BorderFactory.createEmptyBorder(0,6,6,6)));
            fieldsPanel.add(selectedFieldsLabel, BorderLayout.CENTER);
            fieldsPanel.add(modifySelectedFieldsButton, BorderLayout.EAST);

            // CENTER: tabbedPaneContainer(m_tabbedPane)

                // CENTER: m_tabbedPane(simpleSearchPanel, advancedSearchPanel)

                    // PANE 1: simpleSearchPanel(simplePromptPanel, simpleInstructionsLabel)

                        // NORTH: simplePromptPanel(promptLabel, m_simpleQueryField)

                            // FLOW: promptLabel

                            JLabel promptLabel=new JLabel("Search all fields for ");

                            // FLOW: m_simpleQueryField

                            m_simpleQueryField=new JTextField("*", 15);

                        JPanel simplePromptPanel=new JPanel();
                        simplePromptPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                        simplePromptPanel.add(promptLabel);
                        simplePromptPanel.add(m_simpleQueryField);

                        // SOUTH: simpleInstructionsLabel

                        JLabel simpleInstructionsLabel=new JLabel("<html>Note: You may use the ? and * wildcards.  '?' means <i>any one</i> character, and '*' means <i>any number of any characters</i>. Searches are case-insensitive.");

                    JPanel simpleSearchPanel=new JPanel();
                    simpleSearchPanel.setLayout(new BorderLayout());
                    simpleSearchPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
                    simpleSearchPanel.add(simplePromptPanel, BorderLayout.NORTH);
                    simpleSearchPanel.add(simpleInstructionsLabel, BorderLayout.CENTER);

                    // PANE 2: advancedSearchPanel(innerConditionsPanel, modifyConditionsOuterPanel)

                        // CENTER: innerConditionsPanel(conditionsScrollPane)

                            // CENTER: conditionsScrollPane(conditionsTable)

                                // WRAPS: conditionsTable
                                m_model=new ConditionsTableModel();
                                JTable conditionsTable=new JTable(m_model);

                            JScrollPane conditionsScrollPane=
                                    new JScrollPane(conditionsTable);
                            conditionsScrollPane.setBorder(
                                    BorderFactory.createEmptyBorder(0,0,6,6));

                        JPanel innerConditionsPanel=new JPanel();
                        innerConditionsPanel.setLayout(new BorderLayout());
                        innerConditionsPanel.add(conditionsScrollPane, BorderLayout.CENTER);

                        // EAST: modifyConditionsOuterPanel(modifyConditionsInnerPanel)

                            // NORTH: modifyConditionsInnerPanel

                                // GRID: addConditionButton
                                JButton addConditionButton=new JButton("Add..");

                                // GRID: modifyConditionButton
                                JButton modifyConditionButton=new JButton("Change..");

                                // GRID: deleteConditionButton
                                JButton deleteConditionButton=new JButton("Delete");

                            // Now that buttons are available, register the
                            // list selection listener that sets their enabled state.
                            conditionsTable.setSelectionMode(
                                    ListSelectionModel.SINGLE_SELECTION);
                            ConditionSelectionListener sListener=
                                    new ConditionSelectionListener(modifyConditionButton,
                                            deleteConditionButton, -1);
                            conditionsTable.getSelectionModel().
                                    addListSelectionListener(sListener);
                            // ..and add listeners to the buttons

                            addConditionButton.addActionListener(
                                    new AddConditionButtonListener(m_model));
                            modifyConditionButton.addActionListener(
                                    new ChangeConditionButtonListener(m_model, sListener));
                            deleteConditionButton.addActionListener(
                                    new DeleteConditionButtonListener(m_model, sListener));

                            JPanel modifyConditionsInnerPanel=new JPanel();
                            modifyConditionsInnerPanel.setLayout(new GridLayout(3, 1));
                            modifyConditionsInnerPanel.add(addConditionButton);
                            modifyConditionsInnerPanel.add(modifyConditionButton);
                            modifyConditionsInnerPanel.add(deleteConditionButton);

                        JPanel modifyConditionsOuterPanel=new JPanel();
                        modifyConditionsOuterPanel.setLayout(new BorderLayout());
                        modifyConditionsOuterPanel.add(modifyConditionsInnerPanel, BorderLayout.NORTH);

                    JPanel advancedSearchPanel=new JPanel();
                    advancedSearchPanel.setLayout(new BorderLayout());
                    advancedSearchPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
                    advancedSearchPanel.add(innerConditionsPanel, BorderLayout.CENTER);
                    advancedSearchPanel.add(modifyConditionsOuterPanel, BorderLayout.EAST);

                m_tabbedPane=new JTabbedPane();
                m_tabbedPane.addTab("Simple", simpleSearchPanel);
                m_tabbedPane.setSelectedIndex(0);
                m_tabbedPane.addTab("Advanced", advancedSearchPanel);

            JPanel tabbedPaneContainer=new JPanel();
            tabbedPaneContainer.setLayout(new BorderLayout());
            tabbedPaneContainer.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6,0,6,0),
                    BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), "Query"),
                    BorderFactory.createEmptyBorder(0,6,6,6))));
            tabbedPaneContainer.add(m_tabbedPane, BorderLayout.CENTER);

            // SOUTH: finishButtonsPanel

                // FLOW: searchButton
                JButton searchButton=new JButton("Search");
                searchButton.addActionListener(new SearchButtonListener(
                        cfbl, m_model));

                // FLOW: cancelButton
                JButton cancelButton=new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        doDefaultCloseAction();
                    }
                });

            JPanel finishButtonsPanel=new JPanel();
            finishButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            finishButtonsPanel.add(searchButton);
            finishButtonsPanel.add(cancelButton);
            
        */

}

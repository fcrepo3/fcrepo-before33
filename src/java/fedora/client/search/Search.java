package fedora.client.search;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

import fedora.client.Administrator;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.FieldSearchQuery;

public class Search
        extends JInternalFrame {
        
    private List m_displayFields;
    private ConditionsTableModel m_model;
        
    public Search() {
        super("Search Repository",
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
              
        m_displayFields=new ArrayList();
        m_displayFields.add("pid");
        m_displayFields.add("cDate");
        m_displayFields.add("title");
              
        // outerPane(fieldsPanel, conditionsPanel, finishButtonsPanel)
        
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
        
            // CENTER: outerConditionsPanel(innerConditionsPanel, modifyConditionsOuterPanel)
            
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
                    
                    JPanel modifyConditionsInnerPanel=new JPanel();
                    modifyConditionsInnerPanel.setLayout(new GridLayout(3, 1));
                    modifyConditionsInnerPanel.add(addConditionButton);
                    modifyConditionsInnerPanel.add(modifyConditionButton);
                    modifyConditionsInnerPanel.add(deleteConditionButton);
                    
                JPanel modifyConditionsOuterPanel=new JPanel();
                modifyConditionsOuterPanel.setLayout(new BorderLayout());
                modifyConditionsOuterPanel.add(modifyConditionsInnerPanel, BorderLayout.NORTH);
                
            JPanel outerConditionsPanel=new JPanel();
            outerConditionsPanel.setLayout(new BorderLayout());
            outerConditionsPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(6,0,6,0),
                    BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(), "Conditions to Match"),
                    BorderFactory.createEmptyBorder(0,6,6,6))));
            outerConditionsPanel.add(innerConditionsPanel, BorderLayout.CENTER);
            outerConditionsPanel.add(modifyConditionsOuterPanel, BorderLayout.EAST);
        
            // SOUTH: finishButtonsPanel
            
                // FLOW: cancelButton
                JButton cancelButton=new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        doDefaultCloseAction();
                    }
                });
                
                // FLOW: searchButton
                JButton searchButton=new JButton("Search");
                searchButton.addActionListener(new SearchButtonListener(
                        cfbl, m_model));
                
            JPanel finishButtonsPanel=new JPanel();
            finishButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
            finishButtonsPanel.add(cancelButton);
            finishButtonsPanel.add(searchButton);

        JPanel outerPane=new JPanel();
        outerPane.setLayout(new BorderLayout());
        outerPane.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        outerPane.add(fieldsPanel, BorderLayout.NORTH);
        outerPane.add(outerConditionsPanel, BorderLayout.CENTER);
        outerPane.add(finishButtonsPanel, BorderLayout.SOUTH);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(outerPane, BorderLayout.CENTER);

        setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Search16.gif")));

        setSize(400,400);
    }
    
    public class SelectFieldsDialog
            extends JDialog {

        private List m_selectedFields;
        
        private JCheckBox pidBox, bDefBox, typeBox, labelBox, bMechBox,
                          formatBox, fTypeBox, titleBox, identifierBox,
                          cModelBox, creatorBox, sourceBox, stateBox,
                          subjectBox, languageBox, lockerBox, descriptionBox,
                          relationBox, cDateBox, publisherBox, coverageBox,
                          mDateBox, contributorBox, rightsBox, dcmDateBox,
                          dateBox;
        
        public SelectFieldsDialog(List fieldList) {
            super(Administrator.getInstance(), "Select Fields to Display", true);
            
            // mainPanel(northPanel, southPanel)

                // NORTH: northPanel(bunch of JCheckBoxes)

                    pidBox=new JCheckBox("pid", fieldList.contains("pid"));
                    bDefBox=new JCheckBox("bDef", fieldList.contains("bDef"));
                    typeBox=new JCheckBox("type", fieldList.contains("type"));
                    labelBox=new JCheckBox("label", fieldList.contains("label"));
                    bMechBox=new JCheckBox("bMech", fieldList.contains("bMech"));
                    formatBox=new JCheckBox("format", fieldList.contains("format"));
                    fTypeBox=new JCheckBox("fType", fieldList.contains("fType"));
                    titleBox=new JCheckBox("title", fieldList.contains("title"));
                    identifierBox=new JCheckBox("identifier", fieldList.contains("identifier"));
                    cModelBox=new JCheckBox("cModel", fieldList.contains("cModel"));
                    creatorBox=new JCheckBox("creator", fieldList.contains("creator"));
                    sourceBox=new JCheckBox("source", fieldList.contains("source"));
                    stateBox=new JCheckBox("state", fieldList.contains("state"));
                    subjectBox=new JCheckBox("subject", fieldList.contains("subject"));
                    languageBox=new JCheckBox("language", fieldList.contains("language"));
                    lockerBox=new JCheckBox("locker", fieldList.contains("locker"));
                    descriptionBox=new JCheckBox("description", fieldList.contains("description"));
                    relationBox=new JCheckBox("relation", fieldList.contains("relation"));
                    cDateBox=new JCheckBox("cDate", fieldList.contains("cDate"));
                    publisherBox=new JCheckBox("publisher", fieldList.contains("publisher"));
                    coverageBox=new JCheckBox("coverage", fieldList.contains("coverage"));
                    mDateBox=new JCheckBox("mDate", fieldList.contains("mDate"));
                    contributorBox=new JCheckBox("contributor", fieldList.contains("contributor"));
                    rightsBox=new JCheckBox("rights", fieldList.contains("rights"));
                    dcmDateBox=new JCheckBox("dcmDate", fieldList.contains("dcmDate"));
                    dateBox=new JCheckBox("date", fieldList.contains("date"));

                JPanel northPanel=new JPanel();
                northPanel.setLayout(new GridLayout(9,3));
                northPanel.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
                northPanel.add(pidBox);
                northPanel.add(bDefBox);
                northPanel.add(typeBox);
                northPanel.add(labelBox);
                northPanel.add(bMechBox);
                northPanel.add(formatBox);
                northPanel.add(fTypeBox);
                northPanel.add(titleBox);
                northPanel.add(identifierBox);
                northPanel.add(cModelBox);
                northPanel.add(creatorBox);
                northPanel.add(sourceBox);
                northPanel.add(stateBox);
                northPanel.add(subjectBox);
                northPanel.add(languageBox);
                northPanel.add(lockerBox);
                northPanel.add(descriptionBox);
                northPanel.add(relationBox);
                northPanel.add(cDateBox);
                northPanel.add(publisherBox);
                northPanel.add(coverageBox);
                northPanel.add(mDateBox);
                northPanel.add(contributorBox);
                northPanel.add(rightsBox);
                northPanel.add(dcmDateBox);
                northPanel.add(dateBox);

                // SOUTH: southPanel(cancelButton, okButton)

                    JButton cancelButton=new JButton("Cancel");
                    cancelButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            setVisible(false);
                        }
                    });
                    JButton okButton=new JButton("Ok");
                    okButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            updateSelectedFields();
                            setVisible(false);
                        }
                    });
                
                JPanel southPanel=new JPanel();
                southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                southPanel.add(cancelButton);
                southPanel.add(okButton);

            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(northPanel, BorderLayout.NORTH);
            getContentPane().add(southPanel, BorderLayout.SOUTH);
            pack();
            setLocation(Administrator.getInstance().getCenteredPos(getSize().width, getSize().height));
        }
        
        public void updateSelectedFields() {
                            m_selectedFields=new ArrayList();
                            if (pidBox.isSelected()) m_selectedFields.add("pid");
                            if (bDefBox.isSelected()) m_selectedFields.add("bDef");
                            if (typeBox.isSelected()) m_selectedFields.add("type");
                            if (labelBox.isSelected()) m_selectedFields.add("label");
                            if (bMechBox.isSelected()) m_selectedFields.add("bMech");
                            if (formatBox.isSelected()) m_selectedFields.add("format");
                            if (fTypeBox.isSelected()) m_selectedFields.add("fType");
                            if (titleBox.isSelected()) m_selectedFields.add("title");
                            if (identifierBox.isSelected()) m_selectedFields.add("identifier");
                            if (cModelBox.isSelected()) m_selectedFields.add("cModel");
                            if (creatorBox.isSelected()) m_selectedFields.add("creator");
                            if (sourceBox.isSelected()) m_selectedFields.add("source");
                            if (stateBox.isSelected()) m_selectedFields.add("state");
                            if (subjectBox.isSelected()) m_selectedFields.add("subject");
                            if (languageBox.isSelected()) m_selectedFields.add("language");
                            if (lockerBox.isSelected()) m_selectedFields.add("locker");
                            if (descriptionBox.isSelected()) m_selectedFields.add("description");
                            if (relationBox.isSelected()) m_selectedFields.add("relation");
                            if (cDateBox.isSelected()) m_selectedFields.add("cDate");
                            if (publisherBox.isSelected()) m_selectedFields.add("publisher");
                            if (coverageBox.isSelected()) m_selectedFields.add("coverage");
                            if (mDateBox.isSelected()) m_selectedFields.add("mDate");
                            if (contributorBox.isSelected()) m_selectedFields.add("contributor");
                            if (rightsBox.isSelected()) m_selectedFields.add("rights");
                            if (dcmDateBox.isSelected()) m_selectedFields.add("dcmDate");
                            if (dateBox.isSelected()) m_selectedFields.add("date");
        }

        public List getSelectedFields() {
            return m_selectedFields;
        }
        
    }
    
    public class SearchButtonListener
            implements ActionListener {
        
        private ChangeFieldsButtonListener m_fieldSelector;
        private ConditionsTableModel m_model;
        
        public SearchButtonListener(ChangeFieldsButtonListener fieldSelector, ConditionsTableModel model) {
            m_fieldSelector=fieldSelector;
            m_model=model;
        }
        
        public void actionPerformed(ActionEvent e) {
            List fields=m_fieldSelector.getFieldList();
            String[] displayFields=new String[fields.size()];
            for (int i=0; i<fields.size(); i++) {
                displayFields[i]=(String) fields.get(i);
            }
            FieldSearchQuery query=new FieldSearchQuery();
            List conditions=m_model.getConditions();
            Condition[] cond=new Condition[conditions.size()];
            for (int i=0; i<conditions.size(); i++) {
                cond[i]=(Condition) conditions.get(i);
            }
            query.setConditions(cond);
            ResultFrame frame=new ResultFrame("Search Results", displayFields,
                100, query);
            frame.setVisible(true);
            Administrator.getDesktop().add(frame);
            try {
                frame.setSelected(true);
            } catch (java.beans.PropertyVetoException pve) {}
        }
    }
    
    public class ChangeFieldsButtonListener
            implements ActionListener {
            
        private JLabel m_fieldLabel;
        private List m_fieldList;
        
        public ChangeFieldsButtonListener(JLabel fieldLabel, List fieldList) {
            m_fieldLabel=fieldLabel;
            m_fieldList=fieldList;
        }
        
        public void actionPerformed(ActionEvent e) {
            // launch an editor for the fields to search on,
            // and put the values in 
            // - the label (with html and italics)
            // - the fieldList
            
            // first, construct the dialog with the values from fieldList
            SelectFieldsDialog dialog=new SelectFieldsDialog(m_fieldList);
            dialog.setVisible(true);
            if (dialog.getSelectedFields()!=null) {
                m_fieldList=dialog.getSelectedFields(); 
                // if they clicked cancel, just exit.
                // otherwise, set the values in m_fieldList,
                // then set the text of m_fieldLabel based on those.
                StringBuffer text=new StringBuffer();
                text.append("<html><i>");
                for (int i=0; i<m_fieldList.size(); i++) {
                    if (i>0) text.append(", ");
                    text.append((String) m_fieldList.get(i));
                }
                text.append("</i></html>");
                m_fieldLabel.setText(text.toString());
            }
        }
        
        public List getFieldList() {
            return m_fieldList;
        }
    }
    
    public class ConditionsTableModel
            extends AbstractTableModel {
            
        List m_conditions;
        
        public ConditionsTableModel() {
            m_conditions=new ArrayList();
        }
        
        public ConditionsTableModel(List conditions) {
            m_conditions=conditions;
        }
        
        public List getConditions() {
            return m_conditions;
        }
        
        public String getColumnName(int col) { 
            if (col==0) {
                return "Field";
            } else if (col==1) {
                return "Operator";
            } else {
                return "Value";
            }
        }
        
        public int getRowCount() { 
            return m_conditions.size();
        }
        
        public int getColumnCount() { 
            return 3;
        }
        
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
        
        public Object getValueAt(int row, int col) { 
            Condition cond=(Condition) m_conditions.get(row);
            if (col==0) {
                return cond.getProperty();
            } else if (col==1) {
                return cond.getOperator().toString();
            } else {
                return cond.getValue();
            }
        }

    }
    
}

package fedora.client.search;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
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
                modifySelectedFieldsButton.addActionListener(
                        new ChangeFieldsButtonListener(selectedFieldsLabel, 
                        m_displayFields));
                
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
                        m_displayFields, m_model));
                
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
        
        public SelectFieldsDialog() {
            super(Administrator.getInstance(), "Select Fields to Display", true);
            JButton cancelButton=new JButton("Cancel");
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            getContentPane().add(cancelButton);
        }
        
    }
    
    public class SearchButtonListener
            implements ActionListener {
        
        private List m_fields;
        private ConditionsTableModel m_model;
        
        public SearchButtonListener(List fields, ConditionsTableModel model) {
            m_fields=fields;
            m_model=model;
        }
        
        public void actionPerformed(ActionEvent e) {
            String[] displayFields=new String[m_fields.size()];
            for (int i=0; i<m_fields.size(); i++) {
                displayFields[i]=(String) m_fields.get(i);
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
            SelectFieldsDialog dialog=new SelectFieldsDialog();
            dialog.setVisible(true);
            
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

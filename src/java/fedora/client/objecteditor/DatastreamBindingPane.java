package fedora.client.objecteditor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;

import fedora.client.Administrator;
import fedora.client.objecteditor.types.DatastreamInputSpec;
import fedora.client.objecteditor.types.DatastreamBindingRule;
import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.DatastreamBinding;

/**
 * Binding pane for datastreams.
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
 */
public class DatastreamBindingPane
        extends JPanel
        implements DatastreamListener, 
                   PotentiallyDirty {

    private Datastream[] m_datastreams;
    private DatastreamInputSpec m_spec;
    private HashMap m_ruleForKey;
    private HashMap m_perkyPanels;
    private EditingPane m_owner;
    private static DatastreamBindingComparator s_dsBindingComparator=
            new DatastreamBindingComparator();

    static ImageIcon notFulfilledIcon=new ImageIcon(Administrator.cl.getResource("images/fedora/exclaim16.gif"));
    static ImageIcon fulfilledIcon=new ImageIcon(Administrator.cl.getResource("images/fedora/checkmark16.gif"));
    private JTabbedPane m_bindingTabbedPane;
    private ValidityListener m_validityListener;

    public DatastreamBindingPane(Datastream[] currentVersions,
                                 DatastreamBinding[] initialBindings,
                                 String bMechPID,
                                 DatastreamInputSpec spec,
                                 ValidityListener validityListener,  // ok if null
                                 EditingPane owner) {  // ok if null
        m_validityListener=validityListener;
        m_owner=owner;
        m_datastreams=currentVersions;
        m_spec=spec;
        m_perkyPanels=new HashMap();
        // put rules in a hash by key so they're easy to use later
        m_ruleForKey=new HashMap();
        for (int i=0; i<spec.bindingRules().size(); i++) {
            DatastreamBindingRule rule=(DatastreamBindingRule) spec.bindingRules().get(i);
            m_ruleForKey.put(rule.getKey(), rule);
        }

        // sort existing values, prepping for putting them in the table model
        SortedMap dsBindingMap=getSortedBindingMap(initialBindings);

        // construct the tabbedpane, one tab per binding key
        m_bindingTabbedPane=new JTabbedPane();
        Iterator keys=m_ruleForKey.keySet().iterator();
        int tabNum=-1;
        while (keys.hasNext()) {
            tabNum++;
            String key=(String) keys.next();
            Set values=(Set) dsBindingMap.get(key);
            if (values==null) {
                values=new HashSet();
            }
            SingleKeyBindingPanel p=new SingleKeyBindingPanel(
                    bMechPID,
                    key, 
                    values, 
                    (DatastreamBindingRule) m_ruleForKey.get(key));
            m_perkyPanels.put(key, p);
            m_bindingTabbedPane.add(key, p);
            m_bindingTabbedPane.setBackgroundAt(tabNum, Administrator.DEFAULT_COLOR);
            if (p.doValidityCheck()) {
                m_bindingTabbedPane.setIconAt(tabNum, fulfilledIcon);
            } else {
                m_bindingTabbedPane.setIconAt(tabNum, notFulfilledIcon);
            }
        }
        setLayout(new BorderLayout());
        add(m_bindingTabbedPane, BorderLayout.CENTER);

    }

    public boolean isDirty() {
        // are any of the table models dirty?
        Iterator iter=m_perkyPanels.keySet().iterator();
        while (iter.hasNext()) {
            String key=(String) iter.next();
            SingleKeyBindingPanel panel=
                    (SingleKeyBindingPanel) m_perkyPanels.get(key);
            if (panel.isDirty()) return true;
        }
        return false;
    }

    public void undoChanges() {
        // undo all mods to each binding
        Iterator iter=m_perkyPanels.keySet().iterator();
        while (iter.hasNext()) {
            String key=(String) iter.next();
            SingleKeyBindingPanel panel=
                    (SingleKeyBindingPanel) m_perkyPanels.get(key);
            panel.undoChanges();
        }
    }

    public DatastreamBinding[] getBindings() {
        Set set=new HashSet();
        Iterator iter=m_perkyPanels.keySet().iterator();
        while (iter.hasNext()) {
            String key=(String) iter.next();
            SingleKeyBindingPanel panel=
                    (SingleKeyBindingPanel) m_perkyPanels.get(key);
            set.addAll(panel.getBindings());
        }
        DatastreamBinding[] result=new DatastreamBinding[set.size()];
        iter=set.iterator();
        int i=0;
        while (iter.hasNext()) {
            result[i++]=(DatastreamBinding) iter.next();
        }
        return result;
    }

    // checks for validity of the bindings according to the rules,
    // updates the fulfilled indicators on the tabs, and returns
    // whether it's valid.
    public boolean doValidityCheck() {
        // if valid or invalid
        //     set the icon appropriately in the tabbedpane
        // if ALL VALID return true
        // else return false
        boolean panelsValid=true;
        Iterator iter=m_perkyPanels.keySet().iterator();
        while (iter.hasNext()) {
            String key=(String) iter.next();
            SingleKeyBindingPanel panel=
                    (SingleKeyBindingPanel) m_perkyPanels.get(key);
            int tabNum=m_bindingTabbedPane.indexOfTab(key);
            if (panel.doValidityCheck()) {
                // put the valid thingy on it
                m_bindingTabbedPane.setIconAt(tabNum, fulfilledIcon);

            } else {
                // put the invalid thingy on it, and make sure we
                // eventually return false.
                m_bindingTabbedPane.setIconAt(tabNum, notFulfilledIcon);
                panelsValid=false;
            }
        }
        return panelsValid;
    }

    public void fireDataChanged() {
        if (m_owner!=null) {
            m_owner.setValid(doValidityCheck());
            m_owner.dataChangeListener.dataChanged();
        } else {
            if (m_validityListener!=null) {
                m_validityListener.setValid(doValidityCheck());
            }
        }
    }

    public void datastreamAdded(Datastream ds) {
        // append to the end of the array
        Datastream[] newArray=new Datastream[m_datastreams.length+1];
        for (int i=0; i<m_datastreams.length; i++) {
            newArray[i]=m_datastreams[i];
        }
        newArray[m_datastreams.length]=ds;
        m_datastreams=newArray;
    }

    public void datastreamModified(Datastream ds) {
        // swap the value in the array
        for (int i=0; i<m_datastreams.length; i++) {
            if (ds.getID().equals(m_datastreams[i].getID())) {
                m_datastreams[i]=ds;
            }
        }
    }

    // each string will be ID - mime/type - Label
    public String[] getCandidates(DatastreamBindingRule rule, String[] usedIDs) {
        ArrayList possible=new ArrayList();
        for (int i=0; i<m_datastreams.length; i++) {
            boolean alreadyUsed=false;
            for (int j=0; j<usedIDs.length; j++) {
                if (m_datastreams[i].getID().equals(usedIDs[j])) {
                    alreadyUsed=true;
                }
            }
            if (!alreadyUsed && rule.accepts(m_datastreams[i].getMIMEType())) {
                possible.add(m_datastreams[i].getID()
                        + " - " + m_datastreams[i].getMIMEType()
                        + " - " + m_datastreams[i].getLabel());
            }
        }
        String[] out=new String[possible.size()];
        for (int i=0; i<possible.size(); i++) {
            out[i]=(String) possible.get(i);
        }
        return out;
    }

    public void datastreamPurged(String dsID) {
        // remove the datastream from the array, if it's there
        int where=-1;
        for (int i=0; i<m_datastreams.length; i++) {
            if (dsID.equals(m_datastreams[i].getID())) {
                where=i;
            }
        }
        if (where!=-1) {
            Datastream[] newArray=new Datastream[m_datastreams.length-1];
            for (int i=0; i<m_datastreams.length-1; i++) {
                if (i<where) {
                    newArray[i]=m_datastreams[i];
                } else if (i>where) {
                    newArray[i-1]=m_datastreams[i];
                }
            }
            m_datastreams=newArray;
        }
    }

    /**
     * Get a SortedMap of SortedSets of DatastreamBinding objects, keyed
     * by sorted binding key, and sorted by seqNo, respectively.
     */
    public static SortedMap getSortedBindingMap(DatastreamBinding[] bindingArray) {
        TreeMap map=new TreeMap(); // automagically sorts by key
        for (int i=0; i<bindingArray.length; i++) {
            String key=bindingArray[i].getBindKeyName();
            if (!map.containsKey(key)) {
                map.put(key, new TreeSet(s_dsBindingComparator));
            }
            ((SortedSet) map.get(key)).add(bindingArray[i]);
        }
        return map;
    }

    class SingleKeyBindingPanel
            extends JPanel
            implements TableModelListener,
                       PotentiallyDirty {

        private DatastreamBindingTableModel m_tableModel;
        private JTable m_table;
        private JLabel m_statusLabel;
        private JButton m_addButton;
        private JButton m_insertButton;
        private JButton m_removeButton;
        private JButton m_upButton;
        private JButton m_downButton;
        private boolean m_dirty;
        private boolean m_wasValid;
        private DatastreamBindingRule m_rule;
        private JEditorPane m_instructionPane;
        private NonCancelingCellEditor m_cellEditor;

        public SingleKeyBindingPanel(String bMechPID,
                                     String bindingKey, 
                                     Set dsBindings, 
                                     DatastreamBindingRule rule) {
            m_rule=rule;
            m_instructionPane=createInstructionPane(bMechPID, rule);
            m_instructionPane.setBackground(getBackground());

            JPanel statusPane=new JPanel(new BorderLayout());
            m_statusLabel=new JLabel(" Binding is incomplete.");
            JPanel leftify=new JPanel(new BorderLayout());
            leftify.add(m_statusLabel, BorderLayout.WEST);
            statusPane.add(leftify, BorderLayout.NORTH);

            m_tableModel=new DatastreamBindingTableModel(dsBindings, bindingKey, m_rule);
            m_table=new JTable(m_tableModel);
            m_cellEditor=new NonCancelingCellEditor(new JTextField(), m_tableModel);
            m_table.setDefaultEditor(Object.class, m_cellEditor);
            m_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            m_table.setRowSelectionAllowed(true);
            m_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            m_table.setShowVerticalLines(false);
            m_table.getColumnModel().getColumn(0).setMinWidth(90);
            m_table.getColumnModel().getColumn(0).setMaxWidth(90);            
			m_tableModel.addTableModelListener(this);
            if (m_table.getRowCount()>0) {
                m_table.addRowSelectionInterval(0, 0);
            }

            JPanel middlePane=new JPanel(new BorderLayout());
            middlePane.add(new JScrollPane(m_table), BorderLayout.CENTER);

            GridBagLayout gridbag=new GridBagLayout();
            JPanel buttonPane=new JPanel(gridbag);
            buttonPane.setBorder(BorderFactory.createEmptyBorder(0,4,0,0));
            GridBagConstraints c=new GridBagConstraints();
            c.gridx=0;
            c.fill=GridBagConstraints.BOTH;
            c.anchor=GridBagConstraints.NORTH;
            c.weightx=1.0;
            c.weighty=0.0;
            c.insets=new Insets(0,0,3,0);

            m_addButton=new JButton("Add...");
            gridbag.setConstraints(m_addButton, c);
            Administrator.constrainHeight(m_addButton);
            buttonPane.add(m_addButton);
            m_addButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_cellEditor.stopCellEditing();
                    // first bring up a dialog with candidates to
                    // choose from.
                    String selected=getCandidateSelection();
                    if (selected!=null) {
                        // if that succeeds, we need to tell the model
                        // to update itself and fire change events
                        String[] parts=selected.split(" - ");
                        m_tableModel.addRow(parts[0], "Binding to " + parts[2]);
                    }
                }
            });

            if (rule.orderMatters()) {
                m_insertButton=new JButton("Insert...");
                gridbag.setConstraints(m_insertButton, c);
                Administrator.constrainHeight(m_insertButton);
                buttonPane.add(m_insertButton);
            }

            m_removeButton=new JButton("Remove");
            gridbag.setConstraints(m_removeButton, c);
            Administrator.constrainHeight(m_removeButton);
            buttonPane.add(m_removeButton);
            m_removeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    m_cellEditor.stopCellEditing();
                    // tell the model to update itself and fire change events
                    m_tableModel.removeRow(m_table.getSelectedRow());
                }
            });

            if (rule.orderMatters()) {
                Component strut=Box.createVerticalStrut(6);
                gridbag.setConstraints(strut, c);
                buttonPane.add(strut);

                m_upButton=new JButton("Up");
                gridbag.setConstraints(m_upButton, c);
                Administrator.constrainHeight(m_upButton);
                buttonPane.add(m_upButton);

                m_downButton=new JButton("Down");
                gridbag.setConstraints(m_downButton, c);
                Administrator.constrainHeight(m_downButton);
                buttonPane.add(m_downButton);
            }

            c.weighty=1.0;
            c.fill=GridBagConstraints.VERTICAL;
            Component glue=Box.createVerticalGlue();
            gridbag.setConstraints(glue, c);
            buttonPane.add(glue);

            JPanel bottomPane=new JPanel(new BorderLayout());
            bottomPane.setBorder(BorderFactory.createEmptyBorder(4,0,0,0));
            bottomPane.add(middlePane, BorderLayout.CENTER);
            bottomPane.add(buttonPane, BorderLayout.EAST);

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
            add(m_instructionPane, BorderLayout.NORTH);
            add(bottomPane, BorderLayout.CENTER);
        }

        private String getCandidateSelection() {
            String[] usedIDs=m_tableModel.getUsedDatastreamIDs();
            String[] options=getCandidates(m_rule, usedIDs);
            if (options.length==0) {
                // bring up a dialog telling them there are no candidates
                // to add, so they need to add one to the object
                String more="";
                if (usedIDs.length>0) more=" more";
                JOptionPane.showInternalMessageDialog(Administrator.getDesktop(), 
                        "There are no" + more + " datastreams of the required type\n"
                        + "for this binding.  Add one to the object first.",
                        "No candidates found",
                        JOptionPane.INFORMATION_MESSAGE);
                return null;
            } else {
                StringBuffer instr=new StringBuffer();
                instr.append("Choose a datastream:");
                if (m_rule.getInputInstruction()!=null && m_rule.getInputInstruction().length()>0) {
                    instr.append("\n(");
                    instr.append(m_rule.getInputInstruction());
                    instr.append(')');
                } 
                return (String) JOptionPane.showInputDialog(
                        Administrator.getDesktop(),
                        instr.toString(),
                        "New Binding",
                        JOptionPane.QUESTION_MESSAGE, null,
                        options, options[0]);
            }
        }

        private void selectRowLater(final int rowNum) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    m_table.getSelectionModel().setSelectionInterval(rowNum, rowNum);
                }
            });
        }

        public void tableChanged(TableModelEvent e) {
		    DatastreamBindingTableModel model=(DatastreamBindingTableModel) e.getSource();
            // update the shown selection of the table
            if (e.getType()==TableModelEvent.INSERT) {
                selectRowLater(e.getFirstRow());
            } else if (e.getType()==TableModelEvent.DELETE) {
                if (m_tableModel.getRowCount()>0) {
                    if (m_tableModel.getRowCount()==e.getFirstRow()) {
                        // if the row that was deleted was the last one,
                        // select the one just above it
                        selectRowLater(e.getFirstRow()-1);
                    } else {
                        // otherwise, select the one that's now in its place
                        selectRowLater(e.getFirstRow());
                    }
                }
            }
			// update the dirty flag, then send the event up to datastreambindingpane
			if (model.isDirty()) {
                m_dirty=true;
			} else {
                m_dirty=false;
			}
            fireDataChanged();  // this will indirectly result in a call to 
                                // doValidityCheck below
		}

        public boolean doValidityCheck() {
            // check whether the model in its current state is valid
            // if so or not, update the status text and button visibility
            // appropriately, then return whether it's valid or not

            boolean isValid=updateButtonsAndReturnValidity();
            // do the appropriate completeness text updates
            if (isValid && !m_wasValid) {
                m_instructionPane.setText(m_instructionPane.getText().replaceAll("incomplete</b>.", "complete</b>.")); 
            } else if (!isValid && m_wasValid) {
                m_instructionPane.setText(m_instructionPane.getText().replaceAll("complete</b>.", "incomplete</b>.")); 
            }
           
            // remember this for next time so we don't have to do too much work
            m_wasValid=isValid;
            return isValid;
        }

        // check if it's valid, update buttons appropriately, and return
        // if it's valid
        private boolean updateButtonsAndReturnValidity() {
            Set bindingSet=getBindings();
            boolean couldUseMore=(m_rule.getMax()==-1) || (bindingSet.size()<m_rule.getMax());

            // Add : always exists; enabled if couldUseMore
            m_addButton.setEnabled(couldUseMore);
            // Insert: exists if orderMatters; enabled if couldUseMore and there's at least one binding
            if (m_rule.orderMatters()) {
                m_insertButton.setEnabled(couldUseMore && bindingSet.size()>0);
            }
            // Remove : always exists; enabled if there's at least one binding
            m_removeButton.setEnabled(bindingSet.size()>0);
            // Up and Down : exist if orderMatters; enabled if > 1 binding 
            if (m_rule.orderMatters()) {
                int row=m_table.getSelectedRow();
                // Up : enabled if there's more than one binding and 
                //      the current selection is below row 0
                m_upButton.setEnabled(bindingSet.size()>1 && row>0);
                // Down : enabled if there's more than one binding and 
                //        the current selection is above the last row
                m_downButton.setEnabled(bindingSet.size()>1 && row<(bindingSet.size()-1));
            }
            // now for returning the validity.
            // we already know the types are valid and there aren't too many
            // datastreams in the binding, because the widget doesn't allow
            // that.  the only thing that can possibly happen that's invalid
            // is not enough datastreams.
            return (bindingSet.size()>=m_rule.getMin());
        }

        // get a set of DatastreamBinding objects reflecting the current state
        // of the model
        public Set getBindings() {
            return m_tableModel.getBindings();
        }

        public boolean isDirty() {
            return m_dirty;
        }

        public void undoChanges() {
            // undo the changes in the underlying table model by asking
            // the current one for an unmodified copy
            m_tableModel=m_tableModel.getOriginal();
            m_table.setModel(m_tableModel);
			m_tableModel.addTableModelListener(this);
            m_table.getColumnModel().getColumn(0).setMinWidth(90);
            m_table.getColumnModel().getColumn(0).setMaxWidth(90);            
            if (m_table.getRowCount()>0) {
                m_table.addRowSelectionInterval(0, 0);
            }
            m_dirty=false;
            // then make sure the view is updated
            fireDataChanged();
        }

        private JEditorPane createInstructionPane(String bMechPID,
                                            DatastreamBindingRule rule) {
            StringBuffer buf=new StringBuffer();
            // requires x to y datastreams...
            buf.append("Binding of ");
            if (rule.getInputLabel()!=null && rule.getInputLabel().length()>0) {
                buf.append(rule.getInputLabel());
            } else {
                buf.append(rule.getKey());
            }
            buf.append(" is <b>incomplete</b>. Requires ");
            if (rule.orderMatters() && (rule.getMax()==-1) || (rule.getMax()>1)) {
                buf.append("<i>an ordered list</i> of ");
            }
            if (rule.getMin()==0) {
                if (rule.getMax()==-1) {
                    buf.append("<i>any number</i> of datastreams");
                } else {
                    buf.append("<i>up to ");
                    if (rule.getMax()==1) {
                        buf.append("one");
                    } else {
                        buf.append(rule.getMax());
                    }
                    buf.append("</i> datastream");
                    if (rule.getMax()>1) {
                        buf.append('s');
                    }
                }
            } else {
                buf.append("<i>");
                if (rule.getMin()==rule.getMax()) {
                    if (rule.getMin()==1) {
                        buf.append("one");
                    } else {
                        buf.append(rule.getMin());
                    }
                    buf.append("</i> datastream");
                    if (rule.getMax()>1) {
                        buf.append('s');
                    }
                } else {
                    if (rule.getMin()==1) {
                        buf.append("one");
                    } else {
                        buf.append(rule.getMin());
                    }
                    if (rule.getMax()==-1) {
                        buf.append(" or more</i> datastreams");
                    } else {
                        buf.append(" to ");
                        buf.append(rule.getMax());
                        buf.append("</i> datastreams");
                    }
                }
            }
            // of type...
            String[] types=rule.getTypes();
            buf.append(" of ");
            if (rule.accepts("*/*")) {
                buf.append("<i>any type</i>.");
            } else {
                buf.append("type ");
                buf.append("<i>");
                buf.append(types[0]);
                buf.append("</i>");
                if (types.length==2) {
                    buf.append(" or ");
                    buf.append("<i>");
                    buf.append(types[1]);
                    buf.append("</i>");
                } else if (types.length>2) {
                    for (int i=1; i<types.length; i++) {
                        buf.append(", ");
                        if (i==types.length-1) {
                            buf.append("or ");
                        }
                        buf.append("<i>");
                        buf.append(types[i]);
                        buf.append("</i>");
                    }
                }
            }
            // add inputLabel if available
            if (rule.getInputInstruction()!=null 
                    && rule.getInputInstruction().length()>0) {
                buf.append(" (");
                buf.append(rule.getInputInstruction());
                buf.append(")");
            } else {
                buf.append(".");
            }
            // finally, set up and return the Pane
            JEditorPane result=new JEditorPane("text/html", buf.toString());
            result.setEditable(false);
            return result;
        }

    }

    /**
     * A TableCellEditor that bypasses swing's awkward "undo" behavior when 
     * the table loses focus during editing, and sets the table model
     * appropriately on each editing event inside the textField, 
     * instead of waiting for the textField to lose focus.
     */
    class NonCancelingCellEditor
            extends DefaultCellEditor {

        private int m_row;
        private int m_column;
        private TableModel m_model;
        private JTextField m_textField;

        NonCancelingCellEditor(JTextField f, TableModel model) {
            super(f);
            m_textField=f;
            m_model=model;
            f.getDocument().addDocumentListener(new DocumentListener() {
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
                    // update the model for each change
                    m_model.setValueAt(m_textField.getText(), m_row, m_column);
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, 
                                                     Object value, 
                                                     boolean isSelected, 
                                                     int row, 
                                                     int column) {
            m_row=row;
            m_column=column;
            m_textField.setText((String) value);
            return m_textField;
        }

        // Overridden to ignore cancels due to focus loss
        public void cancelCellEditing() {
            stopCellEditing();
        }

    }

    class DatastreamBindingTableModel
            extends AbstractTableModel 
            implements PotentiallyDirty {

        public DatastreamBinding[] m_bindings;        
        public DatastreamBinding[] m_originalBindings;        
        public String m_bindingKey;
        public DatastreamBindingRule m_rule;

        public DatastreamBindingTableModel(Set values, String bindingKey, DatastreamBindingRule rule) {
		    m_bindingKey=bindingKey;
            m_rule=rule;
            m_bindings=new DatastreamBinding[values.size()];
            m_originalBindings=new DatastreamBinding[values.size()];
            Iterator iter=values.iterator();
            int i=0;
            while (iter.hasNext()) {
                DatastreamBinding n=(DatastreamBinding) iter.next();
                m_bindings[i]=n;
                m_originalBindings[i]=new DatastreamBinding();
				m_originalBindings[i].setBindKeyName(new String(n.getBindKeyName()));
				m_originalBindings[i].setBindLabel(new String(n.getBindLabel()));
				m_originalBindings[i].setDatastreamID(new String(n.getDatastreamID()));
				m_originalBindings[i].setSeqNo(new String(n.getSeqNo()));
				i++;
            }
        }

        public String getBindingKey() {
		    return m_bindingKey;
		}

        public Set getBindings() {
            HashSet set=new HashSet();
            for (int i=0; i<m_bindings.length; i++) {
                set.add(m_bindings[i]);    
            }
            return set;
        }

        public String[] getUsedDatastreamIDs() {
            String[] out=new String[m_bindings.length];
            for (int i=0; i<out.length; i++) {
                out[i]=m_bindings[i].getDatastreamID();
            }
            return out;
        }


// TODO: add methods for adding, inserting, and moving rows, which
//       set all the attributes (including SeqNo) appropriately
//       and fire the appropriate table change events

        /**
         * Remove a row from the model and fire an event.
         */
        public void removeRow(int rowNum) {
            DatastreamBinding[] n=new DatastreamBinding[m_bindings.length-1];
            int newRowNum=0;
            for (int i=0; i<m_bindings.length; i++) {
                if (i!=rowNum) {
                    n[newRowNum]=m_bindings[i];
                    newRowNum++;
                }
            }
            m_bindings=n;
            fireTableRowsDeleted(rowNum, rowNum);
        }

        /**
         * Append a row to the model and fire an event.
         */
        public void addRow(String dsID, String bindingLabel) {
            DatastreamBinding newBinding=new DatastreamBinding();
            newBinding.setBindKeyName(m_bindingKey);
            newBinding.setDatastreamID(dsID);
            newBinding.setBindLabel(bindingLabel);
            if (m_rule.orderMatters()) {
                newBinding.setSeqNo("" + m_bindings.length);
            } else {
                newBinding.setSeqNo("0");
            }
            // add it to the array
            DatastreamBinding[] n=new DatastreamBinding[m_bindings.length+1];
            for (int i=0; i<m_bindings.length; i++) {
                n[i]=m_bindings[i];
            }
            n[m_bindings.length]=newBinding;
            m_bindings=n;
            fireTableRowsInserted(m_bindings.length-1, m_bindings.length-1);
        }

        /**
         * Insert a row into the model and fire an event.
         */
        public void insertRow(int rowNum, String dsID, String bindingLabel) {
        }









        /**
		 * Get a new table model, initialized with the initial values given
		 * in the constructor of this table model.
		 *
		 * Used to implement undo.
		 */
        public DatastreamBindingTableModel getOriginal() {
		    LinkedHashSet origSet=new LinkedHashSet();
			for (int i=0; i<m_originalBindings.length; i++) {
			    origSet.add(m_originalBindings[i]);
			}
			return new DatastreamBindingTableModel(origSet, getBindingKey(), m_rule);
		}

        /**
		 * Are the underlying values in this table model different than
		 * those it was initialized with?
		 */
        public boolean isDirty() {
		    if (m_bindings.length!=m_originalBindings.length) return true;
			for (int i=0; i<m_bindings.length; i++) {
			    if (!m_bindings[i].equals(m_originalBindings[i])) return true;
			}
			return false;
		}

        public int getRowCount() {
            return m_bindings.length;
        }

        public int getColumnCount() {
            return 2;
        }

        public String getColumnName(int i) {
            if (i==0) return "Datastream";
            return "Binding Label";
        }

        public boolean isCellEditable(int row, int column) {
            return (column==1);
        }

        public Object getValueAt(int row, int column) {
            if (column==0) {
                return m_bindings[row].getDatastreamID();
            } else {
                return m_bindings[row].getBindLabel();
            }
        }

        public void setValueAt(Object value, int row, int column) {
            if (column==1) {
                m_bindings[row].setBindLabel((String) value);
                fireTableCellUpdated(row, column);
            }
        }
        
    }

}
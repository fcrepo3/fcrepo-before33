package fedora.client.objecteditor;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;

import fedora.client.Administrator;
import fedora.client.objecteditor.types.DatastreamInputSpec;
import fedora.client.objecteditor.types.DatastreamBindingRule;
import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.DatastreamBinding;
import fedora.server.types.gen.Disseminator;


public class DatastreamBindingPane
        extends JPanel
        implements DatastreamListener, 
                   PotentiallyDirty,
                   TableModelListener {

    private Datastream[] m_datastreams;
    private DatastreamInputSpec m_spec;
    private HashMap m_ruleForKey;
    private HashMap m_perkyPanels;
    private EditingPane m_owner;
    private static DatastreamBindingComparator s_dsBindingComparator=
            new DatastreamBindingComparator();

    static ImageIcon notFulfilledIcon=new ImageIcon(Administrator.cl.getResource("images/fedora/exclaim16.gif"));
    static ImageIcon fulfilledIcon=new ImageIcon(Administrator.cl.getResource("images/fedora/checkmark16.gif"));

    public DatastreamBindingPane(Datastream[] currentVersions,
                                 DatastreamBinding[] initialBindings,
                                 String bMechPID,
                                 DatastreamInputSpec spec,
                                 EditingPane owner) {  // ok if null
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
        JTabbedPane bindingTabbedPane=new JTabbedPane();
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
                    (DatastreamBindingRule) m_ruleForKey.get(key),
                    this);
            m_perkyPanels.put(key, p);
            bindingTabbedPane.add(key, p);
            bindingTabbedPane.setBackgroundAt(tabNum, Administrator.DEFAULT_COLOR);
            if (tabNum==0) {
                bindingTabbedPane.setIconAt(tabNum, notFulfilledIcon);
            } else {
                bindingTabbedPane.setIconAt(tabNum, fulfilledIcon);
            }
        }
        setLayout(new BorderLayout());
        add(bindingTabbedPane, BorderLayout.CENTER);

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

    // called when one of the tab's table models changed
    public void tableChanged(TableModelEvent e) {
        DatastreamBindingTableModel model=(DatastreamBindingTableModel) e.getSource();
        String key=model.getBindingKey();
		// TODO: all we really need to do here is update fulfilled indicator on tabs
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
        private TableModelListener m_listener;

        public SingleKeyBindingPanel(String bMechPID,
                                     String bindingKey, 
                                     Set dsBindings, 
                                     DatastreamBindingRule rule,
                                     TableModelListener listener) {
            m_listener=listener;
            JEditorPane instructionPane=createInstructionPane(bMechPID, rule);
            instructionPane.setBackground(getBackground());

            JPanel statusPane=new JPanel(new BorderLayout());
            m_statusLabel=new JLabel(" Binding is incomplete.");
            JPanel leftify=new JPanel(new BorderLayout());
            leftify.add(m_statusLabel, BorderLayout.WEST);
            statusPane.add(leftify, BorderLayout.NORTH);

            m_tableModel=new DatastreamBindingTableModel(dsBindings, bindingKey);
            m_table=new JTable(m_tableModel);
            m_table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            m_table.setRowSelectionAllowed(true);
            m_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            m_table.setShowVerticalLines(false);
            m_table.getColumnModel().getColumn(0).setMinWidth(90);
            m_table.getColumnModel().getColumn(0).setMaxWidth(90);            
			m_tableModel.addTableModelListener(this);
			if (listener!=null) m_tableModel.addTableModelListener(listener);

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

            m_addButton=new JButton("Add...");
            gridbag.setConstraints(m_addButton, c);
            Administrator.constrainHeight(m_addButton);
            buttonPane.add(m_addButton);

            m_insertButton=new JButton("Insert...");
            gridbag.setConstraints(m_insertButton, c);
            Administrator.constrainHeight(m_insertButton);
            buttonPane.add(m_insertButton);

            m_removeButton=new JButton("Remove");
            gridbag.setConstraints(m_removeButton, c);
            Administrator.constrainHeight(m_removeButton);
            buttonPane.add(m_removeButton);

            Component strut=Box.createVerticalStrut(8);
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
            add(instructionPane, BorderLayout.NORTH);
            add(bottomPane, BorderLayout.CENTER);
//            add(instructionPane, BorderLayout.SOUTH);
        }

        public void tableChanged(TableModelEvent e) {
		    DatastreamBindingTableModel model=(DatastreamBindingTableModel) e.getSource();
			// update button visibility and status label based on the state of 
			// the table and send a message to the owner.
			if (model.isDirty()) {
                m_dirty=true;
                
			} else {
                m_dirty=false;
			}
            fireDataChanged();
		}

        // get a set of DatastreamBinding objects reflecting the current state
        // of the model
        public Set getBindings() {
            return m_tableModel.getBindings();
        }

        public void fireDataChanged() {
            if (m_owner!=null) m_owner.dataChangeListener.dataChanged();
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
			if (m_listener!=null) m_tableModel.addTableModelListener(m_listener);
            m_table.getColumnModel().getColumn(0).setMinWidth(90);
            m_table.getColumnModel().getColumn(0).setMaxWidth(90);            
            m_dirty=false;
            fireDataChanged();
        }

        private JEditorPane createInstructionPane(String bMechPID,
                                            DatastreamBindingRule rule) {
            StringBuffer buf=new StringBuffer();
            // requires x to y datastreams...
            buf.append("<b>Binding is incomplete.</b> ");
            buf.append("Requires ");
            if (rule.orderMatters()) {
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
            if (rule.getInputLabel()!=null 
                    && rule.getInputLabel().length()>0) {
                buf.append(" (");
                buf.append(rule.getInputLabel());
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

    class DatastreamBindingTableModel
            extends AbstractTableModel 
            implements PotentiallyDirty {

        public DatastreamBinding[] m_bindings;        
        public DatastreamBinding[] m_originalBindings;        
        public String m_bindingKey;

        public DatastreamBindingTableModel(Set values, String bindingKey) {
		    m_bindingKey=bindingKey;
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


// TODO: add methods for adding, inserting, and moving rows, which
//       set all the attributes (including SeqNo) appropriately
//       and fire the appropriate table change events













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
			return new DatastreamBindingTableModel(origSet, getBindingKey());
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
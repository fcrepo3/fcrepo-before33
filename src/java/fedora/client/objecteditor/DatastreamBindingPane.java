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
                   TableModelListener {

    private Datastream[] m_datastreams;
    private DatastreamInputSpec m_spec;
    private HashMap m_ruleForKey;
    private static DatastreamBindingComparator s_dsBindingComparator=
            new DatastreamBindingComparator();

    static ImageIcon notFulfilledIcon=new ImageIcon(Administrator.cl.getResource("images/fedora/exclaim16.gif"));
    static ImageIcon fulfilledIcon=new ImageIcon(Administrator.cl.getResource("images/fedora/checkmark16.gif"));

    public DatastreamBindingPane(Datastream[] currentVersions,
                                 DatastreamBinding[] initialBindings,
                                 String bMechPID,
                                 DatastreamInputSpec spec) {
        m_datastreams=currentVersions;
        m_spec=spec;

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

    // called when one of the tab's table models changed
    public void tableChanged(TableModelEvent e) {
        DatastreamBindingTableModel model=(DatastreamBindingTableModel) e.getSource();
        String key=model.getBindingKey();
		// update fulfilled indicator on tabs,

        // then notify parent component that this on

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
            implements TableModelListener {

        private DatastreamBindingTableModel m_tableModel;
        private JTable m_table;
        private JLabel m_statusLabel;
        private JButton m_addButton;
        private JButton m_insertButton;
        private JButton m_removeButton;
        private JButton m_upButton;
        private JButton m_downButton;

        public SingleKeyBindingPanel(String bMechPID,
                                     String bindingKey, 
                                     Set dsBindings, 
                                     DatastreamBindingRule rule,
                                     TableModelListener listener) {
            JEditorPane instructionPane=createInstructionPane(bMechPID, rule);
            instructionPane.setBackground(getBackground());

            JPanel statusPane=new JPanel(new BorderLayout());
            m_statusLabel=new JLabel(" Binding is incomplete.");
            JPanel leftify=new JPanel(new BorderLayout());
            leftify.add(m_statusLabel, BorderLayout.WEST);
            statusPane.add(leftify, BorderLayout.NORTH);
  //          statusPane.add(instructionPane, BorderLayout.SOUTH);

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
            add(statusPane, BorderLayout.NORTH);
            add(bottomPane, BorderLayout.CENTER);
            add(instructionPane, BorderLayout.SOUTH);
        }

        public void tableChanged(TableModelEvent e) {
		    DatastreamBindingTableModel model=(DatastreamBindingTableModel) e.getSource();
			// update button visibility based on the state of the table
			if (model.isDirty()) {
			} else {
			}
		}

        private JEditorPane createInstructionPane(String bMechPID,
                                            DatastreamBindingRule rule) {
            StringBuffer buf=new StringBuffer();
            // requires x to y datastreams...
            buf.append("The <b>");
            buf.append(rule.getKey());
            buf.append("</b> binding requires ");
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
            // add inputInstruction if available
            if (rule.getInputInstruction()!=null 
                    && rule.getInputInstruction().length()>0) {
                buf.append(" - ");
                buf.append(rule.getInputInstruction());
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
            extends AbstractTableModel {

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
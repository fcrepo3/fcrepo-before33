package fedora.client.objecteditor;

import java.awt.*;
import javax.swing.*;
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
        implements DatastreamListener {

    private Disseminator m_diss;
    private Datastream[] m_datastreams;
    private DatastreamInputSpec m_spec;
    private ArrayList m_dsBindingModels;
    private static DatastreamBindingComparator s_dsBindingComparator=
            new DatastreamBindingComparator();

    public DatastreamBindingPane(Datastream[] currentVersions,
                                 Disseminator diss,
                                 DatastreamInputSpec spec) {
        m_diss=diss;
        m_datastreams=currentVersions;
        m_spec=spec;

        // test: just print out the input spec
        System.out.println("Datastream binding spec");
        System.out.println("  label=" + spec.getLabel());
        for (int i=0; i<spec.bindingRules().size(); i++) {
            DatastreamBindingRule rule=(DatastreamBindingRule) spec.bindingRules().get(i);
            System.out.println("  " + rule.getKey());
            System.out.println("    inputLabel=" + rule.getInputLabel());
            System.out.println("    inputInstr=" + rule.getInputInstruction());
            System.out.println("           min=" + rule.getMin());
            System.out.println("           max=" + rule.getMax());
            System.out.println("       ordered=" + rule.orderMatters());
            StringBuffer types=new StringBuffer();
            for (int j=0; j<rule.getTypes().length; j++) {
                if (j!=0) types.append(", ");
                types.append(rule.getTypes()[j]);
            }
            System.out.println("         types=" + types.toString());
        }

        // DatastreamBinding[] bindArray=m_diss.getDsBindMap().getDsBindings()
        //                       getBindKeyName
        //                       getBindLabel
        //                       getDatastreamID
        //                       getSeqNo
        SortedMap dsBindingMap=getSortedBindingMap(
                m_diss.getDsBindMap().getDsBindings());
        // one tab per binding key
        JTabbedPane bindingTabbedPane=new JTabbedPane();
        bindingTabbedPane.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        Iterator keys=dsBindingMap.keySet().iterator();
        int tabNum=-1;
        m_dsBindingModels=new ArrayList();
        while (keys.hasNext()) {
            tabNum++;
            String key=(String) keys.next();
            Set values=(Set) dsBindingMap.get(key);
            TableModel tableModel=new DatastreamBindingTableModel(values);
            m_dsBindingModels.add(tableModel);
            JTable table=new JTable(tableModel);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
            table.getColumnModel().getColumn(0).setMinWidth(90);
            table.getColumnModel().getColumn(0).setMaxWidth(90);
            JPanel bindingTab=new JPanel(new BorderLayout());
            bindingTab.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
            bindingTab.add(new JScrollPane(table), BorderLayout.CENTER);
            bindingTabbedPane.add(key, bindingTab);
            bindingTabbedPane.setBackgroundAt(tabNum, Administrator.DEFAULT_COLOR);
        }
        setLayout(new BorderLayout());
        add(bindingTabbedPane, BorderLayout.CENTER);

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

    class DatastreamBindingTableModel
            extends AbstractTableModel {

        public DatastreamBinding[] m_bindings;        

        public DatastreamBindingTableModel(Set values) {
            m_bindings=new DatastreamBinding[values.size()];
            Iterator iter=values.iterator();
            int i=0;
            while (iter.hasNext()) {
                m_bindings[i]=(DatastreamBinding) iter.next();
                i++;
            }
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
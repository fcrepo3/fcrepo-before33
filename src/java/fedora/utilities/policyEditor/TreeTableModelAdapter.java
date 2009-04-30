/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://fedora-commons.org/license/).
 */

/*
 */
package fedora.utilities.policyEditor;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * @author Robert Haschart
 */
public class TreeTableModelAdapter
        extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    JTree tree;

    TreeTableModel treeTableModel;

    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;

        tree.addTreeExpansionListener(new TreeExpansionListener() {

            // Don't use fireTableRowsInserted() here; 
            // the selection model would get  updated twice. 
            public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();
            }

            public void treeCollapsed(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
        });
    }

    // Wrappers, implementing TableModel interface. 

    public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }

    @Override
    public String getColumnName(int column) {
        return treeTableModel.getColumnName(column);
    }

    @Override
    public Class getColumnClass(int column) {
        return treeTableModel.getColumnClass(column);
    }

    public int getRowCount() {
        return tree.getRowCount();
    }

    protected Object nodeForRow(int row) {
        TreePath treePath = tree.getPathForRow(row);
        return treePath.getLastPathComponent();
    }

    public Object getValueAt(int row, int column) {
        return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return treeTableModel.isCellEditable(nodeForRow(row), column);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        treeTableModel.setValueAt(value, nodeForRow(row), column);
    }
}

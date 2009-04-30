/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://fedora-commons.org/license/).
 */

/*
 */
package fedora.utilities.policyEditor;

import javax.swing.tree.TreeModel;

public interface TreeTableModel
        extends TreeModel {

    /**
     * Returns the number ofs availible column.
     */
    public int getColumnCount();

    /**
     * Returns the name for column number <code>column</code>.
     */
    public String getColumnName(int column);

    /**
     * Returns the type for column number <code>column</code>.
     */
    public Class getColumnClass(int column);

    /**
     * Returns the value to be displayed for node <code>node</code>, at
     * column number <code>column</code>.
     */
    public Object getValueAt(Object node, int column);

    /**
     * Indicates whether the the value for node <code>node</code>, at column
     * number <code>column</code> is editable.
     */
    public boolean isCellEditable(Object node, int column);

    /**
     * Sets the value for node <code>node</code>, at column number
     * <code>column</code>.
     */
    public void setValueAt(Object aValue, Object node, int column);
}

/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.utilities.policyEditor;

/**
 * @author Robert Haschart
 */
public class FedoraSystemModel
        extends AbstractTreeTableModel
        implements TreeTableModel {

    //  Names of the columns.
    static protected String[] cNames = {"Name", "Allow Access", "Exception"};//, "And/Or", "Access"};

    //  Types of the columns.
    static protected Class[] cTypes =
            {TreeTableModel.class, String.class, String.class};//, Integer.class, String.class};

    // The the returned file length for directories. 
    public static final Integer ZERO = new Integer(0);

    /**
     * @param root
     */
    public FedoraSystemModel(Object root) {
        super(root);
        FedoraNode.model = this;
        // TODO Auto-generated constructor stub
    }

    protected Object[] getChildren(Object node) {
        FedoraNode fedoraNode = (FedoraNode) node;
        return fedoraNode.getChildren();
    }

    //
    //  The TreeModel interface
    //

    public int getChildCount(Object node) {
        Object[] children = getChildren(node);
        return children == null ? 0 : children.length;
    }

    public Object getChild(Object node, int i) {
        return getChildren(node)[i];
    }

    //
    //  The TreeTableNode interface. 
    //

    public int getColumnCount() {
        return cNames.length;
    }

    public String getColumnName(int column) {
        return cNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return cTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        FedoraNode fnode = (FedoraNode) node;
        if (column == 0) {
            return fnode.toString();
        } else {
            return fnode.getValue(column - 1);
        }
    }

    @Override
    public void setValueAt(Object aValue, Object node, int column) {
        FedoraNode fnode = (FedoraNode) node;
        if (column > 0) {
            Object prev = fnode.getValue(column - 1);
            if (prev != aValue) {
                PolicyEditor.mainWin.setDirty();
            }
            fnode.setValue(column - 1, aValue);
        }
    }

}

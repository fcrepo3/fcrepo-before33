/*
 */

package fedora.utilities.policyEditor;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Graphics;

/**
  */

public class JTreeTable extends JTable 
{
    protected TreeTableCellRenderer tree;
    protected TreeTableModel model;

    public JTreeTable(TreeTableModel treeTableModel) 
    {
    	super();
        setModel(treeTableModel);    
    }
    
    public void setModel(TreeTableModel treeTableModel)
    {
        // Create the tree. It will be used as a renderer and editor. 
    	model = treeTableModel;
        tree = new TreeTableCellRenderer(model); 
    
    	// Install a tableModel representing the visible rows in the tree. 
    	super.setModel(new TreeTableModelAdapter(model, tree));
    
    	// Force the JTable and JTree to share their row selection models. 
    	tree.setSelectionModel(new DefaultTreeSelectionModel() 
        { 
    	    // Extend the implementation of the constructor, as if: 
    	 /* public this() */ 
            {
    		setSelectionModel(listSelectionModel); 
    	    } 
    	}); 
    	// Make the tree and table row heights the same. 
        setRowHeight(20);
    	tree.setRowHeight(getRowHeight());
    
    	// Install the tree editor renderer and editor. 
    	setDefaultRenderer(TreeTableModel.class, tree); 
        setDefaultEditor(String.class, new DefaultCellEditor(new JComboBox()));  
        setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());  
    
    	setShowGrid(true);
    	setIntercellSpacing(new Dimension(1, 1)); 
  //      initColumnSizes();
        setUpSubjectColumn(true, this.getColumnModel().getColumn(1));
        setUpSubjectColumn(false, this.getColumnModel().getColumn(2));
    }
    
    public Component prepareEditor(TableCellEditor editor, int row, int column)
    {
        //Set up the editor for the sport cells.
        if (editor instanceof DefaultCellEditor)
        {
            JComboBox comboBox = (JComboBox)(((DefaultCellEditor)editor).getComponent());
            comboBox.removeAllItems();
            Object value = this.getValueAt(row, column);
            comboBox.setRenderer(new MyCellRenderer());
            int offset = 0;
            if (value.equals(FedoraNode.seeChildren))
            {
                comboBox.addItem(FedoraNode.seeChildren);
                comboBox.setSelectedIndex(0);
            }
            else if (value.equals(FedoraNode.seeParent))
            {
                comboBox.addItem(FedoraNode.seeParent);
                comboBox.setSelectedIndex(0);
            }
            for (int i = 0; i < GroupRuleInfo.getNumRules(column == 1); i++)
            {
                comboBox.addItem(GroupRuleInfo.getEntry(column == 1, i));
                if (value.toString().equals(GroupRuleInfo.getEntry(column == 1, i).toString()))
                {
                    comboBox.setSelectedIndex(i);   
                }
            }
            return(comboBox);
       }
       else 
       {
           return(super.prepareEditor(editor, row, column));
       }
    }    
    
    public void expandNodes()
    {
        Object root = model.getRoot();
        expandNodes(root, tree.getPathForRow(0));
    }
    
    public void expandNodes(Object parent, TreePath path)
    {
        for (int i = 0; i < model.getChildCount(parent); i++)
        {
            Object child = model.getChild(parent, i);
            if (model.getValueAt(child, 1) == FedoraNode.seeChildren ||
                model.getValueAt(child, 2) == FedoraNode.seeChildren)
            {
                TreePath newPath = path.pathByAddingChild(child); 
                int row = tree.getRowForPath(newPath);
                tree.expandRow(row);
                expandNodes(child, newPath);
            }
            else
            {
                TreePath newPath = path.pathByAddingChild(child); 
                int row = tree.getRowForPath(newPath);
                tree.collapseRow(row);
             }
        }
    }
    
    
    public void setValueAt(Object aValue, int row, int column) 
    {
        getModel().setValueAt(aValue, row, convertColumnIndexToModel(column));
        repaint();  // ugh
    }
    
    class MyCellRenderer extends JLabel implements ListCellRenderer 
    {
        public MyCellRenderer() 
        {
            setOpaque(true);
        }
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
  /*          if (index == -1)
            {
                JLabel label = new JLabel(value.toString());
                label.setBackground(isSelected ? Color.blue : Color.white);
                label.setForeground(isSelected ? Color.white : Color.black);
                setBackground(isSelected ? Color.blue : Color.white);
                setForeground(isSelected ? Color.white : Color.black);
                return(label);
            }
            else*/
            {
                setText(value.toString());
                setBackground(isSelected ? Color.blue : Color.white);
                setForeground(isSelected ? Color.white : Color.black);
            }
            return this;
        }
    }

    public void setUpSubjectColumn(boolean allowOrDeny, TableColumn subjectColumn/*, TreeTableCellEditor editor*/) 
    {
//        //Set up the editor for the access cells.
//        subjectColumn.setCellEditor(editor);

        
        //Set up renderer and tool tips for the access cells.
        DefaultTableCellRenderer renderer =
               new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        subjectColumn.setCellRenderer(renderer);
    }
    

    /* Workaround for BasicTableUI anomaly. Make sure the UI never tries to 
     * paint the editor. The UI currently uses different techniques to 
     * paint the renderers and editors and overriding setBounds() below 
     * is not the right thing to do for an editor. Returning -1 for the 
     * editing row in this case, ensures the editor is never painted. 
     */
    public int getEditingRow() 
    {
        return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1 : editingRow;  
    }

    // 
    // The renderer used to display the tree nodes, a JTree.  
    //

    public class TreeTableCellRenderer extends JTree implements TableCellRenderer 
    {

    	protected int visibleRow;
       
    	public TreeTableCellRenderer(TreeModel model) 
        { 
    	    super(model); 
    	}
    
    	public void setBounds(int x, int y, int w, int h) 
        {
    	    super.setBounds(x, 0, w, JTreeTable.this.getHeight());
    	}
    
    	public void paint(Graphics g) 
        {
    	    g.translate(0, -visibleRow * getRowHeight());
    	    super.paint(g);
    	}
    
    	public Component getTableCellRendererComponent(JTable table,
    						       Object value,
    						       boolean isSelected,
    						       boolean hasFocus,
    						       int row, int column) 
        {
    	    if(isSelected)
    	        setBackground(table.getSelectionBackground());
    	    else
    	        setBackground(table.getBackground());
           
    	    visibleRow = row;
    	    return this;
    	}
    }
    
    // 
    // The editor used to interact with tree nodes, a JTree.  
    //

    public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor 
    {
    	public Component getTableCellEditorComponent(JTable table, Object value,
    						     boolean isSelected, int r, int c) 
        {
            return tree;
    	}
    }

}


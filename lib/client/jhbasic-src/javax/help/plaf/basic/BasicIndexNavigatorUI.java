/*
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
/*
 * @(#) BasicIndexNavigatorUI.java 1.71 - last change made 04/26/01
 */

package javax.help.plaf.basic;

import javax.help.*;
import javax.help.plaf.HelpNavigatorUI;
import javax.help.plaf.HelpUI;
import javax.help.event.HelpModelListener;
import javax.help.event.HelpModelEvent;
import java.util.EventObject;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.help.Map.ID;
import java.text.Collator;
import java.text.RuleBasedCollator;

/**
 * The default UI for JHelpNavigator of type Index.
 *
 * @author Roger D. Brinkley
 *         revised by Paul Dumais, Nov 7, 1997
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @author Richard Gregor
 * @version   1.71     04/26/01
 */

public class BasicIndexNavigatorUI extends HelpNavigatorUI
             implements HelpModelListener, TreeSelectionListener,
                        PropertyChangeListener, ActionListener,
                        ComponentListener, Serializable
{
    protected JHelpIndexNavigator index;
    protected JScrollPane sp;
    protected DefaultMutableTreeNode topNode;
    protected JTree tree;
    protected JTextField searchField;
    protected RuleBasedCollator rbc;
    protected String oldText;
    protected DefaultMutableTreeNode currentFindNode;


    public static ComponentUI createUI(JComponent x) {
        return new BasicIndexNavigatorUI((JHelpIndexNavigator) x);
    }

    public BasicIndexNavigatorUI(JHelpIndexNavigator b) {
       ImageIcon icon = getImageIcon(b.getNavigatorView());
        if (icon != null)
            setIcon(icon);
        else
            setIcon(BasicHelpUI.getIcon(BasicIndexNavigatorUI.class, "images/index.gif"));
    }

    public void installUI(JComponent c) {
	debug ("installUI");

	index = (JHelpIndexNavigator)c;
	HelpModel model = index.getModel();

	index.setLayout(new BorderLayout());
	index.addPropertyChangeListener(this);
        index.addComponentListener(this);
	if (model != null) {
	    model.addHelpModelListener(this); // for our own changes
	}

	topNode = new DefaultMutableTreeNode();

	JLabel search = new JLabel(HelpUtilities.getString(HelpUtilities.getLocale(c),
							   "index.findLabel"));
	// should be a JButton
	//	search.addActionListener(this);
	searchField= new JTextField();
	search.setLabelFor(searchField);
	searchField.addActionListener(this);

	JPanel box = new JPanel();
	box.setLayout(new BoxLayout(box, BoxLayout.X_AXIS));
	box.add(search);
	box.add(searchField);
 
	index.add("North", box);

	tree = new JTree(topNode);
        TreeSelectionModel tsm = tree.getSelectionModel();
	tsm.addTreeSelectionListener(this);
        tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
           
	tree.setShowsRootHandles(true);
	tree.setRootVisible(false);

	setCellRenderer(index.getNavigatorView(), tree);

	sp = new JScrollPane();
	sp.getViewport().add(tree);

	index.add("Center", sp);
	reloadData();
    }

    /**
     * Sets the desired cell renderer on this tree.  This is exposed for redefinition
     * by subclases.
     */
    protected void setCellRenderer(NavigatorView view, JTree tree) {
	tree.setCellRenderer(new BasicIndexCellRenderer());
    }
    
    public void uninstallUI(JComponent c) {
	debug ("uninstallUI");
	HelpModel model = index.getModel();

        index.removeComponentListener(this);
	index.removePropertyChangeListener(this);
	TreeSelectionModel tsm = tree.getSelectionModel();
	tsm.removeTreeSelectionListener(this);
	index.setLayout(null);
	index.removeAll();

	if (model != null) {
	    model.removeHelpModelListener(this);
	}

	index = null;
    }

    public Dimension getPreferredSize(JComponent c) {
	/*
	if (sp != null) {
	    return ((ScrollPaneLayout)sp.getLayout()).preferredLayoutSize(sp);
	} else {
	    return new Dimension(200,100);
	}
	*/
	return new Dimension(200,100);
	
    }

    public Dimension getMinimumSize(JComponent c) {
	return new Dimension(100,100);
    }

    public Dimension getMaximumSize(JComponent c) {
	return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }

    private void reloadData() {
	debug("reloadData");

	if (index.getModel() == null) {
	    // HERE - do we need this test?  - epll
	    return;
	}

	// remove all children
	topNode.removeAllChildren();

	// parse the Index data into topNode
	IndexView view = (IndexView) index.getNavigatorView();

	if (view == null) {
	    return;
	}

	DefaultMutableTreeNode node = view.getDataAsTree();
        

	// This is a tricky one. As you remove the entries from one node to
	// another the list shrinks. So you can't use an Enumated list to do
	// the move.
	while (node.getChildCount() > 0) {
	    topNode.add((DefaultMutableTreeNode) node.getFirstChild());
	}
        

	// reload the tree data
	((DefaultTreeModel)tree.getModel()).reload();
 
        
	setVisibility(topNode);

	// Set the proper renderer -- needs to be done only once ? - epll
	//tree.setCellRenderer(new BasicIndexCellRenderer());
        
        

    }
    
    private void reloadData(HelpModel model) {
	debug("reloadData in using new model");

        	// remove all children
	topNode.removeAllChildren();

        HelpSet newHelpSet = model.getHelpSet();
        debug("New helpSet: "+newHelpSet.toString());
        
        IndexView indexView = (IndexView)newHelpSet.getNavigatorView("Index");
        if (indexView == null) {
            debug("view is null");
	    return;
	}

	DefaultMutableTreeNode node = indexView.getDataAsTree();

        debug("Node containts: "+node.toString());
	// This is a tricky one. As you remove the entries from one node to
	// another the list shrinks. So you can't use an Enumated list to do
	// the move.
	while (node.getChildCount() > 0) {
	    topNode.add((DefaultMutableTreeNode) node.getFirstChild());
	}

	// reload the tree data
	((DefaultTreeModel)tree.getModel()).reload();
	setVisibility(topNode);
	//setCellRenderer(view, tree);

         

    }
    
    /**
     * Expands entry path and entry itself( when entry is not empty) for specific id
     *
     * @param target The target of entry
     */
   
    private void expand(String target){
        debug("expand called");
        //find all nodes with certain id
        Enumeration nodes = findNodes(target).elements();
        DefaultMutableTreeNode node = null;
        
        while(nodes.hasMoreElements()){
            node = (DefaultMutableTreeNode)nodes.nextElement();
            debug("expandPath :"+node);
            if(node.getChildCount() > 0){
                DefaultMutableTreeNode child =(DefaultMutableTreeNode) node.getFirstChild();
                TreePath path = new TreePath(child.getPath());
                tree.makeVisible(path);
            }
            else{
                TreeNode[] treeNode = node.getPath();
                TreePath path = new TreePath(treeNode);
                //tree.scrollPathToVisible(path);
                tree.makeVisible(path);
            }
        }
    }
        

    
    /**
     * Returns all nodes with certain id
     *
     * @param target The target of entry
     *     
     */
    private Vector findNodes(String target){
        Enumeration nodes = topNode.preorderEnumeration();
        DefaultMutableTreeNode node = null;
        Vector nodeFound = new Vector();
        
        while(nodes.hasMoreElements()){
                node = (DefaultMutableTreeNode)nodes.nextElement();
                debug(" node :"+ node.toString());
                if(node != null){
                    IndexItem indexItem = (IndexItem)node.getUserObject();
                    if(indexItem == null)
                        debug("indexItem is null");
                    else{
                        Map.ID id = indexItem.getID();
                        if(id != null){
                            debug("id name :"+id.id);
                            debug("target :"+target);
                            Map.ID itemID = null;
                            try{
                                itemID = Map.ID.create(target,index.getModel().getHelpSet());
                            }
                            catch(BadIDException exp){
                                System.err.println("Not valid ID :"+target );
                                break;
                            }
                            if(id.equals(itemID))
                                nodeFound.addElement(node);
                        }
                    }
                }
        }
                                
        return nodeFound;
    }
    
    /**
     * Collapses entry specified by id. If entry is empty collapses it's parent.
     *
     * @param target The target of entry 
     */
      
    private void collapse(String target){
        Enumeration nodes = findNodes(target).elements();
        DefaultMutableTreeNode node = null;
        debug("collapse called");
        
        while(nodes.hasMoreElements()){
            node = (DefaultMutableTreeNode)nodes.nextElement();
            if(node.getChildCount() > 0){
                TreeNode[] treeNode = node.getPath();
                TreePath path = new TreePath(treeNode);
                tree.collapsePath(path);
                tree.collapseRow(tree.getRowForPath(path));
            }
            else{
                DefaultMutableTreeNode parent =(DefaultMutableTreeNode) node.getParent();
                TreePath path = new TreePath(parent.getPath());
                tree.collapseRow(tree.getRowForPath(path));
            }
        }
    }    


    /**
     * Merges in the navigational data from another TOCView.
     *
     * @param view A TOCView.  Note the actual argument is a NavigatorView type
     * so it replaces the correct NavigatorUI method.
     */

    public void merge(NavigatorView view) {
	debug("merging data");

	IndexView indexView = (IndexView) view;	// should succeed

	DefaultMutableTreeNode node = indexView.getDataAsTree();

	// This is a tricky one. As you remove the entries from one node to
	// another the list shrinks. So you can't use an Enumated list to do
	// the move.
	while (node.getChildCount() > 0) {
	    topNode.add((DefaultMutableTreeNode) node.getFirstChild());
	}

	// reload the tree data
	((DefaultTreeModel)tree.getModel()).reload(); 
	setVisibility(topNode);
    }

    /**
     * Removes the navigational data from another IndexView.
     *
     * @param view An IndexView.  Note the actual argument is a NavigatorView type
     * so it replaces the correct NavigatorUI method.
     */

    public void remove(NavigatorView view) {
	debug("removing "+view);

	for (Enumeration e = topNode.children();
	     e.hasMoreElements(); ) {
	    DefaultMutableTreeNode child
		= (DefaultMutableTreeNode) e.nextElement();
	    debug("  A child of topNode: "+child);
	}

	remove(topNode, view.getHelpSet());
	
	// reload the tree data
	((DefaultTreeModel)tree.getModel()).reload(); 
	setVisibility(topNode);
    }

    /**
     * Recursively removes all children of the node that have either hs or a HelpSet that
     * is included in hs as their HelpSet data.
     *
     * Recursion is stopped when a node is removed.  This is because of the
     * property of the merge mechanism.
     *
     * @param node The node from which to remove children.
     * @param hs The non-null HelpSet to use.
     */

    private void remove(DefaultMutableTreeNode node,
			HelpSet hs) {
	debug("remove("+node+", "+hs+")");

	// a simple node.children() does not work because the
	// enumeration is voided when a child is removed

	// getNextSibling() has a linear search, so we won't do that either

	// Collect all to be removed
	Vector toRemove = new Vector();
	
	for (Enumeration e = node.children();
	     e.hasMoreElements(); ) {
	    DefaultMutableTreeNode child
		= (DefaultMutableTreeNode) e.nextElement();
	    debug("  considering "+child);
	    IndexItem item = (IndexItem) child.getUserObject();
	    HelpSet chs = item.getHelpSet();
	    if (chs != null &&
		hs.contains(chs)) {
		debug("  tagging for removal: "+child);
		toRemove.addElement(child); // tag to be removed...
	    } else {
		remove(child, hs);
	    }
	}
	    
	// Now remove them
	for (int i=0; i<toRemove.size(); i++) {
	    debug("  removing "+toRemove.elementAt(i));
	    node.remove((DefaultMutableTreeNode) toRemove.elementAt(i));
	}
    }

    // Make all nodes visible

    private void setVisibility (DefaultMutableTreeNode node) {
	tree.expandPath(new TreePath(node.getPath()));
	if (! node.isLeaf()) {
	    int max = node.getChildCount();
	    for (int i=0; i<max; i++) {
		setVisibility((DefaultMutableTreeNode)node.getChildAt(i));
	    }
	}
    }

    // Process and idChanged event

    public void idChanged(HelpModelEvent e) {
	ID id = e.getID();
	HelpModel helpModel = index.getModel();
	debug("idChanged("+e+")");

	if (e.getSource() != helpModel) {
	    debug("Internal inconsistency!");
	    debug("  "+e.getSource()+" != "+helpModel);
	    throw new Error("Internal error");
	}

	if (id == null) {
	    //return;
	}
	TreePath s = tree.getSelectionPath();
	if (s != null) {
	    Object o = s.getLastPathComponent();
	    // should require only a TreeNode
	    if (o instanceof DefaultMutableTreeNode) {
		DefaultMutableTreeNode tn = (DefaultMutableTreeNode) o;
		IndexItem item = (IndexItem) tn.getUserObject();
		if (item != null) {
		    ID nId = item.getID();
		    if (nId != null && nId.equals(id)) {
			return;
		    }
		}
	    }
	}

	DefaultMutableTreeNode node = findID(topNode, id);
	if (node == null) {
	    // node doesn't exist. Need to clear the selection.
	    tree.clearSelection();
	    return;
	}
	TreePath path = new TreePath(node.getPath());
	tree.expandPath(path);
	tree.setSelectionPath(path);
	tree.scrollPathToVisible(path);
    }

    // Note - this recursive implementation may need tuning for very large Index - epll

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode node, ID id) {
	debug("findID: ("+id+")");
	debug("  node: "+node);

	// check on the id
	if (id == null) {
	    return null;
	}
	IndexItem item = (IndexItem) node.getUserObject();
	if (item != null) {
	    ID testID = item.getID();
	    debug("  testID: "+testID);
	    if (testID != null && testID.equals(id)) {
		return node;
	    }
	}
	int size = node.getChildCount();
	for (int i=0; i<size ; i++) {
	    DefaultMutableTreeNode tmp = 
		(DefaultMutableTreeNode) node.getChildAt(i);
	    DefaultMutableTreeNode test = findID(tmp, id);
	    if (test != null) {
		return test;
	    }
	}
	return null;
    }
		
    public void valueChanged(TreeSelectionEvent e) {
	TreePath path = tree.getSelectionPath();
	debug("valueChanged; path: "+path);

	// If the path is null the selection has been removed.
	if (path == null) {
	    return;
	}
	DefaultMutableTreeNode node = 
	    (DefaultMutableTreeNode) path.getLastPathComponent();

	IndexItem indexEl = (IndexItem) node.getUserObject();

	// need to do something for the applet version of this
	if (indexEl != null && indexEl.getID() != null) {
	    try {
		index.getModel().setCurrentID(indexEl.getID());
	    } catch (InvalidHelpSetContextException e2) {
		System.err.println ("BadID");
		return;
	    }
	}
    }

    public void propertyChange(PropertyChangeEvent event) {
	debug("propertyChange: " + event.getSource() + " "  +
	      event.getPropertyName());

	if (event.getSource() == index) {
	    String changeName = event.getPropertyName();
	    if (changeName.equals("helpModel")) {
                debug("model changed");
		reloadData((HelpModel)event.getNewValue());
                
            } else  if (changeName.equals("font")) {
		debug ("Font change");
		Font newFont = (Font)event.getNewValue();
		tree.setFont(newFont);
		RepaintManager.currentManager(tree).markCompletelyDirty(tree);
	    } else if(changeName.equals("expand")){
                debug("Expand change");
                expand((String)event.getNewValue());
            } else if(changeName.equals("collapse")){
                debug("Collapse change");
                collapse((String)event.getNewValue());
            }
	    // changes to UI property?
	}
    }

    /**
     * Invoked when the component's size changes.
     */
    public void componentResized(ComponentEvent e) {
    }
    
    /**
     * Invoked when the component's position changes.
     */
    public void componentMoved(ComponentEvent e) {
    }
    
    /**
     * Invoked when the component has been made visible.
     */
    public void componentShown(ComponentEvent e) {
        searchField.selectAll();
        searchField.requestFocus();
    }
    
    /**
     * Invoked when the component has been made invisible.
     */
    public void componentHidden(ComponentEvent e) {
    }
    
    /**
     *  Handles Action from the JTextField component for searching.
     */
    public void actionPerformed(ActionEvent evt) {
	if (evt.getSource()==searchField) {

	    // get a Collator based on the component locale
	    rbc = (RuleBasedCollator) Collator.getInstance(index.getLocale());
 
	    String text = searchField.getText();
	    if (text != null) {
		text = text.toLowerCase();
	    }
	    if (oldText != null && text.compareTo(oldText) != 0) {
		currentFindNode = null;
	    }
	    oldText = text;

	    // find the node in the tree
	    DefaultMutableTreeNode node = searchName(topNode, text);
	    if (node == null) {         
		currentFindNode = null;
		index.getToolkit().beep();
		return;
	    }
	    currentFindNode = node;
	    
	    //display it
	    TreePath path = new TreePath(node.getPath());
	    tree.scrollPathToVisible(path);
	    tree.expandPath(path);
	    tree.setSelectionPath(path);
  
	}
    }

    /**
     *  Searches in the tree for an index item with a name that starts with String name.
     *  Returns the node that contains the name,
     *  returns null if no node is found.
     */
    private DefaultMutableTreeNode searchName(DefaultMutableTreeNode node,
					  String name) {
	if (currentFindNode == null) {
	    IndexItem item = (IndexItem) node.getUserObject();
	    if (item!=null) {
		String itemName = item.getName();
		if (itemName !=null) {
		    itemName = itemName.toLowerCase();
		    // compare the Node with the Name
		    if (HelpUtilities.isStringInString(rbc, name, itemName)) {
			return node;
		    }
		}
	    }
	} else {
	    if (currentFindNode == node) {
		currentFindNode = null;
	    }
	}

	// travel the the rest of the tree
	int size = node.getChildCount();
	for (int i=0; i<size ; i++) {
	    DefaultMutableTreeNode tmp = 
		(DefaultMutableTreeNode) node.getChildAt(i);
	    DefaultMutableTreeNode test = searchName(tmp, name);
	    if (test != null) {		
		return test;
	    }
	}
	return null;
    }

    /**
     * For printf debugging.
     */
    private static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.out.println("BasicIndexNavigatorUI: " + str);
        }
    }
}

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
 * @(#) BasicTOCNavigatorUI.java 1.75 - last change made 04/26/01
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
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import javax.help.Map.ID;

/**
 * The default UI for JHelpNavigator of type TOC.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @author Richard Gregor
 * @version   1.75     04/26/01
 */
public class BasicTOCNavigatorUI extends HelpNavigatorUI
             implements HelpModelListener, TreeSelectionListener,
                        PropertyChangeListener, ComponentListener,
                        Serializable
{
    protected JHelpTOCNavigator toc;
    protected JScrollPane sp;
    protected DefaultMutableTreeNode topNode;
    protected JTree tree;

    public static ComponentUI createUI(JComponent x) {
        return new BasicTOCNavigatorUI((JHelpTOCNavigator) x);
    }
    
    public BasicTOCNavigatorUI(JHelpTOCNavigator b) {
	debug (this + " " + "CreateUI - sort of");
        ImageIcon icon = getImageIcon(b.getNavigatorView());
        if (icon != null)
            setIcon(icon);
        else
	    setIcon(BasicHelpUI.getIcon(BasicTOCNavigatorUI.class, "images/toc.gif"));
    }

    public void installUI(JComponent c) {
	debug (this + " " + "installUI");

	toc = (JHelpTOCNavigator)c;
	HelpModel model = toc.getModel();

	toc.setLayout(new BorderLayout());
	toc.addPropertyChangeListener(this);
        toc.addComponentListener(this);
	if (model != null) {
	    model.addHelpModelListener(this); // for our own changes
	}

	topNode = new DefaultMutableTreeNode();

	tree = new JTree(topNode);	
	TreeSelectionModel tsm = tree.getSelectionModel();
        tsm.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	tsm.addTreeSelectionListener(this);
        
	tree.setShowsRootHandles(false);
	tree.setRootVisible(false);
	sp = new JScrollPane();
	sp.getViewport().add(tree);
        
	toc.add("Center", sp);
	reloadData();
	debug("topTree is: "+topNode);
    }

    public void uninstallUI(JComponent c) {
	debug (this + " " + "unistallUI");
	HelpModel model = toc.getModel();

        toc.removeComponentListener(this);
	toc.removePropertyChangeListener(this);
	TreeSelectionModel tsm = tree.getSelectionModel();
	tsm.removeTreeSelectionListener(this);
	toc.setLayout(null);
	toc.removeAll();

	if (model != null) {
	    model.removeHelpModelListener(this);
	}

	toc = null;
    }

    public Dimension getPreferredSize(JComponent c) {
	/*
	if (sp != null) {
	    return ((ScrollPaneLayout)sp.getLayout()).preferredLayoutSize(sp);
	} else {
	    return new Dimension(200,100);
	}
	*/
	return new Dimension (200,100);
    }

    public Dimension getMinimumSize(JComponent c) {
	return new Dimension(100,100);
    }

    public Dimension getMaximumSize(JComponent c) {
	return new Dimension(Short.MAX_VALUE, Short.MAX_VALUE);
    }

    /**
     * Sets the desired cell renderer on this tree.  This is exposed for redefinition
     * by subclases.
     */
    protected void setCellRenderer(NavigatorView view, JTree tree) {
	// Use the combined map to drive the TOC tree actions
	Map map = view.getHelpSet().getCombinedMap();
	tree.setCellRenderer(new BasicTOCCellRenderer(map));
    }

    /**
     * Reloads the presentation data.
     */
    private void reloadData() {
	debug("reloadData");

        
	if (toc.getModel() == null) {
	// HERE - check why we need the test for null - epll
            debug("model is null");
	    return;
	}

	// remove all children
	topNode.removeAllChildren(); 

	// parse the TOC data into topNode
	TOCView view = (TOCView) toc.getNavigatorView();
        

	if (view == null) {
            debug("view is null");
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


	setCellRenderer(view, tree);

	// HERE - need to reload the merged info - epll
    }
    /**
     *Reloads the presentation data using new help model
     **/
    private void reloadData(HelpModel model) {
	debug("reloadData using new model");

         HelpSet newHelpSet = model.getHelpSet();
        
        TOCView view = (TOCView)newHelpSet.getNavigatorView("TOC");
        
	// remove all children
	topNode.removeAllChildren(); 

	// parse the TOC data into topNode
	//TOCView view = (TOCView) toc.getNavigatorView();
        

	if (view == null) {
            debug("view is null");
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
  
         
	setCellRenderer(view, tree);

	// HERE - need to reload the merged info - epll
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
                    TOCItem tocItem = (TOCItem)node.getUserObject();
                    if(tocItem == null)
                        debug("tocItem is null");
                    else{
                        Map.ID id = tocItem.getID();
                        if(id != null){
                            debug("id name :"+id.id);
                            debug("target :"+target);
                            Map.ID itemID = null;
                            try{
                                itemID = Map.ID.create(target,toc.getModel().getHelpSet());
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
     * Collapses entry specified by id. If entry is empty collapses its parent.
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
     * @param view A TOCView.  Note the actual argument is of a NavigatorView type
     * so it replaces the correct NavigatorUI method.
     */

    public void merge(NavigatorView view) {
	debug("merging "+view);

	TOCView tocView = (TOCView) view; // should succeed.

	// parse TOC data
	DefaultMutableTreeNode node = tocView.getDataAsTree();

	// This is a tricky one. As you remove the entries from one node to
	// another the list shrinks. So you can't use an Enumated list to do
	// the move.
	while (node.getChildCount() > 0) {
	    topNode.add((DefaultMutableTreeNode) node.getFirstChild());
	}

	// reload the tree data
	((DefaultTreeModel)tree.getModel()).reload(); 
	setVisibility(topNode);

	setCellRenderer(tocView, tree);
    }

    /**
     * Removes the navigational data.
     *
     * @param view A TOCView.  Note the actual argument is of a NavigatorView type
     * so it replaces the correct NavigatorUI method.
     */

    public void remove(NavigatorView view) {
	debug("removing "+view);

	remove(topNode, view.getHelpSet());
	
	// reload the tree data
	((DefaultTreeModel)tree.getModel()).reload(); 
	setVisibility(topNode);
	
	setCellRenderer(view, tree);
    }

    /**
     * Recursively removes all children of the node that have either hs or a HelpSet that
     * is included in hs as their HelpSet data.
     *
     * Recursion is stopped when a node is removed.  This is because of the
     * property of the merge mechanism.
     *
     * @param node The node from where to remove its children
     * @param hs The non-null helpset to use
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
	    TOCItem item = (TOCItem) child.getUserObject();
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


    // In the TOC only set the visibility at the next to the top node
    private void setVisibility (DefaultMutableTreeNode node) {
	int max = node.getChildCount();
	for (int i=0; i<max; i++) {
	    DefaultMutableTreeNode subnode = 
		(DefaultMutableTreeNode)node.getChildAt(i);
	    tree.expandPath(new TreePath(subnode.getPath()));
	}
    }

    /**
     * Processes an idChanged event.
     */

    public void idChanged(HelpModelEvent e) {
 	ID id = e.getID();
	URL url = e.getURL();
	HelpModel helpModel = toc.getModel();
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
		TOCItem item = (TOCItem) tn.getUserObject();
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

    // Note - this recursive implementation may need tuning for very large TOC - epll

    private DefaultMutableTreeNode findID(DefaultMutableTreeNode node, ID id) {
	debug("findID: ("+id+")");
	debug("  node: "+node);

	// check on the ID
	if (id == null) {
	    return null;
	}
	TOCItem item = (TOCItem) node.getUserObject();
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
	debug("ValueChanged: "+e);
	debug("  model: "+toc.getModel());
        
        
        TreePath path = tree.getSelectionPath();
	// If the path is null then the selection has been removed.
	if (path == null) {
            debug("path is null");
	    return;
	}
	DefaultMutableTreeNode node = 
	    (DefaultMutableTreeNode) path.getLastPathComponent();
	
	TOCItem tocEl = (TOCItem) node.getUserObject();

	if (tocEl != null && tocEl.getID() != null) {
	    try {
		toc.getModel().setCurrentID(tocEl.getID());
	    } catch (InvalidHelpSetContextException e2) {
		System.err.println (this + " " + "BadID");
		return;
	    }
	}
    }

    public void propertyChange(PropertyChangeEvent event) {
	debug(this + " " + "propertyChange: " + event.getSource() + " "  +
	      event.getPropertyName());

	if (event.getSource() == toc) {
	    String changeName = event.getPropertyName();
	    if (changeName.equals("helpModel")) {
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
        tree.requestFocus();
    }
    
    /**
     * Invoked when the component has been made invisible.
     */
    public void componentHidden(ComponentEvent e) {
    }
    
    /**
     * For printf debugging.
     */
    protected static final boolean debug = false;
    protected static void debug(String str) {
        if (debug) {
            System.out.println("BasicTOCNavigatorUI: " + str);
        }
    }
}

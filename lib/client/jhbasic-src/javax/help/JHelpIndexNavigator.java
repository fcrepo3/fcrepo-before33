/*
 * @(#) JHelpIndexNavigator.java 1.39 - last change made 04/26/01
 *
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

package javax.help;

import java.net.URL;
import javax.help.plaf.HelpNavigatorUI;

/**
 * JHelpIndexNavigator is a JHelpNavigator for an Index.
 *
 * All the tree navigation and selection has been delegated to the UI
 * where the JTree is created.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.39	04/26/01
 */

public class JHelpIndexNavigator extends JHelpNavigator {
    /**
     * Creates an Index navigator.
     *
    * @param view The NavigatorView
     */
    public JHelpIndexNavigator(NavigatorView view) {
       super(view, null);
    }

    /**
     * Creates a Index navigator.
     *
    * @param view The NavigatorView.
    * @param model The model for the Navigator.
     */
    public JHelpIndexNavigator(NavigatorView view, HelpModel model) {
	super(view, model);
    }
  

    
    // HERE - label & Locale?
    // HERE - URL data vs Hashtable?
    /**
     * Creates an Index navigator with explicit arguments.  Note that this should not throw
     * an InvalidNavigatorViewException since we are implicitly passing the type.
     *
    * @param hs HelpSet
    * @param name The name identifying this HelpSet.
    * @param label The label to use (for this locale).
    * @param data The "data" part of the parameters, a URL location of the TOC data.
     */
    public JHelpIndexNavigator(HelpSet hs,
			       String name, String label, URL data)
	throws InvalidNavigatorViewException
    {
	super(new IndexView(hs, name, label, createParams(data)));
    }

    /**
     * Gets the UID for this JComponent.
     */
    public String getUIClassID() {
	return "HelpIndexNavigatorUI";
    }

    /**
     * Determines if this instance of a JHelpNavigator can merge its data with another one.
     *
     * @param view The data to merge
     * @return Whether it can be merged
     *
     * @see merge(NavigatorView)
     * @see remove(NavigatorView)
     */
    public boolean canMerge(NavigatorView view) {
	if (view instanceof IndexView &&
	    getNavigatorName().equals(view.getName())) {
	    return true;
	}
	return false;
    }

    /**
     * Merges some NavigatorView into this instance.
     *
     * @param view The data to merge
     * @exception IllegalArgumentException
     * @exception IllegalStateException
     *
     * @see canMerge(NavigatorView)
     * @see remove(NavigatorView)
     */
    public void merge(NavigatorView view) {
	debug("merge of: "+view);
	this.getUI().merge(view);
    }

    /**
     * Removes a NavigatorView from this instance.
     *
     * @param view The data to merge
     * @exception IllegalArgumentException
     * @exception IllegalStateException
     *
     * @see canMerge(NavigatorView)
     * @see merge(NavigatorView)
     */
    public void remove(NavigatorView view) {
	this.getUI().remove(view);
    }
    
    /**
     * Sets state of navigation entry for given target to expanded. Non-empty entry is expanded. Empty entry is visible. 
     *
     * @param target The target to expand
     */
     public void expandID(String target){
         firePropertyChange("expand"," ",target);
    }
    
    /**
     * Sets state of navigation entry for given target to collapsed if entry is visible. Parent is collapsed if entry is empty.
     * 
     * @param target The target to collapse
     */
    public void collapseID(String target){
        firePropertyChange("collapse"," ",target);
    }    

    private static final boolean debug = false;
    private static void debug(String msg) {
  	if (debug) {
  	    System.err.println("JHelpIndexNavigator: "+msg);
	}
    }
}

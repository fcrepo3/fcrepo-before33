/*
 * @(#) JHelpSearchNavigator.java 1.51 - last change made 04/11/01
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
import javax.help.search.SearchEngine;
import javax.help.search.MergingSearchEngine;

/**
 * A JHelpNavigator for search data.
 * All of the tree navigation and selection has been delegated to the UI.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.51	04/11/01
 */

public class JHelpSearchNavigator extends JHelpNavigator {
    private MergingSearchEngine search;	// our search engine


    /**
     * Creates a Search navigator
     *
     * @param view The NavigatorView. If view is null it creates a JHelpSearchNavigator
     * with a null NavigatorView.
     */
    public JHelpSearchNavigator(NavigatorView view) {
	super(view, null);
    }

    /**
     * Creates a Search navigator.
     *
     * @param view The NavigatorView. If <tt>view</tt> is null it creates a JHelpSearchNavigator
     * with a null NavigatorView.
     * @param model The HelpModel this Navigator is presenting. If <tt>model</tt> is null it 
     * creates a JHelpSearchNavigator witout a model.
     */
    public JHelpSearchNavigator(NavigatorView view, HelpModel model) {
	super(view, model);
    }
    


    // HERE - label & Locale?
    /**
    * Creates a TOC navigator with explicit arguments.  Note that this should not throw
    * an InvalidNavigatorViewException since the type is passed implicitly.
    *
    * @param hs HelpSet
    * @param name The name indentifying this HelpSet.
    * @param label The label to use (for this locale).
    * @param data The "data" part of the parameters, a URL to the location of the TOC data.
    */
    public JHelpSearchNavigator(HelpSet hs,
				String name, String label, URL data) 
	throws InvalidNavigatorViewException
    {
	super(new SearchView(hs, name, label, createParams(data)));
    }


    /**
     * The UID for this JComponent.
     */
    public String getUIClassID() {
	return "HelpSearchNavigatorUI";
    }

    /**
     * Search Database methods.
     */

    /**
     * Instantiates and returns a SearchEngine class. 
     * The default query engine to use is <tt>com.sun.java.help.search.SearchEngine</tt>,
     * but this can be changed through the &lt;engine&gt;&lt;/engine&gt; attribute
     * of the view.
     *
     * @return The SearchEngine instantiation.
     */
    public SearchEngine getSearchEngine() {
	if (search == null) {
	    search = new MergingSearchEngine(getNavigatorView());
	}
	return search;
    }

    /**
     * Explicitly changes the default (overriding what is in the HelpSet).
     *
     * @param search A SearchEngine instantiation.
     */
    public void setSearchEngine(SearchEngine search) {
	this.search = new MergingSearchEngine(search);
    }

    /**
     * Default for the search engine.
     */

    protected String getDefaultQueryEngine() {
	return HelpUtilities.getDefaultQueryEngine();
    }

    /**
     * Determines if this instance of a JHelpNavigator can merge its data with another one.
     *
     * @param view The data to merge.
     * @return Whether it can be merged.
     *
     * @see merge(NavigatorView)
     * @see remove(NavigatorView)
     */
    public boolean canMerge(NavigatorView view) {
	if (view instanceof SearchView &&
	    getNavigatorName().equals(view.getName())) {
	    debug("canMerge: true");
	    return true;
	}
	debug("canMerge: false");
	return false;
    }

    /**
     * Merges a NavigatorView into this instance.
     *
     * @param view The data to merge.
     * @exception IllegalArgumentException
     * @exception IllegalStateException
     *
     * @see canMerge(NavigatorView)
     * @see remove(NavigatorView)
     */
    public void merge(NavigatorView view) {
	// Add the requested query engine to our list of engines
	debug("JHelpSearchNavigator.merge invoked");
	debug("  params: "+view.getParameters());
	if (search == null) {
	    search = (MergingSearchEngine) getSearchEngine();
	}
	search.merge(view);
	debug("merge: "+view);
	this.getUI().merge(view);
    }

    /**
     * Removes a NavigatorView from this instance.
     *
     * @param view The data to merge.
     * @exception IllegalArgumentException
     * @exception IllegalStateException
     *
     * @see canMerge(NavigatorView)
     * @see merge(NavigatorView)
     */
    public void remove(NavigatorView view) {
	// Remove the requested query engine from our list of engines
	debug("JHelpSearchNavigator.remove invoked");
	debug("  params: "+view.getParameters());
	if (search == null) {
	    search = (MergingSearchEngine) getSearchEngine();
	}
	search.remove(view);
	debug("remove: "+view);
	this.getUI().remove(view);
    }

    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("JHelpSearchNavigator: "+msg);
	}
    }


}


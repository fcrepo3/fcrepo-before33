/*
 * @(#)SearchQuery.java	1.8 04/07/99
 *
 * Copyright (c) 1994-1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DyAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

package javax.help.search;

import javax.help.event.EventListenerList;
import javax.help.search.SearchEvent;
import javax.help.search.SearchListener;
import java.net.URL;
import java.util.Vector;
import java.util.Locale;
import java.util.Enumeration;

/**
 * The instance of a query on a search database. It is 
 * instantiated by SearchEngine.startQuery.
 *
 * Subclasses of SearchQuery can perform the search or negotiate the search
 * results with an outside agent as setup in the SearchEngine class. A server
 * search engine is an an example of an outside agent. 
 *
 * Search results are returned through SearchEvents to listeners that
 * register with a SearchEngine instance.
 *
 * @author Roger D. Brinkley
 * @version	1.22	09/16/98
 *
 * @see javax.help.search.SearchEvent
 * @see javax.help.search.SearchListener
 */

public abstract class SearchQuery {

    protected EventListenerList listenerList = new EventListenerList();
    protected SearchEngine hs;
    protected String searchparams;
    protected Locale l;

    /**
     * Creates a SearchQuery.
     */
    public SearchQuery(SearchEngine hs) {
	this.hs = hs;
    }

    /**
     * Adds a listener for the SearchEngine posted after the search has
     * started, stopped, or search parameters have been defined.
     * 
     * @param l The listener to add.
     * @see java.javahelp.SearchEngine#removeSearchListener
     */
    public void addSearchListener(SearchListener l) {
	listenerList.add(SearchListener.class, l);
    }

    /**
     * Removes a listener previously added with <tt>addSearchListener</tt>.
     *
     * @param l The listener to remove.
     * @see java.javahelp.SearchEngine#addSearchListener
     */
    public void removeSearchListener(SearchListener l) {
	listenerList.remove(SearchListener.class, l);
    }

    /**
     * Starts the search. This method invokes searchStarted on 
     * SearchListeners and stores the searchparams. Extensions
     * of SearchQuery should fully implement this method according
     * to the needs of the SearchQuery and its corresponding SearchEngine.
     * 
     *
     * @param searchparams The search string.
     * @param locale The locale of the search string.
     * @exception IllegalArgumentException The parameters are not 
     * understood by this engine.
     * @exception IllegalStateException There is an active search in 
     * progress in this instance.
     */
    public void start(String searchparams, Locale l)
	throws IllegalArgumentException, IllegalStateException
    {
	this.searchparams = searchparams;
	this.l = l;
	fireSearchStarted();
    }

    /**
     * Stops the search. This method invokes searchStopped on 
     * SearchListeners. Extensions of 
     * SearchQuery should fully implement this method according to needs
     * of the SearchQuery and its corresponding SearchEngine. 
     *
     * @exception IllegalStateException The search engine is not in a state in which it can be started.
     */
    public void stop() throws IllegalStateException {
	fireSearchFinished();
    }

    /**
     * Returns the SearchEngine associated with this SearchQuery.
     */
    public SearchEngine getSearchEngine() {
	return hs;
    }

    /**
     * Determines if this SearchQuery is active.
     *
     * @returns True if active, false otherwise
     */
    public abstract boolean isActive();

    /**
     * Notifies that query of items is found in the search.
     *
     * @param docs A vector of SearchItem.
     * @param inSearch Is the search completed?
     */
    public void itemsFound(boolean inSearch, Vector docs) {
	fireItemsFound(inSearch, docs);
    }

    /**
     * Notifies that a SearchItem has been found.
     *
     * @param params The parameters to the search.
     * @param inSearch Is the search completed?
     * @param docs A vector of SearchItem.
     * @see javax.help.search.SearchItem
     */
    protected void fireItemsFound(boolean inSearch, Vector docs) {
	debug("fireItemsFound");
	debug("  params: " + searchparams);
	debug("  insearch: " + inSearch);
	debug("  docs: " + docs);
	Object[] listeners = listenerList.getListenerList();
	SearchEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == SearchListener.class) {
		if (e == null) {
		    e = new SearchEvent(this, searchparams,
					    inSearch, docs);
		}
		((SearchListener)listeners[i+1]).itemsFound(e);
	    }	       
	}
    }

    /**
     * Passs through that a SearchEvent has happened.
     * This is useful for SearchEngine engines that encapsulate others.
     *
     * @param e The SearchEvent to pass through.
     */
    protected void fireItemsFound(SearchEvent e) {
	Object[] listeners = listenerList.getListenerList();

	Vector newItems = new Vector();
	for (Enumeration enum = e.getSearchItems();
	     enum.hasMoreElements(); ) {
	    newItems.addElement((SearchItem) enum.nextElement());
	}

	SearchEvent e2 = new SearchEvent(this, e.getParams(), 
						 e.isSearchCompleted(),
						 newItems);
	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == SearchListener.class) {
		((SearchListener)listeners[i+1]).itemsFound(e2);
	    }	       
	}
    }

    /**
     * Notifies that a search has started.
     *
     * @param params The parameters to the search.
     */
    protected void fireSearchStarted() {
	debug("fireSearchStarted");
	Object[] listeners = listenerList.getListenerList();
	SearchEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == SearchListener.class) {
		if (e == null) {
		    e = new SearchEvent(this, searchparams, true);
		}
		((SearchListener)listeners[i+1]).searchStarted(e);
	    }	       
	}
    }

    /**
     * Notifies that a search has completed.
     *
     * @param params The parameters to the search.
     */
    protected void fireSearchFinished() {
	debug("fireSearchFinished");
	Object[] listeners = listenerList.getListenerList();
	SearchEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == SearchListener.class) {
		if (e == null) {
		    e = new SearchEvent(this, searchparams, false);
		}
		((SearchListener)listeners[i+1]).searchFinished(e);
	    }	       
	}
    }
    
    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("SearchQuery: "+msg);
	}
    }
}

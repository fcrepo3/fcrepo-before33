/*
 * @(#) MergingSearchEngine.java 1.2 - last change made 04/10/01
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
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
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

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Locale;
import java.net.URL;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.NavigatorView;
import javax.help.search.SearchListener;
import javax.help.search.SearchEvent;
import javax.help.search.SearchEngine;
import javax.help.search.SearchQuery;

/*
 * A class that provides a merging/removing layer for the search.
 */
public class MergingSearchEngine extends SearchEngine {
    
    private Vector engines;
    private Hashtable enginePerView = new Hashtable();

    public MergingSearchEngine(NavigatorView view) {
	engines = new Vector();
	// HERE - the makeEngine() should be delayed until the actual query
	SearchEngine engine = makeEngine(view);
	engines.addElement(engine);
    }
	
    public MergingSearchEngine(SearchEngine engine) {
	engines = new Vector();
	engines.addElement(engine);
    }

    /**
     * Creates the query for this helpset.
     */
    public SearchQuery createQuery() {
	return new MergingSearchQuery(this);
    }

    /**
     * Adds/Removes a Search Engine to/from list.
     *
     * Possibly the makeEngine should be delayed until the actual query.
     */

    public void merge(NavigatorView view) {
	SearchEngine engine = makeEngine(view);
	if (engine == null) {
	    return;
	}
	engines.addElement(engine);
	enginePerView.put(view, engine);
    }

    public void remove(NavigatorView view) {
	SearchEngine engine = (SearchEngine) enginePerView.get(view);
	if (engine != null) {
	    engines.removeElement(engine);
	    enginePerView.remove(engine);
	}
    }

    public Enumeration getEngines() {
	return engines.elements();
    }

    private SearchEngine makeEngine(NavigatorView view) {
	Hashtable params = view.getParameters();

	// if there were no parameters or there were parameters but
	// no data then return a null SearchEngine
	if (params == null || 
	    (params != null && !params.containsKey("data"))) {
	    return null;
	}
	String engineName = (String) params.get("engine");
	HelpSet hs = view.getHelpSet();
	URL base = hs.getHelpSetURL();
	ClassLoader loader = hs.getLoader();

	if (engineName == null) {
	    engineName = HelpUtilities.getDefaultQueryEngine();
	    params.put("engine", engineName);
	}
	
	SearchEngine back = null;

	Constructor konstructor;
	Class types[] = {URL.class, Hashtable.class};
	Object args[] = {base, params};
	Class klass;

	debug("makeEngine");
	debug("  base: "+base);
	debug("  params: "+params);

	try {
	    if (loader == null) {
		klass = Class.forName(engineName);
	    } else {
		klass = loader.loadClass(engineName);
	    }
	} catch (Throwable t) {
	    throw new Error("Could not load engine named "+engineName+" for view: "+view);
	}

	try {
	    konstructor = klass.getConstructor(types);
	} catch (Throwable t) {
	    throw new Error("Could not find constructor for "+engineName+". For view: "+view);
	}
	try {
	    back = (SearchEngine) konstructor.newInstance(args);
	} catch (InvocationTargetException e) {
            System.err.println("Exception while creating engine named "+engineName+" for view: "+view);
            e.printStackTrace();
            System.exit(1); 
	} catch (Throwable t) {
	    throw new Error("Could not create engine named "+engineName+" for view: "+view);
	}
	return back;
    }

    private class MergingSearchQuery extends SearchQuery implements SearchListener {

	private MergingSearchEngine mhs;
	private Vector queries;
	private String searchparams;

	public MergingSearchQuery(SearchEngine hs) {
	    super(hs);
	    if (hs instanceof MergingSearchEngine) {
		this.mhs = (MergingSearchEngine) hs;
	    }
	}

	// Start all the search engines
	public synchronized void start(String searchparams, Locale l)
	    throws IllegalArgumentException, IllegalStateException
	{
	    MergingSearchEngine.this.debug("startSearch()");

	    // if we're already alive you can't start again
	    if (isActive()) {
		throw new IllegalStateException();
	    }

	    // setup everthing to get started
	    super.start(searchparams, l);
	    queries = new Vector();
		
		// Get a query for each engine
	    for (Enumeration e = mhs.getEngines();
		 e.hasMoreElements(); ) {
		SearchEngine engine = (SearchEngine) e.nextElement();
		if (engine != null) {
		    queries.addElement(engine.createQuery());
		}
	    }
		
	    // Set the listener to this class and start the query
	    for (Enumeration e = queries.elements(); e.hasMoreElements(); ) {
		SearchQuery query = (SearchQuery) e.nextElement();
		query.addSearchListener(this);
		query.start(searchparams, l);
	    }
	}

	// Stop all the search engines
	// This is an override of the SearchQuery.stop
	// Donnot call super.stop in this method as an
	// extra fireSearchStopped will be genertated
	public synchronized void stop() throws IllegalStateException {
	    // Can't stop what is already stopped silly
	    if (queries == null) {
		return;
	    }

	    /*
	    // Loop through all the queries and stop them on a per SearchQuery
	    for (Enumeration e = queries.elements();
		 e.hasMoreElements(); ) {
		SearchQuery query = (SearchQuery) e.nextElement();
		try {
		    query.stop();
		} 
	    }
	    */
	    // we use to stop the queries but now just let them run because of
	    // the problems with Thread.stop.

	    queries = null;
	}

	public boolean isActive() {

	    // if there aren't any queries we aren't alive
	    if (queries == null) {
		return false;
	    }

	    // Loop through all the queries and see if anyone is alive
	    for (Enumeration e = queries.elements();
		 e.hasMoreElements(); ) {
		SearchQuery query = (SearchQuery) e.nextElement();
		if (query.isActive()) {
		    return true;
		}
	    }

	    // Didn't find anyone alive so we're not alive
	    return false;
	}

	public SearchEngine getSearchEngine() {
	    return mhs;
	}

	public synchronized void itemsFound(SearchEvent e) {
	    SearchQuery queryin = (SearchQuery) e.getSource();

	    // Loop through all the queries and match this one
	    if (queries != null) {
		Enumeration enum = queries.elements();
		while (enum.hasMoreElements()) {
		    SearchQuery query = (SearchQuery) enum.nextElement();
		    if (query == queryin) {
			// Redirect any Events as if they were from me
			fireItemsFound(e);
		    }
		}
	    }
	}

	public void searchStarted(SearchEvent e) {
	    // Ignore these events as this class already informed
	    // the listeners the search was started so we don't have 
	    // to do anything else
	}

	public synchronized void searchFinished(SearchEvent e) {
	    SearchQuery queryin = (SearchQuery) e.getSource();
		
	    // Loop through all the queries and match this one
	    if (queries != null) {
		Enumeration enum = queries.elements();
		while (enum.hasMoreElements()) {
		    SearchQuery query = (SearchQuery) enum.nextElement();
		    if (query == queryin) {
			queryin.removeSearchListener(this);
			queries.removeElement(query);
		    }
		}
		// If all the queries are done then send a searchFinished
		if (queries.isEmpty()) {
		    queries = null;
		    fireSearchFinished();
		}
	    }
		
	}

    }    // This needs to be public to deal with inner classes...

    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("MergineSearchEngine: "+msg);
	}
    }

}

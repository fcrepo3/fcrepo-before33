/*
 * @(#)SearchEngine.java	1.6 03/19/99
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

import javax.help.event.EventListenerList;
import javax.help.search.SearchEvent;
import javax.help.search.SearchListener;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Locale;
import java.security.InvalidParameterException;

/**
 * Defines the methods used to access a search engine.
 * Each instance is created by a engine factory.
 * 
 * Extensions of SearchEngine can perform the search or negotiate the search
 * results with an outside agent. A server search engine is an an example
 * of an outside agent.
 *
 * Search results are returned through SearchEvents to listeners that
 * register with a SearchQuery instance. The SearchQuery
 * is returned from the method createQuery.
 *
 * @author Roger D. Brinkley
 * @version	1.6	03/19/99
 *
 * @see javax.help.search.SearchEvent
 * @see javax.help.search.SearchListener
 */

public abstract class SearchEngine {

    protected URL base;		// the base for resolving URLs against
    protected Hashtable params;	// other parameters to the engine

    /**
     * Creates a SearchEngine using the standard JavaHelp SearchEngine
     * parameters. Only this constructor is used to create a SearchEngine
     * from within a search view.
     *
     * @param base The base address of the data.
     * @param params A hashtable of parameters from the search view.
     */
    public SearchEngine(URL base, Hashtable params) 
	throws InvalidParameterException
    {
	this.base = base;
	this.params = params;
    }

    /**
     * Creates a SearchEngine.
     */
    public SearchEngine() {
    }

    /**
     * Creates a new search query.
     */
    public abstract SearchQuery createQuery() throws IllegalStateException;

    private static final boolean debug = false;
    private static void debug(String msg) {
	if (debug) {
	    System.err.println("SearchEngine: "+msg);
	}
    }
}

/*
 * @(#)SearchEvent.java	1.16 03/19/99
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

import java.util.Vector;
import java.util.Enumeration;

/**
 * Encapsulates information that describes changes to a SearchQuery.  It is used
 * to notify listeners of the change.
 *
 * @author Roger D. Brinkley
 * @version	1.16	03/19/99
 */

public class SearchEvent extends java.util.EventObject
{
    private String params;
    private boolean searching;
    private Vector items;

    /**
     * Represents a change in the SearchEngine. Used for starting the search
     * or ending the search.
     *
     * @param params The search parameters.
     * @param searching A boolean operator that indicates if searching is 
     * executing (true) or stopped (false).
     */
    public SearchEvent(Object source, String params, boolean searching) {
	super (source);
	this.params = params;
	this.searching = searching;
    }

    /**
     * Represents a change in the SearchEngine. Used to indicate that either a single
     * item or a group of items have matched the params.
     *
     * @param params The search parameters.
     * @param searching A boolean operator that indicates if a search is 
     * executing (true) or stopped (false).
     * @param items A Vector of SearchItems matching the the search params.
     *
     * @see java.javahelp.SearchItems
     */
    public SearchEvent(Object source, String params, boolean searching, Vector items) {
	super(source);
	this.params = params;
	this.searching = searching;
	this.items = items;
    }


    /**
     * Returns the parameters to the query.
     */
    public String getParams() {
	return params;
    }

    /**
     * A boolean value that indicates if the search is completed.
     */
    public boolean isSearchCompleted() {
	return searching;
    }

    /**
     * An enumerated list of SearchItems that match parameters of the query.
     */
    public Enumeration getSearchItems() {
	return items.elements();
    }
}

/*
 * @(#) Map.java 1.7 - last change made 01/29/99
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
import java.net.MalformedURLException;
import java.util.*;

/**
 * A Map is the interface to ID<->URL mapping.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.7	01/29/99
 */
public interface Map {
    /**
     * Determines if the ID is valid (defined in the map file).
     * 
     * @param id The String ID.
     * @param hs The HelpSet against which to resolve the string.
     * @return True if the ID is valid, false if not valid.
     */

    public boolean isValidID(String id, HelpSet hs);

    /**
     * Gets an enumeration of all the IDs in a Map.
     *
     * @return An enumeration of all the IDs in a Map.
     */
    // We need this so we can add FlatMaps to TryMaps - epll
    public Enumeration getAllIDs();

    /**
     * Gets the URL that corresponds to a given ID in the Map.
     *
     * @param id The ID to get the URL for.
     * @return URL The matching URL.  Null if this map cannot solve the ID.
     * @exception MalformedURLException if the URL is malformed
     */
    public URL getURLFromID(ID id) throws MalformedURLException;

    /**
     * Determines if the URL corresponds to an ID in the Map.
     *
     * @param url The URL to check on.
     * @return True if this is an ID, false otherwise.
     */
    public boolean isID(URL url);

    /**
     * Determines the ID for this URL.
     * 
     * @param url The URL to get the ID for.
     * @return The ID (or null if URL does not correspond to an ID).
     */
    public ID getIDFromURL(URL url);

    // HERE - do we want this.  It is *very* expensive to do  - epll

    /**
     * Determines the ID that is "closest" to this URL (with a given anchor).
     *
     * The definition of this is up to the implementation of Map.  In particular,
     * it may be the same as getIDFromURL().
     *
     * @param url A URL
     * @return The closest ID in this map to the given URL.
     */
    public ID getClosestID(URL url);

    /**
     * Determines the IDs related to this URL.
     *
     * @param URL The URL to which to compare the Map IDs.
     * @return Enumeration of Map.Key (Strings/HelpSet)
     */
    public Enumeration getIDs(URL url);

    /**
     * An ID is a pair of String, HelpSet.
     *
     * An ID fully identifies a "location" within a HelpSet.
     */
    
    final public class ID {
	public String id;
	public HelpSet hs;
	
	/**
	 * A location within a HelpSet.  If id or hs are null, a null ID is returned.
	 *
	 * @param id The String
	 * @param hs The HelpSet
	 * @exception BadIDException if String is not within the Map of the
	 * HelpSet.
	 */

	public static ID create(String id, HelpSet hs) throws BadIDException {
	    if (hs == null ||
		id == null) {
		return null;
	    }

	    Map map = hs.getCombinedMap();
	    if (! map.isValidID(id, hs)) {
		throw new BadIDException("Not valid ID", map, id, hs);
	    }
	    return new ID(id, hs);
	}

	/**
	 * Creates an ID object.
	 */

	private ID(String id, HelpSet hs) throws BadIDException {
	    this.id = id;
	    this.hs = hs;
	}

	/**
	 * Determines if two IDs are equal.
	 * @param o The object to compare.
	 */

	public boolean equals(Object o) {
	    if (o instanceof ID) {
		ID id2 = (ID) o;
		return (id2.id.equals(id) && (id2.hs.equals(hs)));
	    }
	    return false;
	}

	/**
	 * Gets an external represenation of an ID.
	 */
	public String toString() {
	    return("ID: "+id+", "+hs);
	}
    }
}

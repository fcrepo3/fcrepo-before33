/*
 * @(#)SearchItem.java	1.20 04/07/99
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
import java.net.URL;

/**
 * A SearchItem corresponds to one specific item found in a search query.
 * SearchItems are used in the default Search navigator.
 * 
 * @see javax.help.SearchTOCItem
 * @see javax.help.search.SearchEvent
 * @see javax.help.search.SearchListener
 *
 * @author Roger D. Brinkley
 * @version	1.20	04/07/99
 */

public class SearchItem { 
    private URL base;
    private String title;
    private String lang;
    private String filename;
    private double confidence; 
    private int begin;
    private int end;
    private Vector concepts;

    /**
     * Constructs a SearchItem
     *
     * @param base The URL to the base from which file is a spec.
     * @param title Title of the item
     * @param lang A string representation of the locale. A null lang is valid
     * and represents the default locale.
     * @param filename FileName for the item.
     * @param confidence How closely this matches the params.
     * @param begin Starting position where the (requested) match has been found.
     * @param end Ending position.
     * @param concepts A vector of concepts.
     */
    public SearchItem(URL base,
		      String title, String lang, String filename,
		      double confidence,
		      int begin, int end, Vector concepts) {
	if (base == null) {
	    throw new NullPointerException("base");
	}
	this.base = base;
	if (title == null) {
	    throw new NullPointerException("title");
	}
	this.title = title;
	this.lang = lang;
	if (filename == null) {
	    throw new NullPointerException("fileName");
	}
	this.filename = filename;
	this.confidence = confidence;
	this.begin = begin;
	this.end = end;
	if (concepts == null) {
	    throw new NullPointerException("concepts");
	}
	this.concepts = concepts;
    }

    /**
     * Gets the base of the SearchItem.
     *
     * @return The base for this SearchItem.  Should be used with filename to
     * 	obtain a URL to the desired hit.
     */
    public URL getBase() {
	return base;
    }

    /**
     * Gets the title of the SearchItem.
     *
     * @return The title of the document.  Used to present the hit in the navigator.
     */
    public String getTitle() {
	return title;
    }

    /**
     * Gets the lang of the SearchItem.
     *
     * @return The title of the document.  Used to present the hit in the navigator.
     */
    public String getLang() {
	return lang;
    }

    /**
     * Gets the spec (as a URL relative to getBase() ) to the document.
     *
     * @return The spec, relative to getBase(), to the document containing the hit.
     */
    public String getFilename() {
	return filename;
    }
    
    /**
     * Gets the confidence value for the hit.
     *
     * @return The confidence value for the hit.  
     * This measures how "good" the hit is. The lower the value the better.
     */
    public double getConfidence() {
	return confidence;
    }

    /**
     * Gets the begin pointer position for the hit.
     *
     * @return The starting position for the area in the document where a hit is found.
     */
    public int getBegin() {
	return begin;
    }

    /**
     * Gets the ending pointer position.
     * 
     * @return The ending position for the area in the document where a hit is found.
     */
    public int getEnd() {
	return end;
    }

    /**
     * If there are "concepts" against which the query is made, this is an enumeration
     * of the concepts.  Otherwise null.
     *
     * @return An enumeration of the concepts found in this query.
     */
    public Enumeration getConcepts() {
	return concepts.elements();
    }

    public String toString() {
	StringBuffer result;
	result = new StringBuffer(confidence + " " + title + ":" + 
				  base + filename + " [" + begin + "," + end + 
				  "], {");
	if (concepts == null) {
	    result.append("}");
	    return result.toString();
	}
	Enumeration enum = concepts.elements();
	while(enum.hasMoreElements()) {
	    String concept = (String)enum.nextElement();
	    result.append(concept);
	    if (enum.hasMoreElements()) {
		result.append(",");
	    }
	}
	result.append("}");
	return result.toString();
    }
}

/*
 *        Copyright (C) 1995  Sun Microsystems, Inc
 *                    All rights reserved.
 *          Notice of copyright on this source code 
 *          product does not indicate publication. 
 * 
 * RESTRICTED RIGHTS LEGEND: Use, duplication, or disclosure by 
 * the U.S. Government is subject to restrictions as set forth 
 * in subparagraph (c)(1)(ii) of the Rights in Technical Data
 * and Computer Software Clause at DFARS 252.227-7013 (Oct. 1988) 
 * and FAR 52.227-19 (c) (June 1987).
 *
 *    Sun Microsystems, Inc., 2550 Garcia Avenue,
 *    Mountain View, California 94043.
 */

/*
 * @(#) IndexBuilder.java 1.7 - last change made 03/19/99
 */

package javax.help.search;

import java.io.*;
import java.util.Enumeration;

/**
 * Abstract base class that builds an index for a search database.
 *
 * @author Roger D. Brinkley
 * @version	1.7	03/19/99
 */

public abstract class IndexBuilder
{

    protected String indexDir;

    /**
     * Builds an index at indexDir. If indexDir already exists
     * the index is opened and the new doucments are merged into
     * the existing document.
     */
    public IndexBuilder(String indexDir) throws Exception
    {
	debug("indexDir=" + indexDir);
	this.indexDir = indexDir;
	File test = new File(indexDir);	
	try {
	    if (!test.exists()) {
		debug ("file " + indexDir + " didn't exist - creating");
		test.mkdirs();
	    }
	} catch (java.lang.SecurityException e) {
	}
    }

    /**
     * Closes the index. 
     */
    public abstract void close() throws Exception;

    /**
     * Sets the stopwords in an index. If the stopwords are already 
     * defined for an index, the stop words are merged with the existing
     * set of stopwords.
     * @params stopWords An Enumeration of Strings.
     */
    public abstract void storeStopWords(Enumeration stopWords);

    /**
     * Returns the list of stopwords for an index.
     * @returns Enumeration An enumeration of Strings. Returns null if there are no stopwords.
     */
    public abstract Enumeration getStopWords();

    /**
     * Opens a document to store information.
     */
    public abstract void openDocument(String name) throws Exception;
  
    /**
     * Closes the document. This prevents any additional information from being
     * stored.
     */
    public abstract void closeDocument() throws Exception;

    /**
     * Stores a concept at a given position.
     */
    public abstract void storeLocation(String text, int position) throws Exception;
    
    /**
     * Stores the title for the document.
     */
    public abstract void storeTitle(String title) throws Exception;

    private boolean debug=false;
    private void debug(String msg) {
	if (debug) {
	    System.err.println("IndexBuilder: "+msg);
	}
    }

}

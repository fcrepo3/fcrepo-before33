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
 * @(#) IndexerKit.java 1.12 - last change made 04/07/99
 */

package javax.help.search;

import java.io.*;
import java.util.Locale;

/**
 * Establishes the requirements of an indexing object for a <em>type</em>
 * of text content.  The DefaultKit acts as a factory for policy.  
 * For example, an implementation 
 * for HTML and RTF can be provided that is replaceable 
 * with other implementations.
 * <p>
 * New kits are normally created by cloning a 
 * prototype kit.  
 *
 * @author  Roger D. Brinkley
 * @version %I	%G
 */
public abstract class IndexerKit implements Cloneable {

    protected IndexBuilder builder;
    protected ConfigFile config;
    protected String file;
    protected Locale locale;

    /**
     * Creates a copy of the indexer kit.  This
     * allows an implementation to serve as a prototype
     * for others, so that they can be quickly created.
     *
     * @return the copy
     */
    public abstract Object clone();

    /**
     * Gets the MIME type of the data that this
     * kit represents support for.
     *
     * @return the type
     */
    public abstract String getContentType();

    /**
     * Sets the locale for string tokenizing. A null locale value is valid and means
     * that no locale has been set for this IndexerKit.
     */
    public void setLocale (Locale locale) {
	this.locale = locale;
    }

    /**
     * Convenience method for setting the locale from a lang string
     * Takes the lang string in the form of "language_country_variant".
     * Parses the string and creates an appropriate locale.
     * @param lang A string representation of a locale. If lang is null it is the
     * same as setting the locale to null.
     */
    public void setLocale(String lang) {
	if (lang == null) {
	    setLocale((Locale)null);
	    return;
	}
	String language;
	String country;
	String variant=null;
	Locale newlocale;
	int lpt = lang.indexOf("_");
	if (lpt == -1) {
	    language = lang;
	    country = "";
	    newlocale = new Locale(language, country);
	} else {
	    language = lang.substring(0, lpt);
	    int cpt = lang.indexOf("_", lpt+1);
	    if (cpt == -1) {
		country = lang.substring(lpt+1);
		newlocale = new Locale(language, country);
	    } else {
		country = lang.substring(lpt+1, cpt);
		variant = lang.substring(cpt+1);
		newlocale = new Locale(language, country, variant);
	    }
	}
	setLocale(newlocale);
    }

    /**
     * Gets the Locale. 
     */
    public Locale getLocale() {
	return locale;
    }

    /**
     * Parses content from the given stream. The stream is expected 
     * to be in a format appropriate for this content
     * handler to parse into tokens according to the locale of the class.
     * In the absense of a locale, the default locale tokenizer
     * is used.
     * 
     * @param in  The stream to read from.
     * @param file The file name being parsed.
     * @param builder The IndexBuilder for the full text insertion.
     * @param config The indexer configuration information.
     * @exception IOException on any I/O error.
     */
    public abstract void parse(Reader in, String file, boolean ignoreCharset,
			       IndexBuilder builder,
			       ConfigFile config) 
	throws IOException;

    /**
     * Parses a string into tokens and stores the tokens.
     */
    public abstract int parseIntoTokens (String source, int pos);

    /**
     * Starts the storing of the dcoument.
     */
    protected abstract void startStoreDocument (String file) 
	throws Exception;

    /**
     * Ends the storing of the document.
     */
    protected abstract void endStoreDocument () 
	throws Exception;

    /**
     * Stores a token in the IndexBuilder.
     */
    protected abstract void storeToken (String token, int pos)
	throws Exception;

    /**
     * Stores a title in the IndexBuilder.
     */
    protected abstract void storeTitle (String title) throws Exception;

    /**
     * Debug code
     */

    private boolean debugFlag=false;
    private void debug(String msg) {
        if (debugFlag) {
            System.err.println("IndexerKit: "+msg);
        }
    }
}

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
 * @(#) ConfigFile.java 1.7 - last change made 05/10/99
 */

package javax.help.search;

import java.io.*;
import java.text.*;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * Describes and parses the configuration file for
 * the full-text search indexer.
 *
 * @see Indexer
 * @version	1.7	05/10/99
 * @author	Roger D. Brinkley
 */

public class ConfigFile {

    private String remove;

    private String prepend;

    private Hashtable stopWords;

    private String defStopWords[] = {
	"a", "all", "am", "an", "and", "any", "are", "as", "at", "be", 	"but",
	"by", "can", "could", "did", "do", "does", "etc", "for", "from",
	"goes", "got", 	"had", "has", "have", "he", "her", "him", "his",
	"how", "if", "in", "is", "it", "let", "me", "more", "much", "must",
	"my", "nor", "not", "now", "of", "off", "on", "or", "our", "own",
	"see", "set", "shall", "she", "should",  "so", "some", "than", "that",
	"the", "them", "then", "there", "these", "this", "those", "though",
	"to", "too", "us", "was", "way", "we", "what", "when", "where", 
	"which", "who", "why", "will", "would", "yes", "yet", "you"};

    private Vector files;

    /**
     * Creates a configuration file.
     * @params configFile The config file.
     * @params files A list of files to process in addition to any files in the config file.
     * @params noStopWords If true do not allow stopwords, if false allow the stopwords
     * defined in the config file or the default stop words.
     */
    public ConfigFile (String configFile, Vector files, boolean noStopWords) {
	this.files = files;
	LineNumberReader in;
	String line;
	String removeText = new String ("IndexRemove ");
	String prependText = new String ("IndexPrepend ");
	String fileText = new String ("File ");
	String stopWordsText = new String ("StopWords ");
	String stopWordsFileText = new String ("StopWordsFile ");
	BreakIterator boundary;
	int start;
	String url;

	stopWords = new Hashtable();

	if (configFile == null) {
	    if (!noStopWords) {
		useDefaultStopWords();
	    }
	    return;
	}
	try {
	    in = new LineNumberReader(new BufferedReader
				      (new FileReader(configFile)));
	    while ((line = in.readLine()) != null) {
		if (line.startsWith(removeText)) {
		    remove = line.substring (removeText.length(),
					     line.length());
		} else if (line.startsWith(prependText)) {
		    prepend = line.substring (prependText.length(),
					      line.length());
		} else if (line.startsWith(fileText)) {
		    String file = line.substring (fileText.length(),
						  line.length());
		    files.addElement(file);
		} else if (line.startsWith(stopWordsFileText)) {
		    String file = line.substring (stopWordsFileText.length(),
						  line.length());
		    addStopWordsFile(file);
		} else if (line.startsWith(stopWordsText)) {
		    if (noStopWords) {
			continue;
		    }
		    String words = line.substring (stopWordsText.length(),
						   line.length());
		    boundary = BreakIterator.getWordInstance();
		    boundary.setText(words);
		    start = boundary.first();
		    for (int end = boundary.next();
			 end != BreakIterator.DONE;
			 start = end, end = boundary.next()) {
			String word = words.substring(start,end).trim().toLowerCase();
			if (word.equals(",") || word.equals("")) {
			    continue;
			}
			stopWords.put(word, word);
		    }
		} else {
		    System.out.println ("Unknown Config Keyword at line " +
					in.getLineNumber());
		}
	    }
	    // If there aren't any stopwords then add the default stopwords
	    if (stopWords.isEmpty() && !noStopWords) {
		useDefaultStopWords();
	    }
	} catch (IOException e) {}
    }

    /**
     * Returns the URL filename of a file in String format.
     */
    public String getURLString (String file) {
	String url;

	if (remove != null && (file.startsWith(remove))) {
	    url = file.substring(remove.length(), file.length());
	} else {
	    url = file;
	}
	if (prepend != null) {
	    url = prepend + url;
	}
	if (File.separatorChar != '/') {
	    url = url.replace(File.separatorChar, '/');
	}
	return url;
    }

    /**
     * Returns the list of stopwords from a config file.
     */
    public Enumeration getStopWords() { return stopWords.elements(); }

    /**
     * Gets the list of files from a config file.
     */
    public Vector getFiles () { return files; }

    private void useDefaultStopWords() {
	for (int i=0; i < defStopWords.length; i++) {
	    stopWords.put(defStopWords[i], defStopWords[i]);
	}
    }

    // Add stopwords from a file
    // A single stop words exist per line in the file
    private void addStopWordsFile(String swfile) {
	String word;
	LineNumberReader in;

	if (swfile == null) {
	    return;
	}
	try {
	    in = new LineNumberReader(new BufferedReader
				      (new FileReader(swfile)));
	    while ((word = in.readLine()) != null) {
		word = word.trim();
		stopWords.put(word, word);
	    }
	} catch (IOException e) {}
    }
}

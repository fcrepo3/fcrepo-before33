/*
 * @(#) DefaultHelpModel.java 1.35 - last change made 01/13/99
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
import java.util.Vector;
import java.util.Enumeration;
import java.io.Serializable;
import javax.help.event.EventListenerList;
import javax.help.event.*;
import javax.help.Map.ID;
import javax.help.TextHelpModel.Highlight;
import java.beans.*;

/**
 * This class implements the javax.help.HelpModel API and 
 * notifies the JHelpModel listeners when changes occur.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.35	01/13/99
 */

public class DefaultHelpModel implements TextHelpModel, Serializable {
    private HelpSet helpset;		// the loaded, aka "the top", HelpSet
    private ID currentID;
    private URL currentURL;
    private String navID;	// the label to the "current" navigator
    private Vector highlights = new Vector();

    private String title;	// title of the document - if appropriate

    protected EventListenerList listenerList = new EventListenerList();
    protected EventListenerList textListenerList = new EventListenerList();

    protected PropertyChangeSupport changes = new PropertyChangeSupport(this);

    /**
     * Constructs a HelpModel from a HelpSet
     *
     * @see javax.help.HelpSet
     * @param hs The HelpSet from which to build this model. A null hs is valid
     * creating a DefaultHelpModel without a HelpSet.
     */
    public DefaultHelpModel(HelpSet hs) {
	this.helpset = hs;
    }

    /**
     * Sets the HelpSet for this HelpModel. A null hs is valid.
     */
    public void setHelpSet(HelpSet hs) {
	HelpSet old = this.helpset;
	this.helpset = hs;
	changes.firePropertyChange("helpSet", old, hs);
    }

    /**
     * Gets the backing HelpSet.
     *
     * @return the helpset. A null hs is valid.
     */
    public HelpSet getHelpSet() {
	return helpset;
    }

    /**
     * Sets the current ID.
     * HelpModelListeners and HelpVisitListeners are notified.
     * If the parameter ident is null, the homeID of the current HelpSet is
     * used unless it is also null in which case the method returns without
     * setting the currentID.
     *
     * @param ident The ID to set. 
     * If ident is null set the currentID to the HelpSet's HomeID.
     * If the HomeID doesn't exist the currentID is set to null.
     * @exception InvalidHelpSetContextException The ID is not valid for the HelpSet
     */
    public void setCurrentID(ID ident) throws InvalidHelpSetContextException {
	if (ident == null) {
	    ident = helpset.getHomeID();
	}
	if (ident == null || ident.equals(currentID)) {
	    // quick return if already set
	    return;
	}
	
	String id = ident.id;
	HelpSet hs = ident.hs;

	if (! helpset.contains(hs)) {
	    // invalid context
	    throw new InvalidHelpSetContextException("Wrong context",
						     helpset,
						     hs);
	}

	Map map = helpset.getCombinedMap();
	currentID = ident;
	try {
	    ID tmpID = ident;
	    URL url;
	    if (hs == helpset) { 
		url = map.getURLFromID(ident);
	    } else {
		Map hsmap = hs.getLocalMap();
		url = hsmap.getURLFromID(ident);
	    }
	    if (currentURL != null &&
		currentURL.equals(url)) {
		// workaround bug in 1.1.x doesn't compare anchors correctly
		String currentRef = currentURL.getRef();
		String urlRef = url.getRef();
		// if both are null then they are the same
		if (currentRef == null && urlRef == null) {
		    return;	// avoid loops, don't propagate the ID change!
		}
		// if both have some value and have the same content they are
		// the same
		if (currentRef != null && urlRef != null &&
		    currentRef.compareTo(urlRef) == 0) {
		    return;	// avoid loops, don't propagate the ID change!
		}
	    }
	    currentURL = url;
	} catch (Exception ex) {
	    currentURL = null;
	}

	// remove the highlights, but no need to fire an event because it is
	// implicit in the next fireIDChanged() event
	highlights.setSize(0);
	fireIDChanged(this, currentID, currentURL);
    }

    /**
     * Gets the current ID.
     *
     * @return the current ID. A null ID is a valid id. If no ID has been set
     * a null ID is returned.
     */
    public ID getCurrentID() {
	return currentID;
    }

    /**
     * Sets the current URL.
     * The current ID changes if there is a matching ID for this URL
     * and HelpModelListners are notified.
     *
     * @param url The url to set the model to. A null URL is a valid url.
     */
    public void setCurrentURL(URL url) {
        
        boolean fire = false;

        // Setup CurrentURL
        if (currentURL == null) {
            if (currentURL != url) {
		currentURL = url;
                fire = true;
            }
        } else {
            if (!currentURL.equals(url)) {
                currentURL = url;
                fire = true;
            }
        }

        // Setup CurrentID
        if (currentURL == null) {
            if (currentID != null) {
                currentID = null;
                fire = true;
            }
        } else {
            ID id = helpset.getCombinedMap().getIDFromURL(currentURL);
            if (currentID == null) {
                if (currentID != id) {
                    currentID = id;
                    fire = true;
                }
            } else {
                if (!currentID.equals(id)) {
                    currentID = id;
                    fire = true;
                }
            }
        }

        // Fire only if CurrentURL or CurrentID was changed
        if (fire) {        
	    // remove the highlights, but no need to fire an event because it is
	    // implicit in the next fireIDChanged() event
	    highlights.setSize(0);
	    fireIDChanged(this, currentID, currentURL);
        }
    }

    /**
     * Returns the current URL
     *
     * @return The current URL. A null URL is a valid URL. If no URL has been
     * previously set a null URL will be returned.
     */
    public URL getCurrentURL() {
	return currentURL;
    }

    /**
     * Highlights a range of positions in a document.
     *
     * @param pos0 start position
     * @param pos1 end position
     */
    public void addHighlight(int pos0, int pos1) {
	debug("addHighlight("+pos0+", "+pos1+")");
	highlights.addElement(new DefaultHighlight(pos0, pos1));
	fireHighlightsChanged(this);
    }

    /**
     * Removes highlights.
     */
    public void removeAllHighlights() {
	debug("removeAllHighlights");
	highlights.setSize(0);
	fireHighlightsChanged(this);
    }
	
    /**
     * Set highlights. Clear the current Hightlights and set new Highlights
     * 
     * @param h An array of Hightlights. If h is null it is the same as setting 
     * no highlights
     */
    public void setHighlights(Highlight[] h) {
	highlights.setSize(0);
	if (h == null) {
	    return;
	}
	for (int i=0; i<h.length; i++) {
	    highlights.addElement(new DefaultHighlight(h[i].getStartOffset(),
						       h[i].getEndOffset()));
	}
	if (highlights.size() > 0) {
	    fireHighlightsChanged(this);
	}
    }

    /**
     * Geta all the highlights currently active.
     *
     * @return An array of highlights
     */
    public Highlight[] getHighlights() {
	Highlight back[] = new DefaultHighlight[highlights.size()];
	highlights.copyInto(back);
	return back;
    }


    /**
     * Adds a listener for the HelpModelEvent posted after the model has
     * changed.
     * 
     * @param l - The listener to add.
     * @see javax.help.HelpModel#removeHelpModelListener
     * @throws IllegalArgumentException if l is null.
     */
    public void addHelpModelListener(HelpModelListener l) {
	debug("addHelpModelListener: ");
	debug("  l:"+l);
	if (debug) {
	    try {
		throw new Exception("");
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	listenerList.add(HelpModelListener.class, l);
    }

    /**
     * Removes a listener previously added with <b>addHelpListener</b>
     *
     * @param l - The listener to remove. If l is not in the list of listeners
     * it is ignored.
     * @see javax.help.HelpModel#addHelpModelListener
     * @throws IllegalArgumentException if l is null.
     */
    public void removeHelpModelListener(HelpModelListener l) {
	listenerList.remove(HelpModelListener.class, l);
    }

    /**
     * Adds a listener for the TextHelpModelEvent posted after the model has
     * changed.
     * 
     * @param l - The listener to add.
     * @see javax.help.HelpModel#removeHelpModelListener
     * @throws IllegalArgumentException if l is null.
     */
    public void addTextHelpModelListener(TextHelpModelListener l) {
	debug("addTextHelpModelListener: ");
	debug("  l:"+l);
	// HERE
	if (debug) {
	    try {
		throw new Exception("");
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }
	}
	textListenerList.add(TextHelpModelListener.class, l);
    }

    /**
     * Removes a listener previously added with <b>addHelpListener</b>
     *
     * @param l - The listener to remove. If l is not on the list of listeners
     * it is ignored.
     * @see javax.help.HelpModel#addHelpModelListener
     * @throws IllegalArgumentException if l is null.
     */
    public void removeTextHelpModelListener(TextHelpModelListener l) {
	textListenerList.remove(TextHelpModelListener.class, l);
    }

    /**
     * Adds a listener to changes to the properties in this model.
     *
     * @param l  the listener to add
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
	changes.addPropertyChangeListener(l);
    }

    /**
     * Removes a listener to changes to the properties in this model.
     *
     * @param l  the listener to remove. If l is not on the list of listeners
     * it is ignored.
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
	changes.removePropertyChangeListener(l);
    }

    /**
     * Assigns the document title.
     *
     * @param title the Title for the document currently being shown. A null 
     * title is valid.
     */
    public void setDocumentTitle(String title) {
	String oldTitle = this.title;
	this.title = title;
	changes.firePropertyChange("documentTitle", oldTitle, title);
    }

    /**
     * Gets the document title.
     *
     * @return The title for the current document. A null title is valid. If 
     * the title has not be previously set it will be null.
     */
    public String getDocumentTitle() {
	return title;
    }

    protected void fireIDChanged(Object source, ID id, URL url) {
	Object[] listeners = listenerList.getListenerList();
	HelpModelEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == HelpModelListener.class) {
		if (e == null) {
		    e = new HelpModelEvent(source, id, url);
		}
		debug("fireIDChanged: ");
		debug("  "+listeners[i+1]);
		debug("  id="+e.getID() + " url="+e.getURL());
		((HelpModelListener)listeners[i+1]).idChanged(e);
	    }	       
	}
    }

    protected void fireHighlightsChanged(Object source) {
	Object[] listeners = textListenerList.getListenerList();
	TextHelpModelEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == TextHelpModelListener.class) {
		if (e == null) {
		    e = new TextHelpModelEvent(source);
		}
		debug("fireHighlightsChanged: ");
		debug("  "+listeners[i+1]);
		debug("  "+e);
		((TextHelpModelListener)listeners[i+1]).highlightsChanged(e);
	    }	       
	}
    }

    /**
     * A default implementation of TextHelpModel.Highlight
     */

    public static class DefaultHighlight implements Highlight {
	public int start, end;	// start, end offsets.  >=0

	/**
	 * Constructor
	 */
	public DefaultHighlight(int start, int end) {
	    if (start < 0) {
		throw new IllegalArgumentException("start");
	    }
	    if (end < 0) {
		throw new IllegalArgumentException("end");
	    }
	    this.start = start;
	    this.end = end;
	}

	/**
	 * Start offset
	 *
	 * @returns start offset (>=0)
	 */
	public int getStartOffset() {
	    return start;
	}

	/**
	 * End offset
	 *
	 * @returns end offset (>=0)
	 */
	public int getEndOffset() {
	    return end;
	}
    }

    /**
     * For printf debugging...
     */
    private static boolean debug = false;
    private static void debug(String str) {
        if (debug) {
            System.err.println("DefaultHelpModel: " + str);
        }
    }
}

/*
 * @(#) HelpModelEvent.java 1.16 - last change made 03/10/99
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

package javax.help.event;

import java.net.URL;
import java.util.Vector;
import java.util.Enumeration;
import javax.help.HelpSet;
import javax.help.Map.ID;

/**
 * Notifies interested parties that a change in a
 * Help Model source has occurred.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.16	03/10/99
 */

public class HelpModelEvent extends java.util.EventObject {
    private ID id;
    private URL url;

    /**
     * Represents a change in the JavaHelp in the current ID or URL.
     * @see javax.help.JavaHelp
     * 
     * @param source The source for this event.
     * @param id The ID that has changed. Should be null if URL is specified.
     * @param url The URL that has changed. Should be null if ID is specified.
     * @throws IllegalArgumentException if source is null.
     */
    public HelpModelEvent(Object source, ID id, URL url) {
	super (source);
	this.id = id;
	this.url = url;
    }

    /**
     * Creates a HelpModelEvent for highlighting.
     *
     * @param source The source for this event.
     * @param pos0 Start position.
     * @param pos1 End position.
     * @throws IllegalArgumentException if source is null.
     */
    public HelpModelEvent(Object source, int pos0, int pos1) {
	super (source);
	this.pos0 = pos0;
	this.pos1 = pos1;
    }
    /**
     * Returns the current ID in the HelpModel.
     * @return The current ID.
     */
    public ID getID() {
	return id;
    }

    /**
     * Returns the current URL in the HelpModel. If there was a current ID
     * this is the URL for that ID.
     * @return The current URL.
     */
    public URL getURL() {
	return url;
    }

    private int pos0, pos1;

    // HERE - Review this highlighting; it is a different type of beast than the rest - epll
    /**
     * @return The start position of this (highlighting) event.
     */
    public int getPos0() {
	return pos0;
    }

    /**
     * @return The end position of this (highlighting) event.
     */
    public int getPos1() {
	return pos1;
    }
}

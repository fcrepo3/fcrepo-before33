/*
 * @(#) HelpModel.java 1.22 - last change made 01/29/99
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
import javax.help.event.*;
import javax.help.Map.ID;
import java.beans.*;

/**
 * The interface to the model of a JHelp that represents the 
 * HelpSet being presented to the user.
 * 
 * Note that a HelpSet can contain nested HelpSets within it; IDs
 * include both a String and the HelpSet to which the String applies.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.22	01/29/99
 */
public interface HelpModel {
    /**
     * Sets the loaded (aka "top") HelpSet for this model.
     */
    public void setHelpSet(HelpSet hs);

    /**
     * Gets the loaded (aka "top") HelpSet for this model.
     */
    public HelpSet getHelpSet();

    /**
     * Sets the current ID relative to some HelpSet
     * HelpModelListeners and HelpVisitListeners are notified
     *
     * @param id the ID used to set
     * @exception InvalidHelpSetContextException The HelpSet of the ID is not
     * valid for the HelpSet currently loaded in the model
     */
    public void setCurrentID(ID id) throws InvalidHelpSetContextException;

    /**
     * Gets the current ID.
     *
     * @return The current ID.
     */
    public ID getCurrentID();

    /**
     * Sets the current URL.
     * HelpModelListeners are notified.
     * The current ID changes if there is a matching id for this URL
     *
     * @param The URL to set.
     */
    public void setCurrentURL(URL url);

    /**
     * Returns The current URL.
     *
     * @return The current URL.
     */
    public URL getCurrentURL();

    /**
     * Adds a listener for the HelpModelEvent posted after the model has
     * changed.
     * 
     * @param l The listener to add.
     * @see javax.help.HelpModel#removeHelpModelListener
     */
    public void addHelpModelListener(HelpModelListener l);

    /**
     * Removes a listener previously added with <tt>addHelpModelListener</tt>
     *
     * @param l The listener to remove.
     * @see javax.help.HelpModel#addHelpModelListener
     */
    public void removeHelpModelListener(HelpModelListener l);

    /**
     * Adds a listener to monitor changes to the properties in this model
     *
     * @param l  The listener to add.
     */
    public void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Removes a listener monitoring changes to the properties in this model
     *
     * @param l  The listener to remove.
     */
    public void removePropertyChangeListener(PropertyChangeListener l);
}

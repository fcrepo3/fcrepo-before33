/*
 * @(#) HelpBroker.java 1.20 - last change made 01/14/99
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

import java.util.Enumeration;
import java.net.URL;
import java.awt.Component;
import java.awt.MenuItem;
import java.awt.event.ActionListener;
import java.awt.Point;
import java.awt.Font;
import java.awt.Dimension;
import javax.help.Map.ID;
import java.util.Locale;

/**
 * The HelpBroker is the default presentation of a HelpSet.
 *
 * A HelpBroker is an abstraction of the presentation for a HelpSet;
 * a straight-forward implementation is a JHelp() on the HelpSet.
 *
 * A HelpBroker can be asked to show a given Navigational View,
 * and can display a given ID (help topic).
 *
 * @author Eduardo Pelegri-Llopart
 * @author Roger D.Brinkley
 * @version	1.20	01/14/99
 *
 * @see javax.help.HelpSet
 * @see javax.help.JHelpNavigator
 * @see javax.help.HelpVisitListener
 */

public interface HelpBroker {
    /**
     * Sets the current HelpSet for this HelpBroker.
     *
     * @param hs The HelpSet this JavaHelp is presenting.
     */
    public void setHelpSet(HelpSet hs);

    /**
     * Gets the current HelpSet for this JavaHelp object.
     *
     * @return The HelpSet this JavaHelp is presenting.
     */
    public HelpSet getHelpSet();

    /**
     * Returns the locale of this object.
     *
     * @return The locale of this object.
     */
    public Locale getLocale();

    /**
     * Sets the locale of this HelpBroker.
     * @param l The locale to become this component's locale.
     * @see #getLocale
     */
    public void setLocale(Locale l);

    /**
     * Gets the font for this HelpBroker.
     */
    public Font getFont();

    /**
     * Sets the font for this HelpBroker.
     *
     */
    public void setFont(Font f);

    /**
     * Activates the Navigator view with a given name.
     *
     * @exception IllegalArgumentException if the name is not valid.
     */
    public void setCurrentView(String name);

    /**
     * Gets name of the current navigational view.
     *
     * @return The name of the current navigational view.
     */
    public String getCurrentView();

    /**
     * Initializes the presentation.
     * This method allows the presentation to be initialized but not displayed.
     * Typically this is done in a separate thread to reduce the
     * intialization time.
     */
    public void initPresentation();

    /**
     * Displays the presentation to the user.
     *
     * @param displayed Makes the presentation visible or not.
     * @exception HelpBroker.UnsupportedOperationException If the operation is not supported.
     */
    public void setDisplayed(boolean displayed) throws UnsupportedOperationException;

    /**
     * Determines if the presentation is visible.
     *
     * @return Whether the presentation is currently visible.
     */
    public boolean isDisplayed();

    /**
     * Sets the position of the presentation.
     * This operation may throw an UnsupportedOperationException if the
     * underlying implementation does not allow this.
     */
    public void setLocation(Point p) throws UnsupportedOperationException;

    /**
     * Gets the location of the presentation.
     * This operation may throw an UnsupportedOperationException if the
     * underlying implementation does not allow this.
     */
    public Point getLocation() throws UnsupportedOperationException;

    /**
     * Sets the size of the presentation.
     * This operation may throw an UnsupportedOperationException if the
     * underlying implementation does not allow this.
     */
    public void setSize(Dimension d) throws UnsupportedOperationException;

    /**
     * Gets the size of the presentation.
     * This operation may throw an UnsupportedOperationException if the
     * underlying implementation does not allow this.
     */
    public Dimension getSize() throws UnsupportedOperationException;

    /**
     * Hides/Shows Navigational Views.
     *
     * @param displayed Make the navigational views visible or not.
     */
    public void setViewDisplayed(boolean displayed);

    /**
     * Determines if the Navigational View is visible.
     * 
     * @return Whether the navigational views are visible.
     */
    public boolean isViewDisplayed();


    /**
     * Displays this ID.
     *
     * @param id An ID that identifies the topic to display.
     * @exception InvalidHelpSetContextException If id.hs is not contanied in the current
     * HelpSet of this broker.
     * @see HelpModel#setCurrentID
     */
    public void setCurrentID(ID id) throws InvalidHelpSetContextException;

    /**
     * Displays this ID.
     * HelpVisitListeners are notified.
     * 
     * @param id A String identifying the topic to show relative to getHelpSet()
     * @exception BadIDException if the ID is not valid in the map.
     */
    public void setCurrentID(String id) throws BadIDException;

    /**
     * Determines The currently displayed ID (if any).
     *
     * @return The ID being shown.
     */
    public ID getCurrentID();

    /**
     * Displays this ID.
     * HelpVisitListeners are notified.
     * The currentID changes if there is a matching ID for this URL.
     *
     * @param url The URL to show
     */
    public void setCurrentURL(URL url);

    /**
     * Determines the currently displayed ID.
     *
     * @return The URL being shown.
     */
    public URL getCurrentURL();

    /**
     * Enables the Help key on a component. This method works best when
     * the component is the
     * rootPane of a JFrame in Swing implementations, or a java.awt.Window
     * (or subclass thereof) in AWT implementations.
     * This method sets the default
     * helpID and HelpSet for the component and registers keyboard actions
     * to trap the "Help" keypress. When the "Help" key is pressed, if the
     * object with the current focus has a helpID, the helpID is displayed,
     * otherwise the default helpID is displayed.
     *
     * @param comp The component to enable the keyboard actions on.
     * @param id The default HelpID to be displayed.
     * @param hs The default HelpSet to be displayed.
     */
    public void enableHelpKey(Component comp, String id, HelpSet hs);

    /**
     * Enables help for a component. This method sets a 
     * component's helpID and HelpSet. 
     *
     * @see CSH.setHelpID
     * @see CSH.setHelpSet
     */
    public void enableHelp(Component comp, String id, HelpSet hs);

    /**
     * Enables help for a MenuItem. This method sets a 
     * component's helpID and HelpSet. 
     *
     * @see CSH.setHelpID
     * @see CSH.setHelpSet
     */
    public void enableHelp(MenuItem comp, String id, HelpSet hs);

    /**
     * Enables help for a component. This method sets a 
     * component's helpID and HelpSet and adds an ActionListener. 
     * When an action is performed
     * it displays the component's helpID and HelpSet in the default viewer.
     * If the component is not a javax.swing.AbstractButton or a 
     * java.awt.Button an IllegalArgumentException is thrown.
     *
     * @see CSH.setHelpID
     * @see CSH.setHelpSet
     * @see javax.swing.AbstractButton
     * @see java.awt.Button
     */
    public void enableHelpOnButton(Component comp, String id, HelpSet hs)
	throws IllegalArgumentException;

    /**
     * Enables help for a MenuItem. This method sets a 
     * component's helpID and HelpSet and adds an ActionListener. 
     * When an action is performed
     * it displays the component's helpID and HelpSet in the default viewer.
     *
     * @see CSH.setHelpID
     * @see CSH.setHelpSet
     * @see java.awt.MenuItem
     */
    public void enableHelpOnButton(MenuItem comp, String id, HelpSet hs);

}

package com.rolemodelsoft.drawlet;

/**
 * @(#)Figure.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1996 Knowledge Systems Corporation (KSC). All Rights Reserved.
 *
 * Permission to use, copy, demonstrate, or modify this software
 * and its documentation for NON-COMMERCIAL or NON-PRODUCTION USE ONLY and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies and all terms of license agreed to when downloading 
 * this software are strictly followed.
 *
 * RMS AND KSC MAKE NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. NEITHER RMS NOR KSC SHALL BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
import java.awt.*;
import java.util.Hashtable;
import java.util.Enumeration;
import com.rolemodelsoft.drawlet.util.*;
import java.io.Serializable;
import java.beans.PropertyChangeListener;

/**
 * This interface defines a generic Figure that can be drawn on and 
 * potentially manipulated on a DrawingCanvas (or elsewhere).
 *
 * @version 	1.1.6, 12/28/98
 */
 
public interface Figure extends Paintable, Duplicatable, Serializable {
	/**
	 * The shape property selector.
	 */
	public static String SHAPE_PROPERTY = "shape";

	/**
	 * The size property selector.
	 */
	public static String SIZE_PROPERTY = "size";

	/**
	 * The location property selector.
	 */
	public static String LOCATION_PROPERTY = "location";

	/**
	 * The style property selector.
	 */
	public static String STYLE_PROPERTY = "style";

	/**
	 * The fill color property selector.
	 */
	public static String FILL_COLOR_PROPERTY = "fillColor";

	/**
	 * The line color property selector.
	 */
	public static String LINE_COLOR_PROPERTY = "lineColor";

	/**
	 * The text color property selector.
	 */
	public static String TEXT_COLOR_PROPERTY = "textColor";

	/**
	 * The string property selector.
	 */
	public static String STRING_PROPERTY = "string";

	/**
	 * The relation property selector.
	 */
	public static String RELATION_PROPERTY = "relation";
	/**
	 * Add a PropertyChangeListener to the listener list.
	 *
	 * @param listener  The PropertyChangeListener to be added
	 */

	public void addPropertyChangeListener(PropertyChangeListener listener);
	/**
	 * Add a RelatedLocationListener to the listener list.
	 *
	 * @param listener  The RelatedLocationListener to be added
	 */

	public void addRelatedLocationListener(RelatedLocationListener listener);
	/**  
	 * Checks whether a specified x,y location is "inside" this
	 * Figure, where x and y are defined to be relative to the 
	 * coordinate system of this figure.  
	 *
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 * @return  boolean value of <code>true</code> if x and y are "inside" this Figure;
	 *          <code>false</code> otherwise.
	 */
	public abstract boolean contains(int x, int y);
	/**  
	 * Checks whether a specified Figure is completely "inside" this
	 * Figure, where the figures share the same coordinate system.
	 * It is intended that objects that are smart enough to use something
	 * besides bounds will implement this intelligently, and isWithin will
	 * turn the parameter and receiver around to invoke this.
	 *
	 * @param figure the Figure to test for inclusion
	 * @return boolean value of <code>true</code> if the specified Figure is completely "inside" this Figure
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean contains(Figure figure);
	/**  
	 * Checks whether a specified Rectangle is "inside" this
	 * Figure, where the Rectangle and this Figure are in the same coordinate system  
	 *
	 * @param box the rectangle to test for inclusion
	 * @return boolean value of <code>true</code> if the specified Rectangle is "inside" this Figure;
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean contains(Rectangle box);
	/** 
	 * Clean up as appropriate if the figure is no longer connected to others.
	 */
	public abstract void disconnect();
	/** 
	 * Answers a Handle that will provide 
	 * editing capabilities on the receiver, or null.
	 *
	 * @param x the x coordinate to potentially begin editing
	 * @param y the y coordinate to potentially begin editing
	 * @return a Handle that will provide editing capabilities on the receiver, or null
	 */
	public abstract Handle editTool(int x, int y);
	/** 
	 * Answer the handles associated with the receiver.
	 *
	 * @return an array of the Handles associated with the receiver
	 */
	public abstract Handle[] getHandles();
	/** 
	 * Returns the current locator of this figure.
	 * This may or may not represent the top left of the receiver's area.
	 *
	 * @return the current Locator of this figure
	 */
	public abstract Locator getLocator();
	/** 
	 * Answer the style which defines how to paint the figure.
	 * NOTE: It may be valid to return null if the figure just doesn't care
	 *
	 * @return the DrawingStyle which defines how to paint the figure
	 */
	public abstract DrawingStyle getStyle();
	/** 
	 * Answers whether the receiver intersects another figure.
	 *
	 * @param anotherFigure the figure the receiver is potentially intersecting.
	 * @return boolean value of <code>true</code> if the receiver intersects another figure;
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean intersects(Figure anotherFigure);
	/** 
	 * Answers whether the receiver intersects a Rectangular area.
	 *
	 * @param box the Rectangular area
	 * @return boolean value of <code>true</code> if the receiver intersects the Rectangular area;
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean intersects(Rectangle box);
	/** 
	 * Answers whether the receiver is obsolete. 
	 * True if some event has happened that makes this a meaningless object.
	 *
	 * @return boolean value of <code>true</code> if some event has happened
	 * that makes this a meaningless object;
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean isObsolete();
	/** 
	 * Answers whether the receiver is fully within another Figure.
	 *
	 * @param anotherFigure the figure the receiver is potentially inside.
	 * @return boolean value of <code>true</code> if the receiver is fully within another Figure;
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean isWithin(Figure anotherFigure);
	/** 
	 * Answers whether the receiver is fully within a Rectangular area.
	 *
	 * @param box the Rectangular area
	 * @return boolean value of <code>true</code> if the receiver is fully within a Rectangular area;
	 *         <code>false</code> otherwise.
	 */
	public abstract boolean isWithin(Rectangle box);
	/** 
	 * Answers a locator corresponding to a significant point on the receiver.
	 * It is up to the receiver whether to answer an absolute or relative locator
	 * or even whether to give a non-null answer or ignore the x, y coordinates.
	 *
	 * @param x the x coordinate of the requested locator
	 * @param y the y coordinate of the requested locator
	 * @return a Locator corresponding to a significant point on the receiver
	 */
	public abstract Locator locatorAt(int x, int y);
	/** 
	 * Moves the Figure to a new location. The x and y coordinates
	 * are in the parent's coordinate space.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @see #location
	 * @see #reshape
	 */
	public abstract void move(int x, int y);
	/** 
	 * Moves the Figure to a new location.
	 * Note: Subclasses may wish to update dependencies based on this new location
	 *
	 * @param locator the Locator which identifies the desired x, y coordinates.
	 * @see #location
	 */
	public abstract void move(Locator locator);
	/**
	 * Answer with an Enumeration over the RelatedLocationListeners.
	 *
	 * @return an Enumeration over the RelatedLocationListeners
	 */

	public Enumeration relatedLocationListeners();
	/**
	 * Remove a PropertyChangeListener from the listener list.
	 *
	 * @param listener  The PropertyChangeListener to be removed
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener);
	/**
	 * Remove a RelatedLocationListener from the listener list.
	 *
	 * @param listener  The RelatedLocationListener to be removed
	 */
	public void removeRelatedLocationListener(RelatedLocationListener listener);
	/** 
	 * Answers a Locator corresponding to a significant point on the receiver 
	 * that will serve as a connection to the other Figure.
	 * It is up to the receiver whether to answer a relative locator
	 * or even whether to give a non-null answer or ignore the x, y coordinates.
	 *
	 * @param requestor the Figure requesting a connection
	 * @param x the x coordinate of the requested locator
	 * @param y the y coordinate of the requested locator
	 * @return a Locator corresponding to a significant point on the
	 * receiver that will serve as a connection to the other Figure
	 */
	public abstract Locator requestConnection(Figure requestor, int x, int y);
	/** 
	 * Reshapes the Figure to the specified bounding box.
	 * Observable objects may wish to notify dependents
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the figure
	 * @param height the height of the figure
	 * @see #bounds
	 * @see #move
	 * @see #resize
	 */
	public abstract void setBounds(int x, int y, int width, int height);
	/**
	 * Resizes the Figure to the specified width and height.
	 *
	 * @param width the width of the figure
	 * @param height the height of the figure
	 * @see #size
	 * @see #reshape
	 */
	public abstract void setSize(int width, int height);
	/** 
	 * Resizes the Figure to the specified dimension.
	 *
	 * @param d the figure dimension
	 * @see #size
	 * @see #reshape
	 */
	public abstract void setSize(Dimension d);
	/** 
	 * Set the style defining how to paint the Figure.
	 *
	 * @param style the specified DrawingStyle
	 */
	public abstract void setStyle(DrawingStyle style);
	/** 
	 * Moves the Figure in the x and y direction.  
	 * Observable objects may wish to notify dependents
	 *
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 * @see #location
	 * @see #reshape
	 */
	public abstract void translate(int x, int y);
}

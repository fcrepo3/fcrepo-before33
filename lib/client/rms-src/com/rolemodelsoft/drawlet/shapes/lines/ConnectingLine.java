package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)ConnectingLine.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1997 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
 
import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.shapes.*;
import java.awt.*;
import java.util.Hashtable;
import java.util.Enumeration;
import java.beans.PropertyChangeEvent;

/**
 * This provides a basic implementation of Lines that can connect to other figures.
 * Note that there can be any number of Locators that make up a Line and
 * that those Locators may be tied to Figures.  When they are, the Line
 * will become a dependent of the Figure and will assume it is moved when
 * the Figure is moved/reshaped in any way.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class ConnectingLine extends Line implements RelatedLocationListener {
	static final long serialVersionUID = -5097190759387410089L;

	/**
	 * A boolean defining whether this line must be connected.
	 */
	protected boolean mustConnect = defaultConnectState();
	/** 
	 * Constructs and initializes a new instance of a connecting line.
	 *
	 * @param beginX the x coordinate for the first point defining the line
	 * @param beginY the y coordinate for the first point defining the line
	 * @param endX the x coordinate for the second point defining the line
	 * @param endY the y coordinate for the second point defining the line
	 */
	public ConnectingLine(int beginX, int beginY, int endX, int endY) {
		super(beginX, beginY, endX, endY);
	}
	/** 
	 * Constructs and initializes a new instance of a connecting line.
	 *
	 * @param beginX the x coordinate for the first point defining the line
	 * @param beginY the y coordinate for the first point defining the line
	 * @param endX the x coordinate for the second point defining the line
	 * @param endY the y coordinate for the second point defining the line
	 * @param mustConnect specifies whether this line has to be connected in order to exist
	 */
	public ConnectingLine(int beginX, int beginY, int endX, int endY, boolean mustConnect) {
		super(beginX, beginY, endX, endY);
		this.mustConnect = mustConnect;
	}
	/** 
	 * Constructs and initializes a new instance of a connecting line.
	 *
	 * @param begin the locator which is the first point defining the line
	 * @param end the locator which is the second point defining the line
	 */
	public ConnectingLine(Locator begin, Locator end) {
		super(begin, end);
	}
	/** 
	 * Constructs and initializes a new instance of a connecting line.
	 *
	 * @param begin the locator which is the first point defining the line
	 * @param end the locator which is the second point defining the line
	 * @param mustConnect specifies whether this line has to be connected in order to exist
	 */
	public ConnectingLine( Locator begin, Locator end, boolean mustConnect ) {
		super(begin, end);
		this.mustConnect = mustConnect;
	}
	/**
	 * Set the locator at the appropriate position.
	 * 
	 * @param index the index of the locator desired.
	 * @param locator the new Locator.
	 */
	protected synchronized void basicSetLocator(int index, Locator locator)  {
		Figure figure = figureFromLocator(points[index]);
		if (figure != null) 
			figure.removeRelatedLocationListener(this);
		points[index] = locator;
		figure = figureFromLocator(locator);
		if (figure != null) 
			figure.addRelatedLocationListener(this);
	}
	/**
	 * Answer the default state for connectedness.
	 *
	 * @return a boolean representing whether this line must be connected to
	 * exist.
	 */
	protected boolean defaultConnectState() {
		return false;
	}
	/** 
	 * Clean up as appropriate if the receiver is no longer valid.
	 */
	public void disconnect() {
		for (int i = 0; i < points.length; i++) {
			Figure figure = figureFromLocator(points[i]);
			if (figure != null)
				figure.removeRelatedLocationListener(this);
		}
		super.disconnect();
	}
	/**
	 * Remove any dependence on the figure.
	 * 
	 * @param figure the Figure to disassociate from.
	 */
	protected synchronized void freeFromFigure(Figure figure)  {
		for (int i = 0; i < points.length; i++) {
			if (figureFromLocator(points[i]) == figure) 
				points[i] = new DrawingPoint(points[i].x(),points[i].y());
		}
	}
	/** 
	 * Answer the handles associated with the receiver.
	 * 
	 * @return	an array of the Handles associated with the receiver
	 */
	public Handle[] getHandles() {
		int numberOfHandles = getNumberOfPoints();
		Handle handles[] = new Handle[numberOfHandles];
		for (int i=0; i < numberOfHandles; i++)
			handles[i] = new ConnectingLinePointHandle(this,i);
		return handles;
	}
	/** 
	 * Answers whether the receiver is listening to the figure directly 
	 * or indirectly (via chain of listeners)
	 * 
	 * @param figure the Figure to test
	 * @return	boolean value of <code>true</code> if the receiver is listening
	 * to the figure directly or indirectly;
	 * 			<code>false</code> otherwise.
	 */
	protected boolean isListening(Figure figure) {
		for (Enumeration e = figure.relatedLocationListeners(); e.hasMoreElements();) {
			RelatedLocationListener listener = (RelatedLocationListener) e.nextElement();
			if (listener == this)
				return true;
			else
				if (listener instanceof Figure)
					if (isListening((Figure) listener))
						return true;
		}
		return false;
	}
	/** 
	 * Answers whether the receiver is obsolete.
	 * 
	 * @return	boolean value of <code>true</code> if both ends
	 * are not connected to a figure;
	 * 			<code>false</code> otherwise.
	 */
	public boolean isObsolete() {
		return mustConnect ? ((figureFromLocator(getLocator(0)) == null) || (figureFromLocator(getLocator(getNumberOfPoints() - 1)) == null)) : false;
	}
	/**
	 * Update because the location of something we depend on has changed.
	 *
	 * @evt the event.
	 */
	public void locationChanged(PropertyChangeEvent event) {
		updateShape();
	}
	/**
	 * Update because the relationship of something we depend on has changed.
	 *
	 * @evt the event.
	 */
	public void relationChanged(PropertyChangeEvent event) {
		freeFromFigure((Figure)event.getSource());
	}
	/** 
	 * Answers a Locator corresponding to a significant point on the receiver 
	 * that will serve as a connection to the other figure.
	 * 
	 * @param requestor the figure requesting the connection
	 * @param x the x coordinate of the requested locator
	 * @param y the y coordinate of the requested locator
	 * @return	a Locator corresponding to a significant point on the receiver
	 */
	public Locator requestConnection(Figure requestor, int x, int y) {
		// make sure we aren't already connected to the locator 
		// which is trying to connect to us 
		if (isListening(requestor))
			return null;
		return locatorAt(x,y);
	}
	/**
	 * Update because the shape of something we depend on has changed.
	 *
	 * @evt the event.
	 */
	public void shapeChanged(PropertyChangeEvent event) {
		updateShape();
	}
	/**
	 * Update because the size of something we depend on has changed.
	 *
	 * @evt the event.
	 */
	public void sizeChanged(PropertyChangeEvent event) {
		updateShape();
	}
	/**
	 * Assume our location has changed due to some movement/reshaping
	 * of something we depend on.
	 */
	protected void updateShape() {
		Rectangle oldBounds = getBounds();
		resetBoundsCache();
		changedShape(oldBounds);
	}
}

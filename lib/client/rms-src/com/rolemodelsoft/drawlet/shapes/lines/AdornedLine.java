package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)AdornedLine.java
 *
 * Copyright (c) 1999-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 *
 * Permission to use, copy, demonstrate, or modify this software
 * and its documentation for NON-COMMERCIAL or NON-PRODUCTION USE ONLY and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies and all terms of license agreed to when downloading 
 * this software are strictly followed.
 *
 * RMS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. RMS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.basics.*;
import java.awt.*;
import java.util.*;
import java.beans.PropertyChangeEvent;

/**
 * This provides a basic implementation of Lines that can have adornments.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class AdornedLine extends ConnectingLine {
	static final long serialVersionUID = -5836590406891850934L;

	/**
	 * The adornments associated with this AdornedLine.
	 */
	protected Vector adornments = new Vector();

	/**
	 * The figure the adornment rests next to.
	 */
	protected Figure figure;
/**
 * Creates a new AdornedLine initialized with the given values.
 *
 * @param beginX an integer representing the x coordinate the line should start at.
 * @param beginY an integer representing the y coordinate the line should start at.
 * @param endX an integer representing the x coordinate the line should end at.
 * @param endY an integer representing the y coordinate the line should end at.
 */
public AdornedLine(int beginX, int beginY, int endX, int endY) {
	super(beginX, beginY, endX, endY);
}
	/**
	 * Creates a new AdornedLine initialized with the given values.
	 *
	 * @param beginX an integer representing the x coordinate the line should start at.
	 * @param beginY an integer representing the y coordinate the line should start at.
	 * @param endX an integer representing the x coordinate the line should end at.
	 * @param endY an integer representing the y coordinate the line should end at.
	 * @param mustConnect specifies whether this line has to be connected in order to exist.
	 */
	public AdornedLine(int beginX, int beginY, int endX, int endY, boolean mustConnect) {
		super(beginX, beginY, endX, endY, mustConnect);
	}
	/**
	 * Creates a new AdornedLine initialized with the given values.
	 *
	 * @param begin a Locator representing the location the line should start at.
	 * @param end a Locator representing the location the line should end at.
	 */
	public AdornedLine(Locator begin, Locator end) {
		super(begin, end);
	}
	/**
	 * Creates a new AdornedLine initialized with the given values.
	 *
	 * @param begin a Locator representing the location the line should start at.
	 * @param end a Locator representing the location the line should end at.
	 * @param mustConnect specifies whether this line has to be connected in order to exist.
	 */
	public AdornedLine( Locator begin, Locator end, boolean mustConnect ) {
		super(begin, end, mustConnect);
	}
	/**
	 * Adds the given figure to the line as an adornment.
	 *
	 * @param adornment the Figure to add as an adornment.
	 */
	public void addAdornment( Figure adornment) {
		if ( ! adornments.contains( adornment ) ) {
			adornments.addElement(adornment);
		}
	}
	/** 
	 * Answers an enumeration of the adornments.
	 * 
	 * @return a FigureEnumeration over the adornments.
	 */
	protected FigureEnumeration adornments()  {
		return new FigureVectorEnumerator(adornments);
	}
	/** 
	 * Reshapes the receiver to the specified bounding box.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the figure
	 * @param height the height of the figure
	 */
	protected synchronized void basicReshape(int x, int y, int width, int height)  {
		int npoints = points.length;
		int xpoints[] = new int[npoints], ypoints[] = new int[npoints];
		Rectangle bounds = getLineBounds();
		double xScale = (double)width / (double)bounds.width;
		double yScale = (double)height / (double)bounds.height;
		for (int i=0; i < npoints; i++) {
			if (points[i] instanceof MovableLocator) {
				((MovableLocator)points[i]).x(x + (int)((points[i].x() - bounds.x) * xScale));
				((MovableLocator)points[i]).y(y + (int)((points[i].y() - bounds.y) * yScale));
			}
		}
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
		if (figure != null) {
			figure.addRelatedLocationListener(this);
			if (index == points.length - 1)
				points[index] = new EdgeLocator(locator, points[points.length - 2]);
		}
	}
	/**  
	 * Checks whether a specified x,y location is "inside" the
	 * receiver, where x and y are defined to be relative to the 
	 * coordinate system of the receiver.
	 * Checks the adornments as well.
	 * 
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 * @return	boolean value of <code>true</code> if the specified x,y location is
	 * inside this Figure;
	 * 			<code>false</code> otherwise.
	 */
	public boolean contains(int x, int y)  {
		for (int i=1; i < points.length; i++) {
			if (insideSegment(x,y,points[i-1].x(),points[i-1].y(),points[i].x(),points[i].y())) 
				return true;
		}
		FigureEnumeration adornmentsE = adornments();
		while ( adornmentsE.hasMoreElements() ) {
			if ( adornmentsE.nextElement().contains( x, y ) ) {
				return true;
			}
		}
		return false;
	}
	/** 
	 * Returns the current bounds of the receiver.
	 * Adds in the adornments.
	 * 
	 * @return	a Rectangle representing the current bounds of the receiver.
	 */
	public Rectangle getBounds()  {
		Rectangle bounds = getLineBounds(); // make a copy, otherwise we'll mess with the polygon's bounds attribute
		FigureEnumeration adornmentsE = adornments();
		while ( adornmentsE.hasMoreElements() ) {
			bounds.add( adornmentsE.nextElement().getBounds() );
		}
		return bounds;
	}
	/** 
	 * Returns the current bounds of the line only (excludes adornments).
	 * 
	 * @return	a Rectangle representing the current bounds of the line.
	 */
	protected Rectangle getLineBounds()  {
		return new Rectangle(getPolygon().getBounds()); // make a copy, otherwise we'll mess with the polygon's bounds attribute
	}
	/** 
	 * Answer a point indicating the location of the receiver... typically the topLeft.
	 * NOTE: This may not correspond to the point indicated by getLocator() as this method
	 * is often used to determine the position before a Locator has already been affected.
	 * 
	 * @return	a Point indicating the location of the receiver
	 */
	protected Point getLocation() {
		Rectangle myBounds = getLineBounds();
		return new java.awt.Point(myBounds.x,myBounds.y);
	}
	/** 
	 * Paints the receiver.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g)  {
		super.paint(g);
		paintAdornments(g);
	}
	/** 
	 * Paints the adornments of the receiver.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paintAdornments(Graphics g)  {
		FigureEnumeration adornmentsE = adornments();
		while ( adornmentsE.hasMoreElements() ) {
			adornmentsE.nextElement().paint(g);
		}
	}
	/**
	 * Remove the specified adornment.
	 *
	 * @param adornment the adornment to remove.
	 */
	public void removeAdornment( Figure adornment) {
		adornments.removeElement(adornment);
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
}

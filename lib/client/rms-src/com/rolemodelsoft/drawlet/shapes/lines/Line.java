package com.rolemodelsoft.drawlet.shapes.lines;

import java.beans.*;
/**
 * @(#)Line.java
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
 
import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.shapes.*;
import com.rolemodelsoft.drawlet.util.Duplicatable;
import java.awt.*;
import java.util.Hashtable;

/**
 * This provides a basic implementation of Lines.
 * It is not expected that the locators that make up this line be connected to
 * other figures.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class Line extends LinearShape {
	static final long serialVersionUID = 4513748053006007765L;
	
	/**
	 * The locators which define the points of the line.
	 */
	protected Locator points[] = new Locator[0];

	/**
	 * A cache to keep the bounds for efficient reference.
	 */
	transient protected Polygon polygon;

	/** 
	 * Constructs and initializes a new instance of a line.
	 *
	 * @param locators the locators which define the points of the line
	 * @exception IllegalArgumentException If their are not at least 2 locators.
	 */
	public Line(Locator locators[]) {
		if (locators.length < 2)
			throw new IllegalArgumentException("Must supply at least two locators"); 
		this.points = locators;
	}
	/** 
	 * Constructs and initializes a new instance of a line.
	 *
	 * @param beginX the x coordinate for the first point defining the line
	 * @param beginY the y coordinate for the first point defining the line
	 * @param endX the x coordinate for the second point defining the line
	 * @param endY the y coordinate for the second point defining the line
	 */
	public Line(int beginX, int beginY, int endX, int endY) {
		this(new DrawingPoint(beginX,beginY),new DrawingPoint(endX,endY));
	}
	/** 
	 * Constructs and initializes a new instance of a line.
	 *
	 * @param begin the locator which is the first point defining the line
	 * @param end the locator which is the second point defining the line
	 */
	public Line(Locator begin, Locator end) {
		this.points = new Locator[2];
		this.basicSetLocator(0,begin);
		this.basicSetLocator(1,end);
	}
	/**
	 * Add the locator at the given position.
	 * 
	 * @param index the index of the locator desired.
	 * @param locator the new Locator.
	 */
	protected synchronized void basicAddLocator(int index, Locator locator)  {
		Locator newPoints[] = new Locator[points.length + 1];
		System.arraycopy(points,0,newPoints,0,index);
		System.arraycopy(points,index,newPoints,index+1, points.length - index);
		points = newPoints;
		basicSetLocator(index,locator);
	}
	/**
	 * Remove the locator at the given position.
	 * 
	 * @param index the index of the locator no longer desired.
	 */
	protected synchronized void basicRemoveLocator(int index)  {
		Locator newPoints[] = new Locator[points.length - 1];
		System.arraycopy(points,0,newPoints,0,index - 1);
		System.arraycopy(points,index + 1,newPoints,index, points.length - index);
		points = newPoints;
	}
	/** 
	 * Reshapes the receiver to the specified bounding box.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the receiver
	 * @param height the height of the receiver
	 * @see #getBounds
	 */
	protected synchronized void basicReshape(int x, int y, int width, int height)  {
		int npoints = points.length;
		int xpoints[] = new int[npoints], ypoints[] = new int[npoints];
		Rectangle bounds = getBounds();
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
	 * Set the locator at the given position.
	 * 
	 * @param index the index of the locator desired.
	 * @param locator the new Locator.
	 */
	protected synchronized void basicSetLocator(int index, Locator locator)  {
		points[index] = locator;
	}
	/** 
	 * Moves the receiver in the x and y direction.
	 * 
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 */
	protected synchronized void basicTranslate(int x, int y)  {
		for (int i=0; i < points.length; i++) {
			if (points[i] instanceof MovableLocator) {
				((MovableLocator)points[i]).translate(x,y);
			}
		}
	}
	/**  
	 * Checks whether a specified x,y location is "inside" this
	 * Figure, where x and y are defined to be relative to the 
	 * coordinate system of this figure.  
	 * 
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 * 
	 * @return	boolean value of <code>true</code> if the specified x,y location is
	 * "inside this Figure;
	 * 			<code>false</code> otherwise.
	 */
	public boolean contains(int x, int y)  {
		for (int i=1; i < points.length; i++) {
			if (insideSegment(x,y,points[i-1].x(),points[i-1].y(),points[i].x(),points[i].y())) 
				return true;
		}
		return false;
	}
	/**
	 * Duplicates the receiver in the given Hashtable. 
	 * 
	 * @param duplicates the Hashtable to put the new duplicate in
	 * @return	an Object which is a duplicate of the receiver
	 */
	public synchronized Object duplicateIn(Hashtable duplicates) {
		Line duplicate = (Line)super.duplicateIn(duplicates);
		duplicate.points = new Locator[points.length];
		for (int i=0; i < points.length; i++) 
			duplicate.points[i] = (Locator)points[i].duplicateIn(duplicates);
		duplicate.resetBoundsCache();
		return duplicate;
	}
	/**
	 * Answers the expected number of significant duplicates when duplicating
	 * the receiver.
	 * Subclasses may wish to override.
	 * 
	 * @returns an integer representing an estimate of the number of objects
	 * to be duplicated
	 */
	protected int estimatedDuplicateSize() {
		return super.estimatedDuplicateSize() + points.length;
	}
	/** 
	 * Returns the current bounds of the receiver.
	 * 
	 * @return	a Rectangle representing the current bounds of the receiver.
	 */
	public Rectangle getBounds()  {
		return new Rectangle(getPolygon().getBounds()); // make a copy, otherwise we'll mess with the polygon's bounds attribute
	}
	/**
	 * Answer the indexth locator.
	 * 
	 * @param index the index of the locator desired.
	 * @return	the Locator at the given index
	 */
	public Locator getLocator(int index)  {
		return points[index];
	}
	/**
	 * Answer the number of points which define the receiver.
	 * 
	 * @return	an integer representing the number of points which define the
	 * receiver
	 */
	public int getNumberOfPoints()  {
		return points.length;
	}
	/**  
	 * Answer the Polygon associated with the receiver.
	 * 
	 * @return	the Polygon associated with the receiver.
	 */
	protected Polygon getPolygon() {
	if (polygon == null) {
		int npoints = points.length;
		int xpoints[] = new int[npoints];
		int ypoints[] = new int[npoints];
		for (int i=0; i < npoints; i++) {
			xpoints[i] = points[i].x();
			ypoints[i] = points[i].y();
		}
		polygon = new Polygon(xpoints,ypoints,npoints);
	}
	return polygon;
	}
	/** 
	 * Answers whether the receiver intersects a Rectangular area.
	 * 
	 * @param bounds the Rectangular area
	 * @return	boolean value of <code>true</code> if the receiver intersects the
	 * given rectangular area;
	 * 			<code>false</code> otherwise.
	 */
	public boolean intersects(Rectangle bounds) {
		return getBounds().intersects(bounds);
	}
	/** 
	 * Paints the receiver.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g)  {
		super.paint(g);
		int npoints = points.length;
		int xpoints[] = new int[npoints];
		int ypoints[] = new int[npoints];
		for (int i=0; i < npoints; i++) {
			xpoints[i] = points[i].x();
			ypoints[i] = points[i].y();
		}
		g.drawPolyline(xpoints, ypoints, npoints);
	}
	/**
	 * After a series of Figures are duplicated, this can be sent to each of the
	 * duplicates to resolve any changes it might like to reconcile.
	 * 
	 * In this case, remove any dependency on any figures defining original 
	 * points.  If there is an available duplicate corresponding to the original, 
	 * use it as the original was used.  If not, convert it to a non-Figure-
	 * dependent point.
	 *
	 * @param duplicates a Hashtable where originals as keys and duplicates as elements
	 */
	public void postDuplicate(Hashtable duplicates) {
		super.postDuplicate(duplicates);
		for (int i=0; i < points.length; i++) {
			Figure figure = figureFromLocator(points[i]);
			if (figure != null) {
				if (!duplicates.containsKey(figure)) {
					points[i] = new DrawingPoint(points[i].x(),points[i].y());
				}
			}
		}
		for (int i=0; i < points.length; i++) 
			points[i].postDuplicate(duplicates);
	}
	/**
	 * Flush caches with respect to determining bounds.
	 */
	protected void resetBoundsCache() {
		polygon = null;
	}
	/**
	 * Flush caches with respect to determining location.
	 */
	protected void resetLocationCache() {
		polygon = null;
	}
	/**
	 * Flush caches with respect to determining size.
	 */
	protected void resetSizeCache() {
		polygon = null;
	}
}

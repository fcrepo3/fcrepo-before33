package com.rolemodelsoft.drawlet.shapes.lines;

/*
 * @(#)LinearShape.java	
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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;

/**
 * This provides basic default functionality for LineFigures that are assumed 
 * to be moveable and reshapeable with observers that want to know when their 
 * locations or shapes change.  It provides most of its functionality based on 
 * its bounds, plus some additional facilities which assume the line is made
 * up of multiple locators, and forces concrete subclasses to define, 
 * at a minimum:
 *	paint(Graphics);
 *	getLocator(int);
 *	getNumberOfPoints();
 *	basicAddLocator(Locator,int);
 *	basicSetLocator(Locator,int);
 *	basicRemoveLocator(int);
 *
 * @version 	1.1.6, 12/29/98
 */
 
public abstract class LinearShape extends AbstractShape implements LineFigure {

	/**
	 * The color with which to paint the line.
	 */
	protected Color lineColor = defaultLineColor();

	/**
	 * Add the locator at the appropriate position.
	 * This is a TemplateMethod with hooks:
	 * 	resetBoundsCache()
	 * 	basicAddLocator()
	 *  changedShape()
	 *
	 * @param locator the new Locator to add.
	 * @param index the index of the locator desired.
	 */
	public void addLocator(int index, Locator locator) {
		Rectangle oldBounds = getBounds();
		if (!oldBounds.contains(locator.x(), locator.y()))
			resetBoundsCache();
		basicAddLocator(index, locator);
		changedShape(oldBounds);
	}
	/**
	 * Add the locator at the end.
	 * 
	 * @param locator the new Locator to add.
	 */
	public void addLocator(Locator locator) {
		addLocator(getNumberOfPoints(), locator);
	}
	/**
	 * Add the locator at the given position.
	 * 
	 * @param index the index to add the Locator at.
	 * @param locator the new Locator.
	 */
	protected abstract void basicAddLocator(int index, Locator locator);
	/**
	 * Remove the locator at the given position.
	 * 
	 * @param index the index of the locator no longer desired.
	 */
	protected abstract void basicRemoveLocator(int index);
	/** 
	 * Reshapes the receiver to the specified bounding box.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the figure
	 * @param height the height of the figure
	 * @see #getBounds
	 */
	protected synchronized void basicReshape(int x, int y, int width, int height)  {
		int npoints = getNumberOfPoints();
		Rectangle bounds = getBounds();
		double xScale = (double)width / (double)bounds.width;
		double yScale = (double)height / (double)bounds.height;
		for (int i=0; i < npoints; i++) {
			Locator locator = getLocator(i);
			if (locator instanceof MovableLocator) {
				((MovableLocator)locator).x(x + (int)((locator.x() - bounds.x) * xScale));
				((MovableLocator)locator).y(y + (int)((locator.y() - bounds.y) * yScale));
			}
		}
	}
	/**
	 * Set the locator at the given position.
	 * 
	 * @param index the index of the locator to be set.
	 * @param locator the new Locator.
	 */
	protected abstract void basicSetLocator(int index, Locator locator);
	/** 
	 * Moves the receiver in the x and y direction.
	 * 
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 */
	protected synchronized void basicTranslate(int x, int y)  {
		int npoints = getNumberOfPoints();
		for (int i=0; i < npoints; i++) {
			Locator locator = getLocator(i);
			if (locator instanceof MovableLocator) 
				((MovableLocator)locator).translate(x,y);
		}
	}
	/**  
	 * Checks whether a specified x,y location is "inside" the
	 * receiver, where x and y are defined to be relative to the 
	 * coordinate system of the receiver.  
	 *
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 * @return	boolean value of <code>true</code> if the specified x,y
	 * location is "inside" this figure;
	 * 			<code>false</code> otherwise.
	 */
	public boolean contains(int x, int y)  {
		int size = getNumberOfPoints();
		for (int i=1; i < size; i++) {
			Locator previous = getLocator(i-1);
			Locator current = getLocator(i);
			if (insideSegment(x,y,previous.x(),previous.y(),current.x(),current.y())) 
				return true;
		}
		return false;
	}
	/**
	 * Answer the default/initial value for lineColor
	 * 
	 * @return	a Color representing the default/initial value for lineColor
	 */
	protected Color defaultLineColor() {
		return Color.black;
	}
	/** 
	 * Returns the current bounds of the receiver.
	 * 
	 * @return	a Rectangle representing the current bounds of the receiver.
	 */
	public Rectangle getBounds()  {
		int npoints = getNumberOfPoints();
		Locator locator = getLocator(0);
		Rectangle bounds = new Rectangle(locator.x(),locator.y(),1,1);
		for (int i=1; i < npoints; i++) {
			locator = getLocator(i);
			bounds.add(locator.x(),locator.y());
		}
		return bounds;
	}
	/** 
	 * Answer the handles associated with the receiver.
	 * 
	 * @return	an array containing the Handles associated with the
	 * receiver
	 */
	public Handle[] getHandles() {
		int numberOfHandles = getNumberOfPoints();
		Handle handles[] = new Handle[numberOfHandles];
		for (int i=0; i < numberOfHandles; i++)
			handles[i] = new LinePointHandle(this,i);
		return handles;
	}
	/**
	 * Answer the Color to use when drawing lines.
	 * 
	 * @return	the Color to use when drawing lines
	 */
	public Color getLineColor() {
		return lineColor;
	}
	/**
	 * Answer the indexth locator.
	 * 
	 * @param index the index of the locator desired.
	 * @return	the Locator at the indexth position
	 */
	public abstract Locator getLocator(int index);
	/**
	 * Answer the number of points which define the receiver.
	 * 
	 * @return	an integer representing the number of points which
	 * define the receiver
	 */
	public abstract int getNumberOfPoints();
	/** 
	 * Answer the style which defines how to paint the figure.
	 * 
	 * @return	the DrawingStyle which defines how to paint the figure
	 */
	public DrawingStyle getStyle()  {
		DrawingStyle style = super.getStyle();
		style.setLineColor(getLineColor());
		return style;
	}
	/**  
	 * Answer whether the point specified is inside the specified line segment.
	 * 
	 * @param x the x coordinate of the point being tested.
	 * @param y the y coordinate of the point being tested.
	 * @param x0 the x coordinate of the beginning of the line segment.
	 * @param y0 the y coordinate of the beginning of the line segment.
	 * @param x1 the x coordinate of the end of the line segment.
	 * @param y2 the y coordinate of the end of the line segment.
	 * @return	boolean value of <code>true</code> if the point specified is
	 * inside the line segment specified;
	 * 			<code>false</code> otherwise.
	 */
	protected boolean insideSegment(int x, int y, int x0, int y0, int x1, int y1) {
		/*
		 * first check if we have a vertical segment and act accordingly
		 */
		if (x1 == x0) return (x == x0) && (y >= Math.min(y0,y1)) && (y <= Math.max(y0,y1));
		/*
		 * before going on, verify that x,y is inside the bounding box,
		 * if not, it can't possible be on the line segment.
		 */
		Rectangle bounds = new Rectangle(x0,y0,1,1);
		int fudge = (int)Math.round(insideTolerance());
		bounds.add(x1,y1);
		bounds.grow(fudge,fudge);
		if (!bounds.contains(x,y)) return false;
		/*
		 * Now check the basic "y (or f(x)) = mx + b" definition of the line.
		 * Since we know it's within the bounds of the segment, 
		 * if it's on the line, it's on the segment.
		 */
		double slope = (double)(y1-y0) / (double)(x1-x0);
		double fx = (slope * (double)(x-x0)) + (double)y0;
		return Math.abs(fx - (double)y) < insideTolerance(slope);
	}
	/**  
	 * Answer a number to define how close we have to be to the geometric line 
	 * to be considered inside it.
	 * 
	 * @return	a double representing a number to define how close we have to
	 * be to be considered inside it
	 */
	protected double insideTolerance() {
		return 2.5;
	}
	/**  
	 * Answer a number to define how close we have to be to the geometric line 
	 * to be considered inside it, given a particular slope.
	 *
	 * @return	a double representing a number to define how close we have to
	 * the geometric line defined by the given slope in order to be considered
	 * inside it
	 */
	protected double insideTolerance(double slope) {
		return Math.max(insideTolerance(),Math.abs(slope));
	}
	/** 
	 * Answers whether the receiver intersects a rectangular area.
	 * 
	 * @param bounds the rectangular area.
	 * @return	boolean value of <code>true</code> if the receiver intersects the
	 * specified rectangular area;
	 * 			<code>false</code> otherwise.
	 */
	public boolean intersects(Rectangle bounds) {
		return getBounds().intersects(bounds);
	}
	/** 
	 * Answers a locator corresponding to a significant point on the receiver.
	 * By default, answer a point with the same relative position as x and y
	 * are at the time of the request.
	 * 
	 * @param x the x coordinate of the requested locator
	 * @param y the y coordinate of the requested locator
	 * @return	a Locator corresponding to a significant point on the receiver
	 */
	public Locator locatorAt(int x, int y) {
		int numberOfSegments = getNumberOfPoints();
		int segment = 1;
		Locator previous = getLocator(0);
		Locator current = getLocator(1);
		for (; segment < numberOfSegments; segment++) {
			previous = getLocator(segment-1);
			current = getLocator(segment);
			if (insideSegment(x,y,previous.x(),previous.y(),current.x(),current.y())) 
				break;
		}
		if (segment == numberOfSegments)
			return null;
		int currentX = current.x();
		int currentY = current.y();
		int previousX = previous.x();
		int previousY = previous.y();

	//	double relativeX = 0.5;
	//	double relativeY = 0.5;
		double relativeX = 0.0;
		double relativeY = 0.0;
		if (currentX != previousX)
			relativeX = Math.abs(((double)(x - previousX)) / (double)(currentX - previousX));
		if (currentY != previousY)
			relativeY = Math.abs(((double)(y - previousY)) / (double)(currentY - previousY));
		return new LineFigureRelativePoint(this, segment, relativeX, relativeY);
	}
	/** 
	 * Paints the receiver.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g) {
		g.setColor(getLineColor());
		// draw figure
	}
	/**
	 * Remove the locator at the given position.
	 * This is a TemplateMethod with hooks:
	 * 	resetBoundsCache()
	 * 	basicRemoveLocator()
	 *  changedShape()
	 *
	 * @param index the index of the locator to be removed.
	 */
	public void removeLocator(int index) {
		Rectangle oldBounds = getBounds();
		resetBoundsCache();
		basicRemoveLocator(index);
		changedShape(oldBounds);
	}
	/** 
	 * Answers a Locator corresponding to a significant point on the receiver 
	 * that will serve as a connection to the other figure.
	 * By default, make it the middle of the receiver.
	 * Subclasses may wish to do something more meaningful.
	 *
	 * @param x the x coordinate of the requested locator
	 * @param y the y coordinate of the requested locator
	 * @return	a Locator corresponding to a significant point on the
	 * receiver that will serve as a connection to the given figure
	 */
	public Locator requestConnection(Figure requestor, int x, int y) {
		if (requestor == this)
			return null;
		return locatorAt(x,y);
	}
	/**
	 * Set the Color to use when drawing lines.
	 * 
	 * @param color the color
	 */
	public void setLineColor(Color color) {
		Color oldColor = lineColor;
		lineColor = color;
		firePropertyChange(LINE_COLOR_PROPERTY, oldColor, color);
	}
	/**
	 * Set the locator at the appropriate position.
	 * This is a TemplateMethod with hooks:
	 * 	resetBoundsCache()
	 * 	basicSetLocator()
	 *  changedShape()
	 *
	 * @param index the index of the locator desired.
	 * @param locator the new Locator.
	 */
	public void setLocator(int index, Locator locator) {
		Rectangle oldBounds = getBounds();
		resetBoundsCache();
		basicSetLocator(index, locator);
		changedShape(oldBounds);
	}
	/** 
	 * Set the DrawingStyle defining how to paint the receuver.
	 * 
	 * @param style the specified DrawingStyle.
	 */
	public void setStyle(DrawingStyle style) {
		DrawingStyle oldStyle = getStyle();
		if (style != null) {
			setLineColor(style.getLineColor());
		}
		firePropertyChange(STYLE_PROPERTY, oldStyle, style);
	}
}

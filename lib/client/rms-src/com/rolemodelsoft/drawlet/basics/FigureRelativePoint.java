package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)FigureRelativePoint.java
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
import java.awt.*;
import java.util.Hashtable;

/**
 * This class implements Locator by providing x and y coordinates that are
 * relative to a Figure.  Although it is assumed that the normal use will have
 * these locators within the bounds of the Figure, that is not necessary.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class FigureRelativePoint extends AbstractLocator implements FigureHolder {
	static final long serialVersionUID = -3811906450260337242L;
	
	/**
	 * The figure from which we derive our coordinates
	 */
	protected Figure figure;

	/**
	 * The relative x position from the top-left to bottom-right from
	 * which to derive our coordinates.  These will most likely be
	 * between 0.0 and 1.0, but do not have to be.
	 */
	protected double relativeX = defaultRelativeX();

	/**
	 * The relative y position from the top-left to bottom-right from
	 * which to derive our coordinates.  These will most likely be
	 * between 0.0 and 1.0, but do not have to be.
	 */
	protected double relativeY = defaultRelativeY();

	/**
	 * The x offset from the purely relative position.
	 */
	protected int offsetX = defaultOffsetX();

	/**
	 * The y offset from the purely relative position.
	 */
	protected int offsetY = defaultOffsetY();

	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a figure
	 *
	 * @param figure the figure to which the instance will be relative
	 * @see #defaultRelativeX
	 * @see #defaultRelativeY
	 * @see #defaultOffsetX
	 * @see #defaultOffsetY
	 */
	public FigureRelativePoint(Figure figure) {
		this.figure = figure;
	}
	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a figure
	 *
	 * @param figure the figure to which the instance will be relative
	 * @param relativeX the percentage of the width of the figure to determine X
	 * @param relativeY the percentage of the height of the figure to determine Y
	 * @see #defaultOffsetX
	 * @see #defaultOffsetY
	 */
	public FigureRelativePoint(Figure figure, double relativeX, double relativeY) {
	this(figure);
	this.relativeX = relativeX;
	this.relativeY = relativeY;
	}
	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a figure
	 *
	 * @param figure the figure to which the instance will be relative
	 * @param relativeX the percentage of the width of the figure to determine X
	 * @param relativeY the percentage of the height of the figure to determine Y
	 * @param offsetX the offset (added to relative X) in the x direction
	 * @param offsetY the offset (added to relative Y) in the y direction
	 */
	public FigureRelativePoint(Figure figure, double relativeX, double relativeY, int offsetX, int offsetY) {
	this(figure,relativeX,relativeY);
	this.offsetX = offsetX;
	this.offsetY = offsetY;
	}
	/** 
	 * Constructs and initializes a new instance of a locator which is
	 * relative to a figure
	 *
	 * @param figure the figure to which the instance will be relative
	 * @param offsetX the offset (added to relative X) in the x direction
	 * @param offsetY the offset (added to relative Y) in the y direction
	 * @see #defaultRelativeX
	 * @see #defaultRelativeY
	 */
	public FigureRelativePoint(Figure figure, int offsetX, int offsetY) {
	this(figure);
	this.offsetX = offsetX;
	this.offsetY = offsetY;
	}
	/** 
	 * Answer the default/initial value of offsetX.
	 * Subclasses may wish to consider changing this from 0
	 * 
	 * @return	an integer representing the default/initial
	 * value of offsetX
	 */
	protected int defaultOffsetX() {
	return 0;
	}
	/** 
	 * Answer the default/initial value of offsetY.
	 * Subclasses may wish to consider changing this from 0
	 * 
	 * @return	an integer representing the default/initial
	 * value of offsetY
	 */
	protected int defaultOffsetY() {
	return 0;
	}
	/** 
	 * Answer the default/initial value of relativeX.
	 * Subclasses may wish to consider changing this from 0.0
	 * 
	 * @return	a double representing the default/initial
	 * value of relativeX
	 */
	protected double defaultRelativeX() {
	return 0.0;
	}
	/** 
	 * Answer the default/initial value of relativeY.
	 * Subclasses may wish to consider changing this from 0.0
	 * 
	 * @return	a double representing the default/initial
	 * value of relativeY
	 */
	protected double defaultRelativeY() {
	return 0.0;
	}
	/**
	 * Duplicates the receiver.
	 * 
	 * @return	an Object which is a duplicate of the receiver
	 */
	public synchronized Object duplicate() {
	try { 
	    return clone();
	} catch (CloneNotSupportedException e) { 
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
	}
	/**
	 * Duplicates the receiver in the given <code>Hashtable</code>.
	 * 
	 * @param duplicates the Hashtable to put the new duplicate in
	 * @return	an Object which is a duplicate of the receiver
	 */
	public synchronized Object duplicateIn(Hashtable duplicates) {
		Object dup = duplicate();
		duplicates.put(this,dup);
		return dup;
	}
	/** 
	 * Answer the figure to which the receiver is relative.
	 * 
	 * @return	the Figure to which the receiver is relative
	 */
	public Figure getFigure()  {
		return figure;
	}
	/**
	 * After a series of Objects are duplicated, this can be sent to each of the
	 * duplicates to resolve any changes it might like to reconcile.
	 * In this case, get the duplicate of the current figure if there is one.
	 * Keep the original if not.
	 *
	 * @param duplicates a Hashtable where originals as keys and duplicates as elements
	 */
	public void postDuplicate(Hashtable duplicates) {
		Figure duplicate = (Figure)duplicates.get(figure);
		if (duplicate != null)
			setFigure(duplicate);
	}
	/** 
	 * Set the figure the receiver is holding.
	 *
	 * @param figure the Figure to hold
	 */
	public void setFigure(Figure figure) {
		this.figure = figure;
	}
	/** 
	 * Answer the x coordinate.
	 * 
	 * @return	an integer representing the x coordinate
	 */
	public int x()  {
		Rectangle bounds = figure.getBounds();
		return (int)(bounds.width * relativeX) + offsetX + bounds.x;
	}
	/** 
	 * Answer the y coordinate.
	 * 
	 * @return	an integer representing the y coordinate
	 */
	public int y()  {
		Rectangle bounds = figure.getBounds();
		return (int)(bounds.height * relativeY) + offsetY + bounds.y;
	}
}

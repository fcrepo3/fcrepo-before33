package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)RelativePoint.java
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
import java.util.Hashtable;

/**
 * This class implements RelativeLocator and MovableLocator by providing 
 * movable x and y offsets to some base locator.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class RelativePoint extends AbstractLocator implements MovableLocator, RelativeLocator {
	static final long serialVersionUID = 1110101860794525149L;
	
	/**
	 * The locator to which this is relative.
	 */
	protected Locator base;

	/**
	 * The x offset from the base locator.
	 */
	protected int offsetX = defaultOffsetX();

	/**
	 * The y offset from the base locator.
	 */
	protected int offsetY = defaultOffsetY();

	/**
	 * Constructs and initializes a RelativePoint with the specified parameters.
	 * 
	 * @param base the Locator to which the constructed point is relative.
	 * @param x the x coordinate of the offset
	 * @param y the y coordinate of the offset
	 */
	public RelativePoint(Locator base, int x, int y) {
	this.base = base;
	this.offsetX = x;
	this.offsetY = y;
	}
	/** 
	 * Answer the default/initial value of offsetX.
	 * Subclasses may wish to consider changing this from 0
	 * 
	 * @return	an integer representing the default/initial value of offsetX
	 */
	protected int defaultOffsetX() {
	return 0;
	}
	/** 
	 * Answer the default/initial value of offsetY.
	 * Subclasses may wish to consider changing this from 0
	 * 
	 * @return	an integer representing the default/initial value of offsetY
	 */
	protected int defaultOffsetY() {
	return 0;
	}
	/**
	 * Duplicates the receiver.  This means duplicating the base.
	 * 
	 * @return	an Object which is a duplicate of the receiver
	 */
	public synchronized Object duplicate() {
	try {
	    RelativePoint duplicate = (RelativePoint)clone();
	    duplicate.base = (Locator)base.duplicate();
	    return duplicate;
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
		try {
		    RelativePoint duplicate = (RelativePoint)clone();
		    duplicates.put(this,duplicate);
		    duplicate.base = (Locator)base.duplicateIn(duplicates);
		    return duplicate;
		} catch (CloneNotSupportedException e) { 
		    // this shouldn't happen, since we are Cloneable
		    throw new InternalError();
		}
	}
	/** 
	 * Answer the base concrete locator of the receiver.
	 * This is a recursive operation to get to the real base in a potential
	 * chain of RelativeLocators
	 * 
	 * @return	the base concrete Locator of the receiver
	 */
	public Locator getBase()  {
		if (base instanceof RelativeLocator)
			return ((RelativeLocator)base).getBase();
		return base;
	}
	/** 
	 * Moves the receiver to the x and y coordinates
	 * 
	 * @param x the new x coordinate
	 * @param y the new x coordinate
	 */
	public synchronized void move(int x, int y)  {
	x(x);
	y(y);
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
		Figure figure = AbstractFigure.figureFromLocator(base);
		if (figure != null) {
			if (!duplicates.containsKey(figure))
				base = new DrawingPoint(base.x(),base.y());
		}
		base.postDuplicate(duplicates);
	}
	/** 
	 * Moves the receiver in the x and y direction.
	 * 
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 */
	public synchronized void translate(int x, int y)  {
	offsetX += x;
	offsetY += y;
	}
	/** 
	 * Answer the x coordinate.
	 * 
	 * @return	an integer representing the x coordinate
	 */
	public int x()  {
	return base.x() + offsetX;
	}
	/** 
	 * Set the x coordinate.
	 * 
	 * @param x its new desired x coordinate.
	 */
	public void x(int x)  {
	offsetX = x - base.x();
	}
	/** 
	 * Answer the y coordinate.
	 * 
	 * @return	an integer representing the y coordinate
	 */
	public int y()  {
	return base.y() + offsetY;
	}
	/** 
	 * Set the y coordinate.
	 * 
	 * @param y its new desired y coordinate.
	 */
	public void y(int y)  {
	offsetY = y - base.y();
	}
}

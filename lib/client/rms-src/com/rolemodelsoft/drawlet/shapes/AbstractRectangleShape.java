package com.rolemodelsoft.drawlet.shapes;

/**
 * @(#)AbstractRectangleShape.java
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
import com.rolemodelsoft.drawlet.shapes.*;
import java.awt.*;

/**
 * This provides a basic abstract implementation of rectangular bounded Figures.
 * Although this is an abstract class, it is
 * acknowledged that there are other implementations (e.g. ones that 
 * uses Locators) which are more flexible.  Therefore, one may wish to consider
 * subclassing the superclass instead.
 *
 * This provides most of its functionality based on a simple x, y, width, height attributes,
 * and forces concrete subclasses to define, at a minimum:
 *	paintStrokes(Graphics);
 *	paintFilled(Graphics);
 *
 */
public abstract class AbstractRectangleShape extends FilledShape {
	/**
	 * The x coordinate for the shape
	 */
	protected int x = defaultX();

	/**
	 * The y coordinate for the shape
	 */
	protected int y = defaultY();

	/**
	 * The width of the shape
	 */
	protected int width = defaultWidth();
	
	
	/**
	 * The height of the shape
	 */
	protected int height = defaultHeight();
/**
 * Creates a new, default AbstractRectangleShape.
 */
public AbstractRectangleShape() {
}
/**
 * Creates a new AbstractRectangeShape initialized with the given values
 * for x, y, width and height.
 *
 * @param x an integer representing the x for this AbstractRectangeShape.
 * @param y an integer representing the y for this AbstractRectangeShape.
 * @param width an integer representing the width for this AbstractRectangeShape.
 * @param height an integer representing the height for this AbstractRectangeShape.
 */
public AbstractRectangleShape(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
}
/**
 * Creates a new AbstractRectangeShape initialized with the given Rectangle.
 *
 * @param rectangle a Rectangle representing the bounds to be used for initialization.
 */
public AbstractRectangleShape(Rectangle rectangle) {
	this(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
}
	/** 
	 * Moves the receiver to a new location. The x and y coordinates
	 * are in the parent's coordinate space.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	protected synchronized void basicMove(int x, int y)  {
		this.x = x;
		this.y = y;
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
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	/** 
	 * Resizes the receiver to the specified width and height.
	 * 
	 * @param width the width of the figure
	 * @param height the height of the figure
	 */
	protected synchronized void basicResize(int width, int height)  {
		this.width = width;
		this.height = height;
	}
	/** 
	 * Moves the receiver in the x and y direction.
	 * 
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 */
	protected synchronized void basicTranslate(int x, int y) {
		this.x += x;
		this.y += y;
	}
	/**
	 * Denote that the receiver has changed. Assume that
	 * the shape has changed even if the bounds haven't.
	 * 
	 * @param oldBounds the old bounds.
	 */
	protected void changedShape(Rectangle oldBounds) {
		if (oldBounds != null && oldBounds.equals(getBounds())) {
		    return;
		}
		super.changedShape( oldBounds );
	}
	/**
	 * Answer the default/initial value for height.
	 * 
	 * @return	an integer representing the default/initial value for height.
	 */
	protected int defaultHeight() {
		return 10;
	}
	/**
	 * Answer the default/initial value for width.
	 * 
	 * @return	an integer representing the default/initial value for width.
	 */
	protected int defaultWidth() {
		return 10;
	}
	/**
	 * Answer the default/initial value for x.
	 * 
	 * @return	an integer representing the default/initial value for x.
	 */
	protected int defaultX() {
		return 0;
	}
	/**
	 * Answer the default/initial value for y.
	 * 
	 * @return	an integer representing the default/initial value for y.
	 */
	protected int defaultY() {
		return 0;
	}
	/** 
	 * Returns the current bounds of the receiver.
	 * 
	 * @return	a Rectangle representing the current bounds of the receiver.
	 */
	public Rectangle getBounds()  {
		return new Rectangle(x,y,width,height);
	}
	/** 
	 * Returns the current size of the receiver.
	 * 
	 * @return	a Dimension representing the current size of the receiver.
	 */
	public Dimension getSize()  {
		return new Dimension(width,height);
	}
}

package com.rolemodelsoft.drawlet.shapes.ellipses;

/**
 * @(#)Ellipse.java
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
import com.rolemodelsoft.drawlet.shapes.*;
import java.awt.*;

/**
 * This provides a basic concrete implementation of Ellipses that are 
 * assumed to be movable and reshapable with observers that want to know when 
 * their locations or shapes change.
 * NOTE: intersection algorithms are currently incomplete and only determine if
 * the Rectangular bounds intersect.
 */

 public class Ellipse extends AbstractRectangleShape {
	static final long serialVersionUID = 6345684636936094954L;
/**
 * Creates a new, default Ellipse.
 */
public Ellipse() {
}
/**
 * Creates a new Ellipse initialized to the given values.
 * 
 * @param x an integer representing the x coordinate for the new Ellipse.
 * @param y an integer representing the y coordinate for the new Ellipse.
 * @param width an integer representing the width for the new Ellipse.
 * @param height an integer representing the height for the new Ellipse.
 */
public Ellipse(int x, int y, int width, int height) {
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
}
/**
 * Creates a new Ellipse initialized to the given value.
 * 
 * @param rectangle a Rectangle representing the bounds for the new Ellipse.
 */
public Ellipse(Rectangle rectangle) {
	this(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
}
	/**  
	 * Checks whether a specified x,y location is "inside" this
	 * Figure. <code>x</code> and <code>y</code> are defined to be relative to the 
	 * coordinate system of this figure.  This is not guaranteed to be 100% accurate around the edges
	 * due to rounding errors, but shouldn't be off by more than a single pixel.
	 *
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 * @return	boolean value of <code>true</code> if the specified x,y
	 * location is "inside" this Figure;
	 * 			<code>false</code> otherwise.
	 * @see #isWithin
	 */
	public boolean contains(int x, int y) {
		if ( ! super.contains( x, y ) )
			return false;
		/*
		 * using standard geometric formula for ellipse
		 *   x^2   y^2
		 *   --- + --- = 1
		 *   a^2   b^2
		 * where a and b are x and y coordinates where y=0 and x=0 respectively...
		 * multiplying by a^2*b^2 and using <= to determine points on or inside yields 
		 *   b^2*x^2 + a^2*y^2 <= a^2*b^2
		 */
		int a = ( getWidth() + 1 ) / 2;  // be conservative in finding points by rounding up
		int b = ( getHeight() + 1 ) / 2;
		int aSquared = a * a;
		int bSquared = b * b;
		int centerX = getLeft() + a;
		int normalizedX = x - centerX;
		int centerY = getTop() + b;
		int normalizedY = y - centerY;
		return ( ( normalizedX * normalizedX ) * bSquared ) + ( ( normalizedY * normalizedY ) * aSquared ) <= aSquared * bSquared;
	}
	/**  
	 * Checks whether a specified Rectangle is "inside" this Figure, 
	 * where the Rectangle and this Figure are in the same coordinate system  
	 * In addition to checking topLeft and bottomRight, check topRight and bottomLeft.
	 * If all four corners are inside, everything is inside.
	 *
	 * @param box the rectangle to test for inclusion
	 * @return	boolean value of <code>true</code> if the specified Rectangle
	 * is "inside" this Figure;
	 * 			<code>false</code> otherwise.
	 */
	public boolean contains(Rectangle box) {
		return super.contains(box) &&
			contains(box.x + box.width,box.y) && contains(box.x, box.y + box.height)
			&& contains(box.x, box.y) && contains(box.x + box.width, box.y + box.height);
	}
	/** 
	 * Answers whether the receiver intersects a Rectangular area.
	 * By default, just check if the bounds intersects.
	 * Subclasses may wish to do something more sophisticated.
	 *
	 * @param box the Rectangular area
	 * @return	boolean value of <code>true</code> if the receiver intersects the
	 * specified Rectangular area;
	 * 			<code>false</code> otherwise.
	 * @see #bounds
	 */
	public boolean intersects(Rectangle box) {
		// if bounding boxes don't intersect, figures don't intersect
		if (! super.intersects(box))
			return false;
			
		// if the ellipse contains one of the corners of the box, they intersect
		if (contains(box.x, box.y))
			return true;
		if (contains(box.x + box.width, box.y))
			return true;
		if (contains(box.x, box.y + box.height))
			return true;
		if (contains(box.x + box.width, box.y + box.height))
			return true;
			
		// if the box contains any of the four edges of the ellipse, they intersect
		Rectangle bigBox = new Rectangle(box.x, box.y, box.width + 1, box.height + 1);
		if (bigBox.contains(x + (width/2), y))
			return true;
		if (bigBox.contains(x, y + (height/2)))
			return true;
		if (bigBox.contains(x + (width/2), y + height))
			return true;
		if (bigBox.contains(x + width, y + (height/2)))
			return true;
			
		// otherwise they don't intersect
		return false;
	}
	/**
	 * Paint the receiver, filling all the contained area.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paintFilled(Graphics g)  {
		super.paintFilled(g);
		// fudge by 1 since filling primitives are different
		// than drawing primitive
		g.fillOval(x,y,width+1,height+1);
	}
	/**
	 * Paint the outline of the receiver.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paintStrokes(Graphics g)  {
		super.paintStrokes(g);
		g.drawOval(x,y,width,height);
	}
}

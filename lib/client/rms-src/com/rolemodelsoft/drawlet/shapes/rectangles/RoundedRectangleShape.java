package com.rolemodelsoft.drawlet.shapes.rectangles;

/**
 * @(#)RoundedRectangleShape.java
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
 * This provides an implementation of a RectangleShape whose corners are rounded.
 * NOTE: intersection algorithms are currently incomplete and only determine if
 * the Rectangular bounds intersect.
 */
 
public class RoundedRectangleShape extends AbstractRectangleShape {
	static final long serialVersionUID = 6428757790918919037L;

	/**
	 * The width of the corner arc.
	 */
	protected int arcWidth = defaultArcWidth();

	/**
	 * The height of the corner arc.
	 */
	protected int arcHeight = defaultArcHeight();
	/**
	 * Constructs a new, default instance of the receiver.
	 */
	public RoundedRectangleShape() {
	}
	/**
	 * Constructs a new instance of the receiver initialized with the
	 * given values for x, y, width and height.
	 * 
	 * @param x an integer representing the x coordinate.
	 * @param y an integer representing the y coordinate.
	 * @param width an integer representing the width.
	 * @param height an integer representing the height.
	 */
	public RoundedRectangleShape(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	/**
	 * Constructs a new instance of the receiver initialized with the
	 * given values for x, y, width, height, arcWidth, and arcHeight.
	 * 
	 * @param x an integer representing the x coordinate.
	 * @param y an integer representing the y coordinate.
	 * @param width an integer representing the width.
	 * @param height an integer representing the height.
	 * @param arcWidth an integer representing the width of the corners.
	 * @param arcHeight an integer representing the height of the corners.
	 */
	public RoundedRectangleShape(int x, int y, int width, int height, int arcWidth, int arcHeight) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.arcWidth = arcWidth;
		this.arcHeight = arcHeight;
	}
	/**
	 * Constructs a new instance of the receiver initialized with the
	 * given Rectangle.
	 * 
	 * @param rectangle a Rectangle representing the bounding box.
	 */
	public RoundedRectangleShape(Rectangle rectangle) {
		this(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
	}
	/**
	 * Constructs a new instance of the receiver initialized with the
	 * given Rectangle, arcWidth, and arcHeight.
	 * 
	 * @param rectangle a Rectangle representing the bounding box.
	 * @param arcWidth an integer representing the width of the corners.
	 * @param arcHeight an integer representing the height of the corners.
	 */
	public RoundedRectangleShape(Rectangle rectangle, int arcWidth, int arcHeight) {
		this(rectangle.x,rectangle.y,rectangle.width,rectangle.height, arcWidth, arcHeight);
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
		if (!super.contains(x,y))
			return false;
		/*
		 * if it's in one of the corners, verify it is not outside the rounded area (ellipse)
		 */
		int a = (arcWidth + 1) / 2; // get half the width of the arc, being overly conservative
		int b = (arcHeight + 1) / 2; // get half the height of the arc, being overly conservative
		return
			!(
			(cornerExcludes(new Rectangle( getLeft(), getTop(), a, b ), getLeft() + a, getTop() + b, x, y)) ||
			(cornerExcludes(new Rectangle( getLeft(), getBottom()-b, a, b ), getLeft() + a, getBottom() - b, x, y)) ||
			(cornerExcludes(new Rectangle( getRight()-a, getTop(), a, b ), getRight() - a, getTop() + b, x, y)) ||
			(cornerExcludes(new Rectangle( getRight()-a, getBottom()-b, a, b ), getRight() - a, getBottom() - b, x, y)));
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
			contains(box.x, box.y) && contains(box.x + box.width, box.y + box.height)
			&& contains(box.x + box.width, box.y) && contains(box.x, box.y + box.height);
	}
	/**  
	 * Checks whether a specified x,y location is "inside" the specified corner but outside the arc
	 * in that corner. <code>x</code> and <code>y</code> are defined to be relative to the 
	 * coordinate system of this figure.
	 *
	 * @param corner the corner in which the rounded edge we are interested in appears. 
	 * @param centerX the x coordinate of the center of the arc
	 * @param centerY the y coordinate of the center of the arc
	 * @param x the x coordinate of the location we are examining
	 * @param y the y coordinate of the location we are examining
	 * @return	boolean value of <code>true</code> if the specified x,y
	 * location is "inside" the corner but outside the arc;
	 * 			<code>false</code> otherwise.
	 */
	protected boolean cornerExcludes(Rectangle corner, int centerX, int centerY, int x, int y) {
		Rectangle bigCorner = new Rectangle (corner.x, corner.y, corner.width + 1, corner.height + 1);
		if ( bigCorner.contains( x, y ) ) {
			/*
			 * using standard geometric formula for ellipse
			 *   x^2   y^2
			 *   --- + --- = 1
			 *   a^2   b^2
			 * where a and b are x and y coordinates where y=0 and x=0 respectively...
			 * multiplying by a^2*b^2 and using > to determine points outside yields 
			 *   b^2*x^2 + a^2*y^2 <= a^2*b^2
			 */
			int a = corner.width;
			int b = corner.height;
			int aSquared = a * a;
			int bSquared = b * b;
			int normalizedX = x - centerX;
			int normalizedY = y - centerY;
			return (((normalizedX * normalizedX) * bSquared) + ((normalizedY * normalizedY) * aSquared)) > (aSquared * bSquared);
		}

		return false;
	}
	/**
	 * Answer the default/initial value for arc height.
	 * 
	 * @return	an integer representing the default/initial value for arc height.
	 */
	protected int defaultArcHeight() {
		return 20;
	}
	/**
	 * Answer the default/initial value for arc width.
	 * 
	 * @return	an integer representing the default/initial value for arc width.
	 */
	protected int defaultArcWidth() {
		return 20;
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
			
		// if the receiver contains one of the corners of the box, they intersect
		if (contains(box.x, box.y))
			return true;
		if (contains(box.x + box.width, box.y))
			return true;
		if (contains(box.x, box.y + box.height))
			return true;
		if (contains(box.x + box.width, box.y + box.height))
			return true;
			
		// if the box contains any of the sides of the receiver, they intersect
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
	 * Paint the shape, filling all contained area.
	 * 
	 * @param g the specified Graphics window.
	 */
	public void paintFilled(Graphics g)  {
		super.paintFilled(g);
		// fudge by 1 since filling primitives are different
		// than drawing primitive
		g.fillRoundRect(x,y,width+1,height+1,arcWidth,arcHeight);
	}
	/**
	 * Paint the outline of the shape.
	 * 
	 * @param g the specified Graphics window.
	 */
	public void paintStrokes(Graphics g)  {
		super.paintStrokes(g);
		g.drawRoundRect(x,y,width,height,arcWidth,arcHeight);
	}
}

package com.rolemodelsoft.drawlet.shapes.rectangles;

/**
 * @(#)RectangleShape.java
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
import com.rolemodelsoft.drawlet.shapes.*;
import java.awt.*;

/**
 * This provides a basic concrete implementation of BoundedFigures in the form
 * of "rectilinear" boxes that are assumed to be movable and reshapable with
 * observers that want to know when their locations or shapes change.
 * Although this is a concrete class, it is acknowledged that there are
 * probably other implementations (e.g. one that uses Locators) which are more flexible.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class RectangleShape extends AbstractRectangleShape {
	static final long serialVersionUID = 5365603316454725622L;
	/**
	 * Constructs a new, default instance of the receiver.
	 */
	public RectangleShape() {
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
	public RectangleShape(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	/**
	 * Constructs a new instance of the receiver initialized with the
	 * given Rectangle.
	 * 
	 * @param rectangle a Rectangle representing the bounding box.
	 */
	public RectangleShape(Rectangle rectangle) {
		this(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
	}
	/**
	 * Paint the shape, filling all contained area.
	 * 
	 * @param g the specified Graphics window.
	 */
	public void paintFilled(Graphics g)  {
		super.paintFilled(g);
		g.fillRect(x,y,width,height);
	}
	/**
	 * Paint the outline of the shape.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paintStrokes(Graphics g)  {
		super.paintStrokes(g);
		g.drawRect(x,y,width,height);
	}
}

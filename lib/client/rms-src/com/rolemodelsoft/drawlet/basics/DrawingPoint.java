package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)DrawingPoint.java
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
import java.util.Hashtable;
 
/**
 * This provides basic default functionality for MovableLocators.
 * Basically, it's just java.awt.Point with the proper interface to be
 * interchangeable with other Locators
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class DrawingPoint extends AbstractLocator implements MovableLocator {
	/**
	 * The serialization id.
	 */
	static final long serialVersionUID = 2517599265720696312L;

	/**
	 * The x coordinate of this DrawingPoint.
	 */
	protected int x;

	/**
	 * The y coordinate of this DrawingPoint.
	 */
	protected int y;
	/**
	 * Constructs and initializes a Point from the specified x and y 
	 * coordinates.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public DrawingPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/** 
	 * Moves the receiver to the x and y coordinates
	 * 
	 * @param x the new x coordinate
	 * @param y the new x coordinate
	 */
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/** 
	 * Moves the receiver in the x and y direction.
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 */
	public void translate(int x, int y) {
		this.x += x;
		this.y += y;
	}
	/** 
	 * Answer the x coordinate.
	 * 
	 * @return	an integer representing the x coordinate
	 */
	public int x()  {
	return x;
	}
	/** 
	 * Set the x coordinate.
	 * 
	 * @param x its new desired x coordinate.
	 */
	public void x(int x)  {
	this.x = x;
	}
	/** 
	 * Answer the y coordinate.
	 * 
	 * @return	an integer representing the y coordinate
	 */
	public int y()  {
	return y;
	}
	/** 
	 * Set the y coordinate.
	 * 
	 * @param y its new desired y coordinate.
	 */
	public void y(int y)  {
	this.y = y;
	}
}

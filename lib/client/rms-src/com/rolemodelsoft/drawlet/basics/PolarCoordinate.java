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
 
public class PolarCoordinate extends AbstractLocator {
	/**
	 * The serialization id.
	 */
	static final long serialVersionUID = 2517599265720696312L;

	/**
	 * The x coordinate of this DrawingPoint.
	 */
	protected int r;

	/**
	 * The y coordinate of this DrawingPoint.
	 */
	protected double theta;
	/**
	 * Constructs and initializes a PolarCoordinate from the specified r and theta 
	 * values.
	 * 
	 * @param r the radius.
	 * @param theta the theta
	 */
	public PolarCoordinate(int r, double theta) {
		this.r = r;
		this.theta = theta;
	}
	/** 
	 * Answer the radius of the Locator (as a PolarCoordinate).
	 * 
	 * @return	an integer representing the radius
	 */
	public int r() {
		return r;
	}
	/** 
	 * Answer the angle in radians of the Locator (as a PolarCoordinate).
	 * 
	 * @return double representing theta
	 */
	public double theta() {
		return theta;
	}
	/** 
	 * Answer the x coordinate.
	 * 
	 * @return	an integer representing the x coordinate
	 */
	public int x()  {
		return (int)( r * Math.cos( theta ) );
	}
	/** 
	 * Answer the y coordinate.
	 * 
	 * @return	an integer representing the y coordinate
	 */
	public int y()  {
		return (int)( r * Math.sin( theta ) );
	}
}

package com.rolemodelsoft.drawlet;

/**
 * @(#)MovableLocator.java
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
 
/**
 * This interface defines protocols for objects that provide
 * 2-D coordinates which can be changed/manipulated
 *
 * @version 	1.1.6, 12/28/98
 */
 
public interface MovableLocator extends Locator {

	/** 
	 * Moves the receiver to the x and y coordinates
	 * 
	 * @param x the new x coordinate
	 * @param y the new x coordinate
	 */
	public abstract void move(int x, int y);
	/** 
	 * Moves the receiver in the x and y direction.
	 * 
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 */
	public abstract void translate(int x, int y);
	/** 
	 * Set the x coordinate.
	 * 
	 * @param x its new desired x coordinate.
	 */
	public abstract void x(int x);
	/** 
	 * Set the y coordinate.
	 * 
	 * @param y its new desired y coordinate.
	 */
	public abstract void y(int y);
}

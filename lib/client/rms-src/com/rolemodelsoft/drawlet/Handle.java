package com.rolemodelsoft.drawlet;

/**
 * @(#)Handle.java
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
import java.awt.*;

/**
 * This interface defines protocols for Handles
 * which are event handlers that may visibly appear on a DrawingCanvas.
 *
 * @version 	1.1.6, 12/28/98
 */
public interface Handle extends InputEventHandler {

	/**  
	 * Checks whether a specified x,y location is "inside" this
	 * handle, where x and y are defined to be relative to the 
	 * coordinate system of this handle.  
	 *
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 * @return	boolean value of <code>true</code> if the specified
	 * x,y location is "inside" this handle;
	 * 			<code>false</code> otherwise.
	 */
	public abstract boolean contains(int x, int y);
	/** 
	 * Returns the current bounds of this handle.
	 *
	 * @return a Rectangle representing the current bounds of this handle
	 */
	public abstract Rectangle getBounds();
	/** 
	 * Answers whether the receiver intersects a Rectangular area.
	 *
	 * @param box the Rectangular area
	 * @return	boolean value of <code>true</code> if the receiver intersects the
	 * rectangular area;
	 * 			<code>false</code> otherwise.
	 */
	public abstract boolean intersects(Rectangle box);
	/** 
	 * Paints the handle.
	 *
	 * @param g the specified Graphics window
	 */
	public abstract void paint(Graphics g);
	/**
	 * Release control of the canvas and clean up if necessary.
	 * Since this is a public method,
	 * don't assume the receiver actually has control.
	 *
	 * @param canvas the canvas which the receiver is to release control
	 */
	public abstract void releaseControl(DrawingCanvas canvas);
	/**  
	 * Make the handle be the event handler for the canvas.
	 * Note, once it takes control, it is obligated to return 
	 * at a future point in time.  
	 *
	 * @param canvas the canvas which the receiver is to control
	 */
	public abstract void takeControl(DrawingCanvas canvas);
}

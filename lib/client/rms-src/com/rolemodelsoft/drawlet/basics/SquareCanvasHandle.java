package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)SquareCanvasHandle.java
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

/**
 * Since many handles will be simple small squares (probably appearing on a 
 * DrawingCanvas), this abstract class offers a simple base for handles which
 * appear this way.
 * All subclasses need to provide, at a minimum, are the coordinates for the center
 * of the square via:
 *	centerX()
 *	centerY()
 *
 * @version 	1.1.6, 12/28/98
 */
public abstract class SquareCanvasHandle extends CanvasHandle {
	/**  
	 * Answer the x coordinate at the center of the handle.  
	 * 
	 * @return	an integer representing the x coordinate in the center of the
	 * handle
	 */
	protected abstract int centerX();
	/**  
	 * Answer the y coordinate at the center of the handle.  
	 * 
	 * @return	an integer representing the y coordinate in the center of the
	 * handle
	 */
	protected abstract int centerY();
	/** 
	 * Returns the current bounds of this handle.
	 * 
	 * @return	a Rectangle representing the current bounds of this handle
	 */
	public Rectangle getBounds()  {
		return new Rectangle(centerX() - halfWidth, centerY() - halfWidth, halfWidth * 2, halfWidth * 2);
	}
	/** 
	 * Paints the handle.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g)  {
		g.fillRect(centerX() - halfWidth, centerY() - halfWidth, 2*halfWidth, 2*halfWidth);
	}
}

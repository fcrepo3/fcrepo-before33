package com.rolemodelsoft.drawlet.shapes;

/**
 * @(#)ShapeTool.java
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
import com.rolemodelsoft.drawlet.basics.*;
import java.awt.event.*;

/**
 * This provides basic functionality for a tool used to create concrete Figures.
 * Concrete subclasses must provide, at a minimum:
 * 	newShape(int,int)
 * But may also wish to consider mouseDragged(MouseEvent) or other events that
 * dynamically alter/build the Figure.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public abstract class ShapeTool extends ConstructionTool {
	/**
	 * The x coordinate where the mouse first went down.
	 */
	protected int anchorX;

	/**
	 * The y coordinate where the mouse first went down.
	 */
	protected int anchorY;
	/**
	 * Called if the mouse is pressed.  By default, create a new figure and add it.
	 * Keep track of where the mouse went down for future operations in
	 * addition to standard creation of new shape if we are not in the midst
	 * of constructing one.
	 * Consume the event since we handle it.
	 *
	 * @param e the event 
	 */
	public void mousePressed(MouseEvent e) {
		if ( e.getX() > canvas.getBounds().width || e.getY() > canvas.getBounds().height ) {
			e.consume();
			return;
		}
		if (figure == null) {
			anchorX = e.getX();
			anchorY = e.getY();
			super.mousePressed(e);
		} else
			e.consume();
	}
	/**
	 * Called if the mouse is released.
	 * By default, if the mouse went up the same place it went down, 
	 * remove the figure as it is probably bogus.
	 * In addition, call the superclass to get inherited behavior.
	 *
	 * @param e the event 
	 */
	public void mouseReleased(MouseEvent e) {
		if ((getX(e) == anchorX) && (getY(e) == anchorY) && (figure != null))
			canvas.removeFigure(figure);
		super.mouseReleased(e);
	}
}

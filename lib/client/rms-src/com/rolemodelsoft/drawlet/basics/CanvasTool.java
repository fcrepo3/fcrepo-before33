package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)CanvasTool.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1997 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
import java.awt.event.*;

/**
 * This abstract class offers a simple base for Tools attached to a DrawingCanvas
 * Subclasses would typically provide a constructor that takes a DrawingCanvas
 * as an argument.
 *
 * @version 	1.1.6, 12/28/98
 */
public abstract class CanvasTool extends AbstractInputEventHandler {

	/**
	 * The canvas upon which to "operate"
	 */
	protected DrawingCanvas canvas;
	/** 
	 * Returns the proper x value for the given event.
	 *
	 * @param evt the MouseEvent to get the corrected x for.
	 * @return	an integer representing the proper x coordinate.
	 */
	protected int getX( MouseEvent evt ) {
		return canvas.getLocator( evt.getX(), evt.getY() ).x();
	}
	/** 
	 * Returns the proper y value for the given event.
	 * 
	 * @param evt the MouseEvent to get the corrected y for.
	 * @return	an integer representing the proper y coordinate.
	 */
	protected int getY( MouseEvent evt ) {
		return canvas.getLocator( evt.getX(), evt.getY() ).y();
	}
	/**
	 * Called if the mouse is released.  Default is to tell the canvas that
	 * the receiver's work is through.  Consume the event since we handle it.
	 * 
	 * @param e the MouseEvent
	 */
	public void mouseReleased(MouseEvent e) {
		canvas.toolTaskCompleted(this);
		e.consume();
	}
}

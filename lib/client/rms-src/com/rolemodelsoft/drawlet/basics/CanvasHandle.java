package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)CanvasHandle.java
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
import java.awt.*;
import java.awt.event.*;

/**
 * This abstract class offers a simple base for handles to be used on a
 * canvas.  Subclasses need to provide, at a minimum, implementations for:
 *	<ul><li>bounds(),</li>
 *	<li>paint(Graphics)</li></ul>
 * Typically, these types of handles wait for some outside object to ask them
 * to takeControl() (e.g. mouse down on top of it), then they do something in
 * response to mouse events, and finally they releaseControl() (e.g. mouse up).
 *
 * @version 	1.1.6, 12/28/98
 */
 
public abstract class CanvasHandle extends AbstractInputEventHandler implements Handle {

	/**
	 * The general size of handles (assuming same size in x and y direction).
	 */
	protected static int HANDLE_SIZE = defaultHandleSize();

	/**
	 * Half the general width of handles.
	 */
	protected static int halfWidth = HANDLE_SIZE / 2;

	/**
	 * The canvas upon which to "operate"
	 */
	protected DrawingCanvas canvas;

	/**
	 * The tool that was in control before this one took over.
	 */
	protected InputEventHandler previousTool;

	/**  
	 * Checks whether a specified x,y location is "inside" this
	 * handle. x and y are defined to be relative to the 
	 * coordinate system of this handle.  
	 * 
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 * @return	boolean value of <code>true</code> if the specified x,y
	 * location is "inside" this handle;
	 * 			<code>false</code> otherwise.
	 */
	public boolean contains(int x, int y) {
		return getBounds().contains(x, y);
	}
	/** 
	 * Answers the default value of HANDLE_SIZE;
	 * 
	 * @return	an integer representing the default value of HANDLE_SIZE
	 */
	public static int defaultHandleSize()  {
	return 6;
	}
	/**
	 * The handle has completed its task... clean up as appropriate.
	 * Don't assume the canvas will cleanly ask us to releaseControl when we
	 * reset our tool.  This is the way we release control from within.
	 */
	protected void finished() {
	canvas.setTool(previousTool);
	releaseControl(canvas);
	}
	/** 
	 * Returns the current bounds of this handle.
	 * 
	 * @return	a Rectangle representing the current bounds
	 * of this handle
	 */
	public abstract Rectangle getBounds();
	/** 
	 * Answers the height of the handle;
	 * 
	 * @return	an integer representing the height of the handle
	 */
	protected int getHandleHeight()  {
		return HANDLE_SIZE;
	}
	/** 
	 * Answer the value of HANDLE_SIZE.  This is the default size of handles
	 * in the system (assume same x and y size).
	 * 
	 * @return	an integer representing the value of HANDLE_SIZE
	 */
	public static int getHandleSize()  {
		return HANDLE_SIZE;
	}
	/** 
	 * Answers the width of the handle;
	 * 
	 * @return	an integer representing the width of the handle
	 */
	protected int getHandleWidth()  {
		return HANDLE_SIZE;
	}
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
	 * Answers whether the receiver intersects a Rectangular area.
	 * 
	 * @param box the Rectangular area
	 * @return	boolean value of <code>true</code> if the receiver intersects
	 * the specified Rectangular area;
	 * 			<code>false</code> otherwise.
	 */
	public boolean intersects(Rectangle box) {
		return getBounds().intersects(box);
	}
	/**
	 * By default, we are finished with our task when the mouse goes up, so we
	 * clean up.
	 * Subclasses may wish to override.
	 *
	 * @param e the event
	 */
	public void mouseReleased(MouseEvent e) {
		finished();
		e.consume();
	}
	/** 
	 * Paints the handle.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g) {
		Rectangle myBounds = getBounds();
		g.fillRect(myBounds.x, myBounds.y, myBounds.width, myBounds.height);
	}
	/**
	 * Release control of the canvas and clean up if necessary.
	 * Since this is a public method,
	 * don't assume the receiver actually has control.
	 * 
	 * @param canvas the canvas which the receiver is to release control
	 */
	public void releaseControl(DrawingCanvas canvas) {
		if (canvas.getTool() == this)
			canvas.toolTaskCompleted(this);
	}
	/** 
	 * Set the value of HANDLE_SIZE.  This is the default size of handles
	 * in the system (assume same x and y size).
	 *
	 * @param size the new size of handles.
	 */
	public static void setHandleSize(int size)  {
		HANDLE_SIZE = size;
		halfWidth = size / 2;
	}
	/**  
	 * Make the handle be the event handler for the canvas.
	 * Note, once it takes control, it is obligated to return 
	 * at a future point in time.  
	 * 
	 * @param canvas the canvas to be the event handler for
	 * @see #locate
	 */
	public void takeControl(DrawingCanvas canvas) {
		previousTool = canvas.getTool();
		this.canvas = canvas;
		canvas.setTool(this);
	}
}

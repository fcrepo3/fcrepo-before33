package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)BoxSelectionHandle.java
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
import java.util.Vector;
import java.util.Enumeration;

/**
 * A handle one can use to select figures on a canvas within a box.
 * This would typically be used by some sort of SelectionTool, but there is
 * nothing in the code that assumes that to be the case.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class BoxSelectionHandle extends CanvasHandle {
	/**
	 * The Rectangular area to search for figures.
	 */
	protected Rectangle box;

	/**
	 * The x coordinate at which the box is anchored.
	 */
	protected int anchorX;

	/**
	 * The y coordinate at which the box is anchored.
	 */
	protected int anchorY;

	/**
	 * The figures that are surrounded.
	 */
	protected Vector surrounded = new Vector();

	/** 
	 * Constructs and initializes a new instance with a box derived from two points.
	 * NOTE: They do NOT have to be top-left and bottom-right
	 *
	 * @param startX the x coordinate of the anchor corner
	 * @param startY the y coordinate of the anchor corner
	 * @param endX the x coordinate of the floating corner
	 * @param endY the y coordinate of the floating corner
	 */
	public BoxSelectionHandle(int startX, int startY, int endX, int endY) {
	this.anchorX = startX;
	this.anchorY = startY;
	this.updateBox(endX,endY);
	}
	/**  
	 * Checks whether a specified x,y location is "inside" this
	 * handle, where x and y are defined to be relative to the 
	 * coordinate system of this handle.  
	 * 
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 * @return	boolean value of <code>true</code> if the specified
	 * x,y position is "inside this handle;
	 * 			<code>false</code> otherwise.
	 */
	public boolean contains(int x, int y)  {
	return box.contains(x,y);
	}
	/** 
	 * Returns the current bounds of this handle.
	 * 
	 * @return	a Rectangle representing the current bounds of this handle
	 */
	public Rectangle getBounds()  {
		return box;
	}
	/**
	 * Answer the Color to use when highlighting.
	 * 
	 * @return	the Color to use when highlighting
	 */
	protected Color getHighlightColor() {
		return canvas.getStyle().getHighlightColor();
	}
/**
 * Resize the selection area and make sure the ones inside are selected,
 * and those newly outside are not.
 *
 * @param evt the event
 */
public void mouseDragged(MouseEvent evt) {
	int x = getX(evt);
	int y = getY(evt);
	Rectangle bounds = new Rectangle(box.x, box.y, box.width, box.height);
	updateBox(x, y);
	if (bounds.contains(x, y)) {
		for (Enumeration e = surrounded.elements(); e.hasMoreElements();) {
			Figure figure = (Figure) e.nextElement();
			if (!figure.isWithin(box)) {
				surrounded.removeElement(figure);
				canvas.removeSelection(figure);
			}
		}
	} else {
		for (FigureEnumeration e = canvas.figures(); e.hasMoreElements();) {
			Figure figure = e.nextElement();
			if (!surrounded.contains(figure) && figure.isWithin(box)) {
				surrounded.addElement(figure);
				canvas.addSelection(figure);
			}
		}
	}
	bounds = bounds.union(box);
	canvas.repaint(bounds);
	evt.consume();
}
	/** 
	 * Paints the handle.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g)  {
		Color oldColor = g.getColor();
		g.setColor(getHighlightColor());
		g.drawRect(box.x, box.y, box.width, box.height);
		g.setColor(oldColor);
	}
	/**
	 * Release control of the canvas and clean up if necessary.
	 * Since this is a public method,
	 * don't assume the receiver actually has control.
	 * 
	 * @param canvas the canvas which the receiver is to release control
	 */
	public void releaseControl(DrawingCanvas canvas) {
	canvas.removeHandle(this);
	super.releaseControl(canvas);
	}
	/**  
	 * Make the handle be the event handler for the canvas.
	 * Note, once it takes control, it is obligated to return 
	 * at a future point in time.  
	 * 
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 * @see #locate
	 */
	public void takeControl(DrawingCanvas canvas) {
	super.takeControl(canvas);
	canvas.addHandle(this);
	}
	/**  
	 * Change the box of the receiver based on its anchor and a new x,y.  
	 * 
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 */
	protected void updateBox(int x, int y) {
	box = new Rectangle(Math.min(anchorX,x), Math.min(anchorY,y), Math.abs(anchorX-x), Math.abs(anchorY-y));
	}
}

package com.rolemodelsoft.drawlet.shapes;

/**
 * @(#)BoundsHandle.java
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
import java.awt.*;
import java.awt.event.*;

/**
 * Although there are plenty of ways to add Handles to figures, a common thing to
 * do is put them on the corners or sides of some rectangular area (e.g. the bounds).
 * This abstract class serves as a base for handles that do just that.
 * Subclasses need to implement, at a minimum:
 * 	Their own constructors, modeled after this one's
 *	defaultLocator(Figure);
 * and should probably define a mouseDrag(Event,int,int) method to invoke the
 * provided resize(int,int) or reshape(int,int,int,int) method if the handle
 * is there to allow reshaping of the underlying figure.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public abstract class BoundsHandle extends SquareCanvasHandle implements FigureHolder {

	/**
	 * The figure whose bounds the handle may modify.
	 */
	protected Figure figure;

	/**
	 * The locator defining where to place the handle.
	 */
	protected Locator locator;

	/** 
	 * Constructs and initializes a new instance of a handle which can 
	 * affect the figure's bounds in some way.  Subclasses should use a similar
	 * constructor, invoking super.
	 *
	 * @param figure the figure whose bounds we may wish to change.
	 */
	public BoundsHandle(Figure figure) {
		this.setFigure(figure);
	}
	/**  
	 * Answer the x coordinate at the center of the handle.  
	 * 
	 * @return	an integer representing the x coordinate at the center of the handle
	 */
	public int centerX() {
		return locator.x();
	}
	/**  
	 * Answer the y coordinate at the center of the handle.  
	 * 
	 * @return	an integer representing the y coordinate at the center of the handle
	 */
	public int centerY() {
		return locator.y();
	}
	/** 
	 * Answer the default/initial locator.
	 * 
	 * @param figure the figure
	 * @return	the default/initial Locator
	 */
	protected abstract Locator defaultLocator(Figure figure);
	/** 
	 * Returns the figure associated with this handle.
	 * 
	 * @return	the Figure associated with this handle
	 */
	public Figure getFigure()  {
		return figure;
	}
	/**
	 * Called if the mouse is dragged (the mouse button is down).
	 * Resize/reshape the figure as appropriate.  Consume the event.
	 * Subclasses should provide their own behavior to resize/reshape the figure.
	 *
	 * @param evt the event
	 * @see #resize
	 * @see #reshape
	 */
	public void mouseDragged(MouseEvent evt) {
		evt.consume();
	}
	/**
	 * Reshape the figure and cleanly repaint the canvas.
	 * 
	 * @param x the new x.
	 * @param y the new y.
	 * @param width the new width.
	 * @param height the new height.
	 */
	public void reshape(int x, int y, int width, int height)  {
		Rectangle bounds = figure.getBounds();
		figure.setBounds(x, y, width, height);
		bounds = bounds.union(figure.getBounds());
		bounds.grow(halfWidth, halfWidth);
		canvas.repaint(bounds);
	}
	/**
	 * Resize the figure and cleanly repaint the canvas.
	 * Note that we're assuming that at least some of the other handles must be repainted.
	 *
	 * @param width the new width.
	 * @param height the new height.
	 */
	public void resize(int width, int height)  {
		Rectangle bounds = figure.getBounds();
		figure.setSize(width, height);
		bounds = bounds.union(figure.getBounds());
		bounds.grow(halfWidth, halfWidth);
		canvas.repaint(bounds);
	}
	/** 
	 * Set the figure associated with this handle.  Reset the locator.
	 *
	 * @param figure the Figure to hold.
	 */
	public void setFigure(Figure figure)  {
		this.figure = figure;
		locator = defaultLocator(figure);
	}
}

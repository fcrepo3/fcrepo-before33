package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)LinePointHandle.java
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
import com.rolemodelsoft.drawlet.shapes.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class provides a handle that shows up at a particular Locator on a line
 * and reshapes the figure accordingly as it is dragged.
 *
 * @version 	1.1.6, 12/29/98
 */
public class LinePointHandle extends SquareCanvasHandle implements FigureHolder {

	/**
	 * The figure to which we are attached.
	 */
	protected LineFigure figure;

	/**
	 * The index of the point (vertex) on the line we manipulate.
	 */
	protected int index;

	/** 
	 * Constructs and initializes a new instance of a handle which can 
	 * affect the the position of a particular point defining a line.
	 *
	 * @param figure the figure whose point (vertex) we may wish to move.
	 * @param index the identifying index of the point of interest.
	 */
	public LinePointHandle(LineFigure figure, int index) {
		this.figure = figure;
		this.index = index;
	}
	/**  
	 * Answer the x coordinate at the center of the handle.  
	 * 
	 * @return	an integer representing the x coordinate at the center of the handle
	 */
	protected int centerX()  {
		return figure.getLocator(index).x();
	}
	/**  
	 * Answer the y coordinate at the center of the handle.  
	 * 
	 * @return	an integer representing the x coordinate at the center of the handle
	 */
	protected int centerY()  {
		return figure.getLocator(index).y();
	}
	/** 
	 * Answers the figure associated with this handle.
	 * 
	 * @return	the Figure associated with this handle
	 */
	public Figure getFigure()  {
		return figure;
	}
	/**
	 * Called if the mouse is dragged (the mouse button is down).
	 *
	 * @param evt the event
	 * @see #move
	 */
	public void mouseDragged(MouseEvent evt) {
		move(getX(evt), getY(evt));
		evt.consume();
	}
	/**
	 * Called if the mouse is released (the mouse button goes up).
	 *
	 * @param evt the event
	 */
	public void mouseReleased(MouseEvent evt) {
		if ( figure.isObsolete() )
			canvas.removeFigure( figure );
			canvas.removeHandles( figure );
		super.mouseReleased( evt );
	}
	/**
	 * Reshape the figure by moving the corresponding point
	 * and cleanly repaint the canvas.
	 *
	 * @param x the x coordinate for the new location of the point
	 * @param y the y coordinate for the new location of the point
	 */
	public void move(int x, int y)  {
		Rectangle bounds = figure.getBounds();
		figure.setLocator(index, new DrawingPoint(x,y));
		bounds = bounds.union(figure.getBounds());
		bounds.grow(halfWidth, halfWidth);
		canvas.repaint(bounds);
	}
	/** 
	 * Set the figure the receiver is holding.
	 * Note: in this case, it must be a LineFigure
	 *
	 * @param newFigure the new LineFigure to affect.
	 * @exception IllegalArgumentException If the figure is not a LineFigure.
	 */
	public void setFigure(Figure newFigure)  {
		if (!(newFigure instanceof LineFigure))
			throw new IllegalArgumentException("Figure must be a LineFigure"); 
		figure = (LineFigure)newFigure;
	}
}

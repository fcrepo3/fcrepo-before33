package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)ConnectedLineCreationHandle.java
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
import com.rolemodelsoft.drawlet.basics.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class provides a handle that creates connected lines from one figure
 * to another.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class ConnectedLineCreationHandle extends SquareCanvasHandle implements FigureHolder {

	/**
	 * The figure to which the handle is attached.
	 */
	protected Figure figure;

	/**
	 * The location at which the handle is displayed which will also
	 * serve as a prototype of the starting point of any line.
	 * This will typically be a FigureRelativePoint, but not necessarily.
	 */
	protected Locator locator;

	/**
	 * The line being constructed.
	 */
	protected ConnectingLine line;

	/**
	 * Construct and initialize a handle that will create connecting lines
	 * from a figure to some other figure.
	 *
	 * @param figure the figure to which the handle is attached.
	 */
	public ConnectedLineCreationHandle(Figure figure) {
		this.setFigure(figure);
	}
	/**
	 * Construct and initialize a handle that will create connecting lines
	 * from a figure to some other figure.
	 *
	 * @param figure the figure to which the handle is attached.
	 * @param locator the locator at which to locate the handle and beginning of any lines.
	 */
	public ConnectedLineCreationHandle(Figure figure, Locator locator) {
		this(figure);
		this.locator = locator;
	}
	/**  
	 * Answers a new ConnectingLine. This is a template method.  
	 *
	 * @param point the point at which the new line should start.
	 * @return	a new ConnectingLine.
	 */
	protected ConnectingLine basicNewLine( Locator point )  {
		return new ConnectingLine( (Locator) locator.duplicate(), point, true );
	}
	/**  
	 * Answer the x coordinate at the center of the handle.  
	 * 
	 * @return	an integer representing the x coordinate at the center
	 * of the handle
	 */
	protected int centerX()  {
		return locator.x();
	}
	/**  
	 * Answer the y coordinate at the center of the handle.  
	 * 
	 * @return	an integer representing the y coordinate at the center
	 * of the handle
	 */
	protected int centerY()  {
		return locator.y();
	}
	/** 
	 * Answer the default/initial locator.
	 *
	 * @param figure the Figure the Locator is to be relative to.
	 * @return	the default/initial Locator
	 */
	protected Locator defaultLocator(Figure figure) {
		return new FigureRelativePoint(figure,0.5,0.5);
	}
	/** 
	 * Returns the figure associated with this handle.
	 * 
	 * @return	the Figure assocatiated with this handle
	 */
	public Figure getFigure()  {
		return figure;
	}
	/**
	 * Called if the mouse is double-clicked.
	 * 
	 * @param evt the event 
	 */
	protected void mouseDoubleClicked(MouseEvent evt) {
		//just absorb it
		evt.consume();
	}
	/**
	 * Called if the mouse is dragged (the mouse button is down).
	 * If in the midst of constructing a line, move its latest point.
	 *
	 * @param evt the event
	 * @see #movePoint
	 */
	public void mouseDragged(MouseEvent evt) {
		if (line == null)
			return;
		movePoint(line.getNumberOfPoints() - 1, getX(evt), getY(evt));
		evt.consume();
	}
	/**
	 * Called if the mouse is moved (the mouse button is up).
	 * If in the midst of constructing a line, move its latest point.
	 *
	 * @param evt the event
	 * @see #movePoint
	 */
	public void mouseMoved(MouseEvent evt) {
		if (line == null)
			return;
		movePoint(line.getNumberOfPoints() - 1, getX(evt), getY(evt));
		evt.consume();
	}
	/**
	 * Called when the mouse goes down.
	 * If we're not already in the midst of constructing a line, start one.
	 *
	 * @param evt the event 
	 */
	public void mousePressed(MouseEvent evt) {
		if (line == null) {
			line = basicNewLine( new DrawingPoint(getX(evt), getY(evt)));
			canvas.addFigure(line);
		}
		evt.consume();
	}
	/**
	 * Called if the mouse goes up.
	 * Connect the latest point to any underlying figure if possible.
	 * If the shift key is down, add a point and keep constructing.
	 * If not, we're done.
	 *
	 * @param evt the event
	 */
	public void mouseReleased(MouseEvent evt) {
		if (line == null)
			return;
		int x = getX(evt);
		int y = getY(evt);
		Figure target = canvas.otherFigureAt(line, x, y);
		if (!evt.isShiftDown() && (target == null || target == figure))
			canvas.removeFigure(line);
		else {
			Locator newLocator = target.requestConnection(figure, x, y);
			if (newLocator != null) {
				line.setLocator(line.getNumberOfPoints() - 1, newLocator);
				canvas.moveFigureBehind(line, figure);
				canvas.moveFigureBehind(line, target);
			}
		}
		if (!evt.isShiftDown())
			super.mouseReleased(evt);
		else
			line.addLocator(new DrawingPoint(x, y));
		evt.consume();
	}
	/**
	 * Move the identified point of the line.  
	 * Assumes that pointIndex is a valid point in the current line being edited.
	 * 
	 * @param pointIndex the index of the point to move.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	protected void movePoint(int pointIndex, int x, int y) {
		Rectangle bounds = line.getBounds();
		line.setLocator(pointIndex,new DrawingPoint(x,y));
		bounds = bounds.union(line.getBounds());
		canvas.repaint(bounds);
	}
	/** 
	 * Paints the handle.
	 * 
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g)  {
		g.fillRect(centerX() - halfWidth, centerY() - halfWidth, 2, 2*halfWidth);
		g.drawLine(centerX() - halfWidth, centerY(), centerX() + halfWidth, centerY());
		g.fillRect(centerX() + halfWidth - 1, centerY() - halfWidth, 2, 2*halfWidth);
	}
	/**
	 * Release control of the canvas and clean up if necessary.
	 * Since this is a public method,
	 * don't assume the receiver actually has control.
	 * Since we're obviously done constructing the line, see if it's valid
	 * If not, delete it.
	 * Prepare to create a new one next time we get control.
	 *
	 * @param canvas the canvas which the receiver is to release control of.
	 */
	public void releaseControl(DrawingCanvas canvas) {
	if (line != null) {
		if (line.isObsolete()) 
			canvas.removeFigure(line);
		line = null;
	}
	super.releaseControl(canvas);
	}
	/** 
	 * Set the figure associated with this handle.  Reset the locator.
	 *
	 * @param figure the Figure to associate with.
	 */
	public void setFigure(Figure figure)  {
		this.figure = figure;
		locator = defaultLocator(figure);
	}
}

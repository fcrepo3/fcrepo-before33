package com.rolemodelsoft.drawlet.shapes.lines;

/*
 * @(#)LineTool.java
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
import java.awt.event.*;

/**
 * This tool produces Lines.
 * Currently, one must hold the shift key down in order to add more than
 * a single segment.
 *
 * @version 	1.1.6, 12/29/98
 */
public class LineTool extends ShapeTool {
	/** 
	 * Constructs and initializes a new instance of a tool to create lines
	 * on a DrawingCanvas
	 *
	 * @param canvas the canvas on which to place lines.
	 */
	public LineTool(DrawingCanvas canvas) {
		this.canvas = canvas;
	}
   /**
	 * Create and answer a new Figure.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return	a newly created Figure
	 */
	protected Figure basicNewFigure(int x, int y)  {
		return new Line(x,y,x,y);
	}
	/**
	 * Called if the mouse is dragged (the mouse button is down and mouse is moving).  
	 * If we are constructing a line, move its last locator as indicated.
	 *
	 * @param e the event
	 */
	public void mouseDragged(MouseEvent e) {
		if (figure == null)
			return;
		movePoint(((LineFigure) figure).getNumberOfPoints() - 1, getX(e), getY(e) );
		e.consume();
	}
	/**
	 * Called if the mouse moves (when the mouse button is up).
	 * If we are constructing a line, move its last locator as indicated.
	 * When done, consume the event.
	 *
	 * @param e the event
	 */
	public void mouseMoved(MouseEvent e) {
		if (figure == null)
			return;
		movePoint(((LineFigure) figure).getNumberOfPoints() - 1, getX(e), getY(e));
		e.consume();
	}
	/**
	 * Called if the mouse is released.
	 * If we are constructing a line, and the shift key is down, add another
	 * point.  Otherwise, we're done constructing this one... get ready for the
	 * next one.
	 *
	 * @param e the event 
	 */
	public void mouseReleased(MouseEvent e) {
		if (figure == null)
			return;
		LineFigure myShape = (LineFigure) figure;
		Rectangle bounds = myShape.getBounds();

		Locator nextLast, last;
		LineFigure line = (LineFigure)figure;
		nextLast = line.getLocator( line.getNumberOfPoints() - 2 );
		last = line.getLocator( line.getNumberOfPoints() - 1 );
		if ( last.x() == nextLast.x() && last.y() == nextLast.y() ) {
			if ( line.getNumberOfPoints() == 2 )
				canvas.removeFigure( figure );
			else
				return;
		}
		
		if (!e.isShiftDown()) {
			figure = null;
			canvas.toolTaskCompleted(this);
		} else {
			myShape.addLocator(new DrawingPoint(getX(e), getY(e)));
			bounds = bounds.union(myShape.getBounds());
		}
		canvas.repaint(bounds);
		e.consume();
	}
	/**
	 * Move the identified point of the line.  
	 * Assumes that pointIndex is a valid point in the current line being edited.
	 * 
	 * @param pointIndex the index of the point to move.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 */
	protected void movePoint(int pointIndex, int x, int y) {
		LineFigure myShape = (LineFigure)figure;
		Rectangle bounds = myShape.getBounds();
		myShape.setLocator(pointIndex,new DrawingPoint(x,y));
		bounds = bounds.union(myShape.getBounds());
		canvas.repaint(bounds);
	}
}

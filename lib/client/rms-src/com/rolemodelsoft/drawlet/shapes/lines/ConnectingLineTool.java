package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)ConnectingLineTool.java
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
import com.rolemodelsoft.drawlet.shapes.*;
import java.awt.event.*;
import java.awt.*;

/**
 * This tool produces ConnectingLines.
 * @version 	1.1.6, 12/29/98
 */

public class ConnectingLineTool extends LineTool {

	/** 
	 * Constructs and initializes a new instance of a tool to create connecting 
	 * lines on a DrawingCanvas.
	 *
	 * @param canvas the canvas on which to place lines.
	 */
	public ConnectingLineTool(DrawingCanvas canvas) {
		super(canvas);
	}
	/**
	 * Create and answer a new Figure.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	protected Figure basicNewFigure(int x, int y)  {
		return new ConnectingLine(x,y,x,y);
	}
	/**
	 * Called if the mouse goes down.
	 * When creating the new line, see if the first point is on top of another
	 * figure, and connect it if possible.
	 *
	 * @param e the event 
	 */
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		// see if the initial point was on top of a figure
		LineFigure myShape = (LineFigure) figure;
		if (myShape.getNumberOfPoints() != 2)
			return;
		int x = e.getX();
		int y = e.getY();
		Figure target = canvas.otherFigureAt(figure, x, y);
		if (target != null) {
			Locator newLocator = target.requestConnection(myShape, x, y);
			if (newLocator != null) {
				myShape.setLocator(0, newLocator);
				canvas.moveFigureBehind(myShape, target);
				canvas.repaint(myShape.getBounds());
			}
		}
	}
	/**
	 * Called if the mouse is released.
	 * If we are constructing a line, finalize the
	 * latest point by seeing if it is one that could connect to an underlying
	 * figure and, if so connect it.  We're done constructing this 
	 * one unless the shift key is down in which case we add another point
	 * and keep going.
	 * Otherwise, get ready to construct the next one.
	 *
	 * @param e the event 
	 */
	public void mouseReleased(MouseEvent e) {
		if (figure == null)
			return;
		LineFigure myShape = (LineFigure) figure;
		Rectangle bounds = myShape.getBounds();
		int x = getX(e);
		int y = getY(e);
		Figure target = canvas.otherFigureAt(myShape, x, y);
		if (target != null) {
			Locator newLocator = target.requestConnection(myShape, x, y);
			if (newLocator != null) {
				myShape.setLocator(myShape.getNumberOfPoints() - 1, newLocator);
				canvas.moveFigureBehind(myShape, target);
				bounds = bounds.union(myShape.getBounds());
			}
		}

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
			myShape.addLocator(new DrawingPoint(x, y));
			bounds = bounds.union(myShape.getBounds());
		}
		canvas.repaint(bounds);
		e.consume();
	}
}

package com.rolemodelsoft.drawlet.shapes.polygons;

/**
 * @(#)PolygonPointHandle.java
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
import com.rolemodelsoft.drawlet.shapes.*;
import com.rolemodelsoft.drawlet.basics.SquareCanvasHandle;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * This class provides a handle that shows up at a particular Locator on a 
 * PolygonFigure and reshapes the figure accordingly as it is dragged.  
 * If the first and last point of the polygon are the same, it moves them both.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class PolygonPointHandle extends SquareCanvasHandle implements FigureHolder {

	/**
	 * The figure to which the handle is attached.
	 */
	protected PolygonFigure figure;

	/**
	 * The index of the point (vertex) we manipulate.
	 */
	protected int index = 0;

	/** 
	 * Constructs and initializes a new instance of a handle which can 
	 * affect the the position of a particular point defining a polygon.
	 *
	 * @param figure the figure whose point (vertex) we may wish to move.
	 * @param index the identifying index of the point of interest
	 */
	public PolygonPointHandle(PolygonFigure figure, int index) {
		this.figure = figure;
		this.index = index;
	}
	/**  
	 * Answer the x coordinate at the center of the handle.
	 *
	 * @return an integer representing the x coordinate at the center of the handle.
	 */
	public int centerX()  {
		return figure.getPolygon().xpoints[index];
	}
	/**  
	 * Answer the y coordinate at the center of the handle.  
	 *
	 * @return an integer representing the y coordinate at the center of the handle.
	 */
	public int centerY()  {
		return figure.getPolygon().ypoints[index];
	}
	/** 
	 * Returns the Figure associated with this handle.
	 *
	 * @return the Figure associated with this handle.
	 */
	public Figure getFigure()  {
		return figure;
	}
	/**
	 * Called if the mouse is dragged (moved with the mouse button down).
	 * Move the particular point of the polygon accordingly.
	 *
	 * @param evt the event
	 * @see #move
	 */
	public void mouseDragged(MouseEvent evt) {
		move(getX(evt), getY(evt));
		evt.consume();
	}
	/**
	 * Reshape the figure by moving the corresponding point
	 * and cleanly repaint the canvas.
	 *
	 * @param x an integer representing the x coordinate to move to.
	 * @param y an integer representing the y coordinate to move to.
	 */
	public void move(int x, int y) {
		Rectangle bounds = figure.getBounds();
		
		// There should be a cleaner way to copy a polygon and all its points
		Polygon polygon = figure.getPolygon();
		polygon = new Polygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
		/* 
		 *if closed polygon and moving the starting/ending point, 
		 * we need to move both 
		 */
		if (index == 0) {
			int lastIndex = polygon.npoints - 1;
			if ((polygon.xpoints[index] == polygon.xpoints[lastIndex]) && (polygon.ypoints[index] == polygon.ypoints[lastIndex])) {
				polygon.xpoints[polygon.npoints - 1] = x;
				polygon.ypoints[polygon.npoints - 1] = y;
			}
		}
		polygon.xpoints[index] = x;
		polygon.ypoints[index] = y;
		figure.setPolygon(polygon);
		bounds = bounds.union(figure.getBounds());
		bounds.grow(halfWidth, halfWidth);
		canvas.repaint(bounds);
	}
	/** 
	 * Set the figure the receiver is holding.
	 * Note: in this case, it must be a PolygonFigure.
	 *
	 * @param newFigure the new PolygonFigure to affect.
	 * @exception IllegalArgumentException If the figure is not a PolygonFigure.
	 */
	public void setFigure(Figure newFigure)  {
		if (!(newFigure instanceof PolygonFigure))
			throw new IllegalArgumentException("Figure must be a PolygonFigure"); 
		figure = (PolygonFigure)newFigure;
	}
}

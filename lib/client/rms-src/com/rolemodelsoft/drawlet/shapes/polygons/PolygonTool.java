package com.rolemodelsoft.drawlet.shapes.polygons;

/*
 * @(#)PolygonTool.java	1.0 96/12/18 Ken Auer
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
import java.awt.*;
import java.awt.event.*;

/**
 * This tool produces PolygonShapes with a given number of sides.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class PolygonTool extends ShapeTool {

	/**
	 * The number of sides of the polygon we are to create.
	 */
	int numberOfSides = defaultNumberOfSides();

	/** 
	 * Constructs and initializes a new instance of a tool to create polygons
	 * on a DrawingCanvas
	 *
	 * @param canvas the canvas on which to place PolygonFigures.
	 */
	public PolygonTool(DrawingCanvas canvas) {
		this.canvas = canvas;
	}
	/** 
	 * Constructs and initializes a new instance of a tool to create polygons
	 * on a DrawingCanvas with a given number of sides.
	 *
	 * @param canvas the canvas on which to place PolygonFigures.
	 * @param numberOfSides the number of sides of the polygons we create.
	 */
	public PolygonTool(DrawingCanvas canvas, int numberOfSides) {
		this(canvas);
		this.numberOfSides = numberOfSides;
	}
   /**
	 * Create and answer a new Figure.
	 * 
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @return	a newly created Figure.
	 */
	protected Figure basicNewFigure(int x, int y)  {
		PolygonFigure newShape = new PolygonShape();
		Polygon polygon = new Polygon();
		polygon.addPoint(x,y);
		newShape.setPolygon(polygon);
		return newShape;
	}
	/**
	 * Answer the default/initial number of sides.
	 * 
	 * @return	an integer representing the default/initial number of sides.
	 */
	protected int defaultNumberOfSides() {
		return 3;
	}
	/**
	 * Called if the mouse is dragged (moved with the mouse button down).
	 * If we are constructing a polygon, move its last point as indicated,
	 * and consume the event.
	 *
	 * @param e the event.
	 */
	public void mouseDragged(MouseEvent e) {
		if (figure == null)
			return;
		movePoint(Math.min(((PolygonFigure) figure).getPolygon().npoints, numberOfSides) - 1, getX(e), getY(e) );
		e.consume();
	}
	/**
	 * Called if the mouse moves (when the mouse button is up).
	 * If we are constructing a polygon, move its last point as indicated.
	 *
	 * @param e the event.
	 */
	public void mouseMoved(MouseEvent e) {
		if (figure == null)
			return;
		movePoint(Math.min(((PolygonFigure) figure).getPolygon().npoints, numberOfSides) - 1, getX( e ), getY( e ));
		e.consume();
	}
	/**
	 * Called if the mouse goes up.
	 * If we are constructing a polygon, and we don't have the right number of
	 * sides yet, add another point.  Otherwise, we're done constructing this 
	 * one... get ready for the next one.
	 *
	 * @param e the event.
	 */
	public void mouseReleased(MouseEvent e) {
		if (figure == null)
			return;
		PolygonFigure myShape = (PolygonFigure) figure;
		Rectangle bounds = myShape.getBounds();
		Polygon polygon = myShape.getPolygon();
		polygon = new Polygon(polygon.xpoints,polygon.ypoints,polygon.npoints);
		if (polygon.npoints > numberOfSides) {
			figure = null;
			canvas.toolTaskCompleted(this);
		} else {
			polygon.addPoint( getX(e), getY(e) );
			if (polygon.npoints == numberOfSides)
				polygon.addPoint(polygon.xpoints[0], polygon.ypoints[0]);
		}
		myShape.setPolygon(polygon);
		canvas.repaint(bounds.union(myShape.getBounds()));
		e.consume();
	}
	/**
	 * Move the identified point of the polygon.  
	 * Assumes that pointIndex is a valid point in the current polygon being edited.
	 * 
	 * @param pointIndex the index of the point to move.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	protected void movePoint(int pointIndex, int x, int y) {
		PolygonFigure myShape = (PolygonFigure) figure;
		Rectangle bounds = myShape.getBounds();
		Polygon polygon = myShape.getPolygon();
		polygon = new Polygon(polygon.xpoints, polygon.ypoints, polygon.npoints);
		polygon.xpoints[pointIndex] = x;
		polygon.ypoints[pointIndex] = y;
		myShape.setPolygon(polygon);
		bounds = bounds.union(myShape.getBounds());
		canvas.repaint(bounds);
	}
}

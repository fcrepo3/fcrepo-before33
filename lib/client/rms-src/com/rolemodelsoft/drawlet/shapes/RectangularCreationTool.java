package com.rolemodelsoft.drawlet.shapes;

/**
 * @(#)RectangularCreationTool.java
 *
 * Copyright (c) 1999-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 *
 * Permission to use, copy, demonstrate, or modify this software
 * and its documentation for NON-COMMERCIAL or NON-PRODUCTION USE ONLY and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies and all terms of license agreed to when downloading 
 * this software are strictly followed.
 *
 * RMS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. RMS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
 
import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.shapes.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This tool produces Shapes by creating an instance and then moving
 * their topLeft and bottomRight.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class RectangularCreationTool extends ShapeTool {

	/**
	 * The ShapeClass must be something that can be created with
	 * a default Constructor and resized by its bottom right.
	 * Consider subclasses of AbstractRectangleShape for example.
	 */
	protected Class shapeClass;
	/** 
	 * Constructs and initializes a new instance of a tool to create 
	 * Shapes of the specified shapeClass on a DrawingCanvas
	 *
	 * The shapeClass must be something that can be created with
	 * a default Constructor and resized by its bottom right.
	 * Consider subclasses of AbstractRectangleShape for example.
	 *
	 * @param canvas the canvas on which to place shapes.
	 * @param shapeClass the class of shape to create.
	 */
	public RectangularCreationTool(DrawingCanvas canvas, Class shapeClass) {
		this.canvas = canvas;
		this.shapeClass = shapeClass;
	}
   /**
	 * Create and answer a new Figure.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return	a newly created Figure
	 */
	protected Figure basicNewFigure(int x, int y)  {
		Figure newFigure;
		try {
			newFigure = (Figure)shapeClass.newInstance();
		} catch (Throwable e) {
			System.out.println(shapeClass + " could not be instantiated via a default constructor.");
			return null;
		}
		newFigure.move(x,y);
		return newFigure;
	}
	/**
	 * Called if the mouse is dragged (when the mouse button is down).
	 *
	 * @param e the event
	 */
	public void mouseDragged(MouseEvent e) {
		if (figure == null)
			return;
		Rectangle bounds = figure.getBounds();

		int figureX = Math.min(anchorX, getX(e));
		int figureY = Math.min(anchorY, getY(e));
		int figureWidth = Math.abs(anchorX - getX(e));
		int figureHeight = Math.abs(anchorY - getY(e));

		figure.setBounds( figureX, figureY, figureWidth, figureHeight );
		bounds = bounds.union(figure.getBounds());
		canvas.repaint(bounds);
		e.consume();
	}
}

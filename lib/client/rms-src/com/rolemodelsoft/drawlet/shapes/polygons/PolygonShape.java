package com.rolemodelsoft.drawlet.shapes.polygons;

/*
 * @(#)PolygonShape.java
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
 
import com.rolemodelsoft.drawlet.util.GraphicsGeometry;
import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.shapes.*;
import java.awt.*;
import java.util.Hashtable;

/**
 * This provides a basic concrete implementation of PolygonFigures that are 
 * assumed to be movable and reshapable with observers that want to know when 
 * their locations or shapes change.  Although this is a concrete class, it is
 * acknowledged that there are probably other implementations (e.g. one that 
 * uses Locators) which are more flexible.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class PolygonShape extends FilledShape implements PolygonFigure {
	static final long serialVersionUID = 8026851545884863918L;
	
	/**
	 * The underlying, defining polygon.
	 */
	protected Polygon polygon = defaultPolygon();

	/** 
	 * Constructs a new instance of the receiver.
	 */
	public PolygonShape() {
	}
	/** 
	 * Constructs and initializes a new instance of the receiver based on
	 * a defining polygon.
	 *
	 * @param polygon the polygon which defines the basic shape of the receiver.
	 */
	public PolygonShape(Polygon polygon) {
	this.polygon = polygon;
	}
	/** 
	 * Reshapes the Figure to the specified bounding box.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the figure
	 * @param height the height of the figure
	 */
	protected synchronized void basicReshape(int x, int y, int width, int height) {
		basicSetPolygon(reshapedPolygon(polygon, x, y, width, height));
	}
	/**  
	 * Set the Polygon associated with the Figure.
	 *
	 * @param polygon the Polygon.
	 */
	protected synchronized void basicSetPolygon(Polygon polygon) {
		this.polygon = polygon;
	}
	/** 
	 * Moves the Figure in the x and y direction.
	 *
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 */
	protected synchronized void basicTranslate(int x, int y) {
		polygon.translate(x, y);
	}
	/**  
	 * Checks whether a specified x,y location is "inside" this
	 * Figure, where x and y are defined to be relative to the 
	 * coordinate system of this figure.  
	 *
	 * @param x the x coordinate 
	 * @param y the y coordinate
	 */
	public boolean contains(int x, int y) {
		return polygon.contains(x, y);
	}
	/**
	 * Answer the default/initial value for polygon
	 */
	protected Polygon defaultPolygon() {
		return new Polygon();
	}
	/**
	 * Duplicates the receiver into a Hashtable.
	 *
	 * @param duplicates the Hashtable to place the duplicate in.
	 * @return the duplicate.
	 */
	public synchronized Object duplicateIn(Hashtable duplicates) {
		PolygonShape duplicate = (PolygonShape)super.duplicateIn(duplicates);
		duplicate.polygon = new Polygon(polygon.xpoints,polygon.ypoints,polygon.npoints);
		return duplicate;
	}
	/** 
	 * Returns the current bounds of the receiver.
	 *
	 * @return a Rectangle representing the current bounds of the receiver.
	 */
	public Rectangle getBounds()  {
		return new Rectangle(polygon.getBounds()); // make a copy, otherwise we'll mess with the polygon's bounds attribute
	}
	/** 
	 * Answer the handles associated with the receiver.
	 *
	 * @return an array of Handles.
	 */
	public Handle[] getHandles() {
		int numberOfHandles = polygon.npoints;
		int lastIndex = polygon.npoints - 1;
		/* 
		 * if the first and last points are the same, 
		 * we only need one handle 
		 */
		if ((polygon.xpoints[0] == polygon.xpoints[lastIndex]) &&
			(polygon.ypoints[0] == polygon.ypoints[lastIndex]))
			numberOfHandles--;

		Handle handles[] = new Handle[numberOfHandles];
		for (int i=0; i < numberOfHandles; i++)
			handles[i] = new PolygonPointHandle(this,i);
		return handles;
	}
	/**  
	 * Answer a Polygon associated with the receiver.
	 *
	 * @return a copy of the Polygon which defines the receiver.
	 */
	public Polygon getPolygon() {
		return new Polygon(polygon.xpoints,polygon.ypoints,polygon.npoints);
	}
	/** 
	 * Answers whether the receiver intersects a Rectangular area.
	 * By default, just check if the bounds intersects.
	 * Subclasses may wish to do something more sophisticated.
	 *
	 * @param box the Rectangular area
	 * @return	boolean value of <code>true</code> if the receiver intersects the
	 * specified Rectangular area;
	 * 			<code>false</code> otherwise.
	 * @see #bounds
	 */
public boolean intersects(int x1, int y1, int x2, int y2) {
	
	if(polygon.npoints < 2) return false;
	
	// check lines between consecutive points
	for(int i = 0; i < polygon.npoints-1; i++){
		if( GraphicsGeometry.segmentIntersectsSegment(x1, y1, x2, y2, polygon.xpoints[i], polygon.ypoints[i], polygon.xpoints[i+1], polygon.ypoints[i+1]) )
			return true;
	}
	// check connecting line from last point to first point
	if( GraphicsGeometry.segmentIntersectsSegment(x1, y1, x2, y2, polygon.xpoints[polygon.npoints-1], polygon.ypoints[polygon.npoints-1], polygon.xpoints[0], polygon.ypoints[0]) )
		return true;
		
	return false;
}
	/** 
	 * Answers whether the receiver intersects a Rectangular area.
	 *
	 * @param Box the Rectangular area
	 * @return	boolean value of <code>true</code> if the receiver intersects the
	 * specified Rectangular area;
	 * 			<code>false</code> otherwise.
	 * @see #bounds
	 */
public boolean intersects(Rectangle Box) {
	// because the width and height of the bounds is one less than what is drawn on screen,
	// to check bounds must expand by one
	Rectangle bigBox = new Rectangle(Box.x, Box.y, Box.width+1, Box.height+1);
	Rectangle bigBounds = new Rectangle(getBounds().x, getBounds().y, getBounds().width+1, getBounds().height+1);
	
	//if bounds don't intersect, box and poly don't intersect
	if (! bigBounds.intersects(bigBox))	
		return false;
	
	//check if rectangle completely inside polygon
	if( contains(Box.x, Box.y) )
		return true;

	//check if polygon completely inside rectangle
	if( bigBox.contains(polygon.xpoints[0], polygon.ypoints[0]) ) 
		return true;
	
	//check top side of rectangle
	if( intersects(Box.x, Box.y, Box.x + Box.width, Box.y) )
		return true;

	//check right side of rectangle
	if( intersects(Box.x + Box.width, Box.y, Box.x + Box.width, Box.y + Box.height) )
		return true;

	//check bottom side of rectangle
	if( intersects(Box.x + Box.width, Box.y + Box.height, Box.x, Box.y + Box.height) )
		return true;

	//check left side of rectangle
	if( intersects(Box.x, Box.y + Box.height, Box.x, Box.y) )
		return true;
		
	return false;
}
	/**
	 * Paint the shape, filling all contained area.
	 *
	 * @param g the specified Graphics window
	 */
	public void paintFilled(Graphics g)  {
		super.paintFilled(g);
		g.fillPolygon(polygon);
	}
	/**
	 * Paint the outline of the shape.
	 *
	 * @param g the specified Graphics window
	 */
	public void paintStrokes(Graphics g)  {
		super.paintStrokes(g);
		g.drawPolygon(polygon);
	}
	/**  
	 * Set the Polygon associated with the figure.
	 * Let observers know what changed.
	 * This is a TemplateMethod with hooks:
	 * 	resetBoundsCache();
	 * 	basicSetPolygon();
	 *  changedShape();
	 *
	 * @param polygon the Polygon
	 */
	public void setPolygon(Polygon polygon) {
		Rectangle oldShape = getBounds();
		resetBoundsCache();
		basicSetPolygon(polygon);
		changedShape(oldShape);
	}
}

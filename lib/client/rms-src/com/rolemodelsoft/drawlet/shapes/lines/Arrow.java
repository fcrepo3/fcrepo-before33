package com.rolemodelsoft.drawlet.shapes.lines;

/**
 * @(#)Arrow.java
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
import com.rolemodelsoft.drawlet.basics.*;
import com.rolemodelsoft.drawlet.shapes.*;
import java.beans.*;
import java.awt.*;

/**
 * Arrows are a basic adornment for AdornedLines.
 *
 * @version 	1.1.6, 03/22/99
 */

public class Arrow extends AbstractShape implements RelatedLocationListener {
	static final long serialVersionUID = 7418562820276513057L;

	/**
	 * The LineFigure we are attached to.
	 */
	protected LineFigure line;

	/**
	 * Defines which end of the line we are attached to.
	 */
	public static final int FORWARD = -1;

	/**
	 * Defines which end of the line we are attached to.
	 */
	public static final int REVERSE = 1;
	
	/**
	 * The direction of the arrow
	 */
	protected int direction = defaultDirection();
	
	/**
	 * The polygon defining the arrow head
	 */
	transient protected Polygon polygon;

	/**
	 * The ArrowStyle which defines how to draw this Arrow.
	 */
	protected ArrowStyle arrowStyle = defaultArrowStyle();

	protected PropertyChangeEvent lastEvent;
	/**
	 * Construct a new arrow initialized with the given LineFigure.
	 *
	 * @param line the LineFigure to attach to.
	 */
	public Arrow( LineFigure line ) {
		this.line = line;
		line.addRelatedLocationListener( this );
	}
	/**
	 * Construct a new arrow initialized with the given LineFigure and direction.
	 *
	 * @param line the LineFigure to attach to.
	 * @param the direction for the Arrow.
	 */
	public Arrow( LineFigure line, int direction ) {
		this.line = line;
		line.addRelatedLocationListener( this );
		this.direction = direction;
	}
/** 
 * Reshapes the Arrow to the specified bounding box. Does nothing by default.
 *
 * @param x the x coordinate
 * @param y the y coordinate
 * @param width the width of the figure
 * @param height the height of the figure
 * @see #getBounds
 */
protected synchronized void basicReshape(int x, int y, int width, int height) {
}
	/**
	 * Shifts the receiver by the specified values.
	 * Don't think this is legal if we are attached to a line.
	 *
	 * @param x the amount to shift horizontally.
	 * @param y the amount to shift vertically.
	 */
	protected void basicTranslate(int x, int y) {
		// don't think this is legal if we are attached to a line
		polygon.translate(x, y);
	}
	/**
	 * Answer the default shape for the arrow.
	 *
	 * @return a Polygon representing the default shape for the arrow.
	 */
	protected ArrowStyle defaultArrowStyle() {
		return new ArrowStyle();
	}
	/**
	 * Answer the default direction for the arrow.
	 *
	 * @return an integer representing the default direction for the arrow.
	 */
	protected int defaultDirection() {
		return FORWARD;
	}
	/**
	 * Answer the default shape for the arrow.
	 *
	 * @return a Polygon representing the default shape for the arrow.
	 */
	protected Polygon defaultPolygon() {
		Locator[] locs = getArrowStyle().getLocators();
		int xArray[] = new int[locs.length];
		int yArray[] = new int[locs.length];
		for ( int i = 0; i < locs.length; i++ ) {
			xArray[i] = getArrowLocator( locs[i] ).x();
			yArray[i] = getArrowLocator( locs[i] ).y();
		}
		return new Polygon(
			xArray,
			yArray,
			locs.length);
	}
	/**
	 * Answer the proper Locator for the given values.
	 *
	 * @param source the first Locator to calculate from.
	 * @param destination the second Locator to calculate from.
	 * @param offset the amount the Locator should be offset.
	 * @return a Locator calculated from the given values.
	 */
	protected Locator getArrowLocator( Locator loc ) {
		Locator dest = getDestinationLocator();
		Locator source = getSourceLocator();
		Locator relative = new DrawingPoint(dest.x()-source.x(), dest.y()-source.y());
		PolarCoordinate coord = new PolarCoordinate( loc.r(), loc.theta() + relative.theta() );
		return new DrawingPoint( dest.x() + coord.x(), dest.y() + coord.y() );
	}
	/**
	 * Answer the ArrowStyle that defines how to draw the arrow.
	 *
	 * @return the ArrowStyle defining how to draw the arrow.
	 */
	public ArrowStyle getArrowStyle() {
		return arrowStyle;
	}
	/**
	 * Answer the bounds of the receiver.
	 *
	 * @return a Rectangle representing the receiver's bounds.
	 */
	public Rectangle getBounds() {
		return getPolygon().getBounds();
	}
	/**
	 * Answers the index of the destination, depending on the direction of the receiver.
	 *
	 * @return an integer representing the index of the destination.
	 */
	protected int getDestinationIndex() {
		if (getDirection() == FORWARD)
			return line.getNumberOfPoints() - 1;
		else
			return 0;
	}
	/**
	 * Answers the Locator of the destination, depending on the direction of the receiver.
	 *
	 * @return the Locator of the destination.
	 */
	protected Locator getDestinationLocator() {
		return line.getLocator(getDestinationIndex());
	}
	/**
	 * Answers the direction of the receiver.
	 *
	 * @return the integer representing the direction (+1 or -1).
	 */
	protected int getDirection() {
		return direction;
	}
	/** 
	 * Answer the handles associated with the receiver.
	 * A better way to do this would be with a Strategy... maybe next release.
	 * 
	 * @return	an array containing the Handles associated with the receiver.
	 */
	public Handle[] getHandles() {
		return new Handle[0];
	}
	/**
	 * Answer the polygon that defines the arrow.
	 *
	 * @return the Polygon defining the arrow.
	 */
	protected Polygon getPolygon() {
		if (polygon == null)
			polygon = defaultPolygon();
		return polygon;
	}
	/**
	 * Answers the index of the source, depending on the direction of the receiver.
	 *
	 * @return an integer representing the index of the source.
	 */
	protected int getSourceIndex() {
		return getDestinationIndex() + getDirection();
	}
	/**
	 * Answers the Locator of the source, depending on the direction of the receiver.
	 *
	 * @return the Locator of the source.
	 */
	protected Locator getSourceLocator() {
		return line.getLocator(getSourceIndex());
	}
	/**
	 * Update because the location of my line has changed.
	 *
	 * @evt the event.
	 */
	public void locationChanged( PropertyChangeEvent evt ) {
		updateShape();
	}
	/**
	 * Paints the Arrow.
	 */
	public void paint(java.awt.Graphics g) {
		if ( arrowStyle.isOpaque() )
			g.fillPolygon(getPolygon());
		else
			g.drawPolygon(getPolygon());
	}
	/**
	 * Update because the relation of my line has changed.
	 *
	 * @evt the event.
	 */
	public void relationChanged( PropertyChangeEvent event ) {
		updateShape();
	}
	/**
	 * Flush caches with respect to determining location.  This is a hook method.
	 * Subclasses may wish to override.
	 */
	protected void resetLocationCache() {
		polygon = null;
	}
	/**
	 * Update because the shape of my line has changed.
	 *
	 * @evt the event.
	 */
	public void shapeChanged( PropertyChangeEvent evt ) {
		lastEvent = evt;
		updateShape();
		lastEvent = null;
	}
	/**
	 * Update because the size of my line has changed.
	 *
	 * @evt the event.
	 */
	public void sizeChanged( PropertyChangeEvent evt ) {
		updateShape();
	}
	/**
	 * The line has notified the receiver of a change.
	 * Assume our location has changed due to some movement/reshaping
	 * of the line.
	 */
	protected void updateShape() {
		Rectangle oldShape = getBounds();
		resetBoundsCache();
		changedShape(oldShape);
	}
}

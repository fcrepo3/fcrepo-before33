package com.rolemodelsoft.drawlet.shapes;

/**
 * @(#)AbstractShape.java
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
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;
import java.beans.PropertyChangeEvent;

/**
 * This provides basic default functionality for Figures that are assumed to be
 * movable and reshapable with observers that want to know when their locations
 * or shapes change.  It provides most of its functionality based on its bounds(),
 * and forces concrete subclasses to define, at a minimum:
 *	paint(Graphics);
 *	getBounds();
 *	basicTranslate(int,int);
 *	basicReshape(int,int,int,int);
 *
 * @version 	1.1.6, 12/29/98
 */
 
public abstract class AbstractShape extends AbstractFigure {

	/** 
	 * Reshapes the receiver to the specified bounding box.
	 * Subclasses should probably provide synchronized versions if they're 
	 * modifying attributes of the receiver.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the figure
	 * @param height the height of the figure
	 */
	protected abstract void basicReshape(int x, int y, int width, int height);
	/**
	 * Resizes the receiver to the specified width and height.
	 *
	 * @param width the new width.
	 * @param height the new height.
	 * @see #basicReshape
	 */
	protected void basicResize(int width, int height)  {
		basicReshape(getLeft(),getTop(),width,height);
	}
	/** 
	 * Answer the handles associated with the receiver.
	 * A better way to do this would be with a Strategy... maybe next release.
	 * 
	 * @return	an array containing the Handles associated with the receiver
	 */
	public Handle[] getHandles() {
		Handle handles[] = new Handle[8];
		handles[0] = new TopLeftHandle(this);
		handles[1] = new TopRightHandle(this);
		handles[2] = new BottomRightHandle(this);
		handles[3] = new BottomLeftHandle(this);
		handles[4] = new TopHandle(this);
		handles[5] = new RightHandle(this);
		handles[6] = new BottomHandle(this);
		handles[7] = new LeftHandle(this);
		return handles;
	}
	/**
	 * Flush caches with respect to determining bounds.  This is a hook method.
	 * Subclasses may wish to override.
	 */
	protected void resetBoundsCache() {
		resetLocationCache();
		resetSizeCache();
	}
	/**
	 * Flush caches with respect to determining size.  This is a hook method.
	 * Subclasses may wish to override.
	 */
	protected void resetSizeCache() {
	}
	/** 
	 * Answer a new version of the given polygon reshaped to the specified 
	 * bounding box.
	 * This is a useful utility.
	 *
	 * @param polygon the polygon
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the figure
	 * @param height the height of the figure
	 * @return	a Polygon which is a new version of the given polygon reshaped to
	 * the specified bounding box
	 */
	public static Polygon reshapedPolygon(Polygon polygon, int x, int y, int width, int height) {
		int npoints = polygon.npoints;
		int xpoints[] = new int[npoints], ypoints[] = new int[npoints];
		Rectangle polyBounds = polygon.getBounds();
		double xScale = (double)width / (double)polyBounds.width;
		double yScale = (double)height / (double)polyBounds.height;
		for (int i=0; i < npoints; i++) {
			xpoints[i] = x + (int)((polygon.xpoints[i] - polyBounds.x) * xScale);
			ypoints[i] = y + (int)((polygon.ypoints[i] - polyBounds.y) * yScale);
		}
		return new Polygon(xpoints, ypoints, npoints);
	}
	/** 
	 * Answer a new version of the given polygon reshaped to the specified 
	 * bounding box.
	 * This is a useful utility.
	 *
	 * @param polygon the polygon
	 * @param bounds its new bounding box
	 * @return	a Polygon which is a new version of the given polygon reshaped to
	 * the specified bounding box
	 */
	public static Polygon reshapedPolygon(Polygon polygon, Rectangle bounds) {
		return reshapedPolygon(polygon, bounds.x, bounds.y, bounds.width, bounds.height);
	}
	/** 
	 * Reshapes the receiver to the specified bounding box.
	 * Let observers know what changed.
	 * This is a TemplateMethod with hooks:
	 * 	resetBoundsCache(),
	 * 	basicReshape(),
	 *  changedShape()
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the figure
	 * @param height the height of the figure
	 */
	public void setBounds(int x, int y, int width, int height) {
		Rectangle oldBounds = getBounds();
		basicReshape(x,y,width,height);
		resetBoundsCache();
		changedShape(oldBounds);
	}
	/**
	 * Resizes the receiver to the specified width and height.
	 * Let observers know what changed.
	 * This is a TemplateMethod with hooks:
	 * 	resetSizeCache(),
	 * 	basicResize(),
	 *  changedSize()
	 *
	 * @param width the width of the figure
	 * @param height the height of the figure
	 */
	public void setSize(int width, int height)  {
		Dimension oldSize = getSize();
		basicResize(width,height);
		resetSizeCache();
		changedSize(oldSize);
	}
}

package com.rolemodelsoft.drawlet.shapes.polygons;

/**
 * @(#)PolygonTestVisualizer.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
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

import com.rolemodelsoft.drawlet.examples.awt.*;
import java.awt.*;

public class PolygonTestVisualizer extends ExitingFrame {
	protected Polygon polygon;
	protected Rectangle[] rectangles;
/**
 * PolygonTestVisualizer constructor comment.
 */
public PolygonTestVisualizer() {
	super();
}
/**
 * PolygonTestVisualizer constructor comment.
 * @param title java.lang.String
 */
public PolygonTestVisualizer(Polygon polygon, Rectangle[] rectangles) {
	super();
	this.polygon = polygon;
	this.rectangles = rectangles;
}
/**
 * PolygonTestVisualizer constructor comment.
 * @param title java.lang.String
 */
public PolygonTestVisualizer(String title) {
	super(title);
}
/**
 * 
 * @param g java.awt.Graphics
 */
public void paint(Graphics g) {
	g.setColor(Color.black);
	for (int i=0; i<rectangles.length; i++) {
		Rectangle rect = rectangles[i];
		g.drawRect(rect.x,rect.y,rect.width,rect.height);
	}
	g.setColor(Color.blue);
	g.drawPolygon(polygon);	
}
}

package com.rolemodelsoft.drawlet.examples.awt;

/**
 * @(#)FullApplet.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1996-1997 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
import com.rolemodelsoft.drawlet.awt.*;
import com.rolemodelsoft.drawlet.shapes.lines.ConnectingLineTool;
import com.rolemodelsoft.drawlet.shapes.lines.DrawLineTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RectangleTool;
import com.rolemodelsoft.drawlet.shapes.RectangularCreationTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RoundedRectangleShape;
import com.rolemodelsoft.drawlet.shapes.ellipses.EllipseTool;
import com.rolemodelsoft.drawlet.shapes.polygons.PolygonTool;
import com.rolemodelsoft.drawlet.shapes.polygons.AnySidedPolygonTool;
import com.rolemodelsoft.drawlet.text.*;
import java.net.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

/**
 * Although there are plenty of ways to use the drawlet framework, it may not
 * be apparent without some examples.  Here is a very simple one which provides
 * a basic DrawingCanvas as an Applet.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class FullApplet extends SimpleApplet {

	/**
	 * Applet info.
	 *
	 * @return a String containing information on this applet
	 */
	public String getAppletInfo() {
		return "Drawlets Example v1.1.6 (30 Mar 1999), by RoleModel Software.";
	}
	/**
	 * @return the ToolPalette.
	 */
	public ToolPalette getToolPalette() {
		if ( toolPalette == null ) {
			Image triangleImage = getImage("greygrey/triangle.gif");
			Image pentagonImage = getImage("greygrey/pentagon.gif");
			Image freeHandImage = getImage("greygrey/freehand.gif");
			
			// Create the tool palette
			toolPalette = super.getToolPalette();
			toolPalette.addTool(new PolygonTool(canvas,3), "Triangle", triangleImage);
			toolPalette.addTool(new PolygonTool(canvas,5), "Pentagon", pentagonImage);
			toolPalette.addTool(new DrawLineTool(canvas), "Smooth Line", freeHandImage);
		}
		return toolPalette;
	}
	/**
	 * Initializes the applet.
	 * You never need to call this directly, it is called automatically
	 * by the system once the applet is created.
	 * @see #start
	 * @see #stop
	 * @see #destroy
	 */
	public void init() {
		super.init();

		GridBagConstraints stylePaletteConstraints = new GridBagConstraints();
		stylePaletteConstraints.gridx = 3;
		stylePaletteConstraints.gridy = 2;
		stylePaletteConstraints.anchor = GridBagConstraints.NORTH;
		add(getStylePalette(), stylePaletteConstraints);

	}
}

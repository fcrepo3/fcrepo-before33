package com.rolemodelsoft.drawlet.examples.awt;

/**
 * @(#)SimplestApplet.java
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
import com.rolemodelsoft.drawlet.shapes.lines.AdornedLineTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RectangleTool;
import com.rolemodelsoft.drawlet.shapes.RectangularCreationTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RoundedRectangleShape;
import com.rolemodelsoft.drawlet.shapes.ellipses.EllipseTool;
import com.rolemodelsoft.drawlet.shapes.polygons.PolygonTool;
import com.rolemodelsoft.drawlet.shapes.polygons.AnySidedPolygonTool;
import com.rolemodelsoft.drawlet.text.LabelTool;
import java.awt.*;

/**
 * Although there are plenty of ways to use the drawlet framework, it may not
 * be apparent without some examples.  Here is a very simple one which provides
 * a basic DrawingTool as an Applet.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class SimplestApplet extends java.applet.Applet {

	/**
	 * The canvas to use.
	 */
	protected DrawingCanvas canvas;

	/**
	 * The tool palette for this applet.
	 */
	protected ToolPalette toolPalette;
	/**
	 * Applet info.
	 */
	public String getAppletInfo() {
			return "Simple Drawlets Example v1.1.6 (30 Dec 1998), by RoleModel Software.";
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
		setLayout(new BorderLayout());

		// Create the canvas
		canvas = new SimpleDrawingCanvas();
		Component canvasComponent = new BufferedDrawingCanvasComponent(canvas);
		canvasComponent.setSize(200,200);
		add("Center", canvasComponent);

		// Set the drawing style
		DrawingStyle style = new SimpleDrawingStyle();
		style.setBackgroundColor(Color.blue);
		style.setForegroundColor(Color.white);
		canvas.setStyle(style);

		// Create the tool palette
		toolPalette = new ToolPalette(canvas);
		toolPalette.setLayout(new GridLayout(8,1,2,2));
		toolPalette.addTool(new SelectionTool(canvas), "Select");
		toolPalette.addTool(new LabelTool(canvas), "Label");
		toolPalette.addTool(new ConnectingLineTool(canvas), "Line");
		toolPalette.addTool(new AdornedLineTool(canvas), "Line w/ Arrow");
		toolPalette.addTool(new RectangleTool(canvas), "Box");
		toolPalette.addTool(new RectangularCreationTool(canvas,RoundedRectangleShape.class), "Rounded");
		toolPalette.addTool(new EllipseTool(canvas), "Ellipse");
		toolPalette.addTool(new AnySidedPolygonTool(canvas), "N-Sided");
		add("West",toolPalette);
	}
}

package com.rolemodelsoft.drawlet.examples.awt;

/**
 * @(#)SimplePanel.java
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
import java.awt.event.*;

/**
 * Although there are plenty of ways to use the drawlet framework, it may not
 * be apparent without some examples.  Here is a very simple one which provides
 * a basic DrawingTool as an Applet.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class SimplestPanel extends Panel {
	
	/**
	 * The canvas this applet will use
	 */
	protected DrawingCanvas canvas;
	/**
	 * Default constructor.
	 */
	public SimplestPanel() {
		super();
		initialize();
	}
	/**
	 * @param layout the layout manager to use with this application.
	 */
	public SimplestPanel(java.awt.LayoutManager layout) {
		super(layout);
	}
	/**
	 * Initialize class
	 */
	protected void initialize() {
		setName("Drawlets");
		setSize(320, 180);
		//setFont(new java.awt.Font("timesroman", 0, 12));
		setLayout(new BorderLayout());
		
		// Create the canvas
		canvas = new SimpleDrawingCanvas();
		Component canvasComponent = new BufferedDrawingCanvasComponent(canvas);
		canvasComponent.setSize(300,240);
		add("Center", canvasComponent);

		// Set the drawing style
		DrawingStyle style = new SimpleDrawingStyle();
		style.setBackgroundColor(Color.blue);
		style.setForegroundColor(Color.white);
		canvas.setStyle(style);

		// Create the tool palette
		ToolPalette toolPalette = new ToolPalette(canvas);
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
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args the arguments passed to the application on entry.
	 */
	public static void main(java.lang.String[] args) {
		try {
			java.awt.Frame frame = new ExitingFrame( "Simplest Drawlet Panel Example" );
			SimplestPanel aTestCanvas;
			aTestCanvas = new SimplestPanel();
			frame.add("Center", aTestCanvas);
			frame.setSize(aTestCanvas.getSize());
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of TestCanvas");
			exception.printStackTrace(System.out);
		}
	}
	/**
	 * Set the drawing associated with the receiver to the specified drawing
	 * 
	 * @param newDrawing the drawing
	 */
	public void setDrawing(Drawing newDrawing) {
		((SimpleDrawingCanvas)canvas).setDrawing(newDrawing);
	}
}

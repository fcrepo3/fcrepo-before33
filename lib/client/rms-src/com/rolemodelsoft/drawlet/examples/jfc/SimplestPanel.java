package com.rolemodelsoft.drawlet.examples.jfc;

/**
 * @(#)SimplestPanel.java
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
import com.rolemodelsoft.drawlet.jfc.*;
import com.rolemodelsoft.drawlet.shapes.lines.ConnectingLineTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RectangleTool;
import com.rolemodelsoft.drawlet.shapes.RectangularCreationTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RoundedRectangleShape;
import com.rolemodelsoft.drawlet.shapes.ellipses.EllipseTool;
import com.rolemodelsoft.drawlet.shapes.polygons.PolygonTool;
import com.rolemodelsoft.drawlet.shapes.polygons.AnySidedPolygonTool;
import com.rolemodelsoft.drawlet.text.LabelTool;
import javax.swing.*;
import java.awt.*;

/**
 * Although there are plenty of ways to use the drawlet framework, it may not
 * be apparent without some examples.  Here is a very simple one which provides
 * a basic DrawingTool.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class SimplestPanel extends JPanel {
	/**
	 * Default constructor.
	 */
	public SimplestPanel() {
		super();
		initialize();
	}
	/**
	 * @param layout the layout this application should use.
	 */
	public SimplestPanel(java.awt.LayoutManager layout) {
		super(layout);
	}
	/**
	 * @param layout the layout this application should use.
	 * @param isDoubleBuffered determines whether the application will be
	 * double buffered or not.
	 */
	public SimplestPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}
	/**
	 * @param isDoubleBuffered  determines whether the application will be
	 * double buffered or not.
	 */
	public SimplestPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}
	/**
	 * Initialize class
	 */
	private void initialize() {
		setName("Drawlets");
		setSize(426, 240);
		//setFont(new java.awt.Font("timesroman", 0, 12));
		setLayout(new BorderLayout());
		
		// Create the canvas
		JDrawingCanvasComponent canvasComponent = new JDrawingCanvasComponent();
		DrawingCanvas canvas = canvasComponent.getCanvas();
	//	canvasComponent.setSize(300,240);
		add(canvasComponent);

		// Set the drawing style
		DrawingStyle style = new SimpleDrawingStyle();
		style.setBackgroundColor(Color.blue);
		style.setForegroundColor(Color.white);
		canvas.setStyle(style);

		// Create the tool palette
		JToolBar toolPalette = new JToolBar();
		toolPalette.add(new CanvasToolAction("Select", canvas, (InputEventHandler)new SelectionTool(canvas)));
		toolPalette.addSeparator();
		toolPalette.add(new CanvasToolAction("Label", canvas, (InputEventHandler)new LabelTool(canvas)));
		toolPalette.add(new CanvasToolAction("Line", canvas, (InputEventHandler)new ConnectingLineTool(canvas)));
		toolPalette.add(new CanvasToolAction("Line w/Arrow", canvas, (InputEventHandler)new com.rolemodelsoft.drawlet.shapes.lines.AdornedLineTool(canvas)));
		toolPalette.add(new CanvasToolAction("Box", canvas, (InputEventHandler)new RectangleTool(canvas)));
		toolPalette.add(new CanvasToolAction("Rounded", canvas, (InputEventHandler)new RectangularCreationTool(canvas,RoundedRectangleShape.class)));
		toolPalette.add(new CanvasToolAction("Ellipse", canvas, (InputEventHandler)new EllipseTool(canvas)));
		toolPalette.add(new CanvasToolAction("N-Sided", canvas, (InputEventHandler)new AnySidedPolygonTool(canvas)));
		add(toolPalette,BorderLayout.NORTH);
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args the arguments passed to the application on entry
	 */
	public static void main(java.lang.String[] args) {
		try {
			JFrame frame = new ExitingFrame( "Drawlets" );
			SimplestPanel aTestCanvas;
			aTestCanvas = new SimplestPanel();
			frame.getContentPane().add("Center", aTestCanvas);
			frame.setSize(aTestCanvas.getSize());
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of TestCanvas");
			exception.printStackTrace(System.out);
		}
	}
}

package com.rolemodelsoft.drawlet.examples.jfc;

/**
 * @(#)SimplePanelExample.java
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
import com.rolemodelsoft.drawlet.shapes.lines.DrawLineTool;
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
 
public class SimplePanel extends JPanel {
	protected JToolBar toolPalette, toolBar;
	protected DrawingCanvas canvas;
	/**
	 * Default constructor.
	 */
	public SimplePanel() {
		super();
		initialize();
	}
	/**
	 * @param layout the layout this application should use.
	 */
	public SimplePanel(java.awt.LayoutManager layout) {
		super(layout);
		initialize();
	}
	/**
	 * @param layout the layout this application should use.
	 * @param isDoubleBuffered determines whether the application will be
	 * double buffered or not.
	 */
	public SimplePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		initialize();
	}
	/**
	 * @param isDoubleBuffered  determines whether the application will be
	 * double buffered or not.
	 */
	public SimplePanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		initialize();
	}
	/**
	 * @return Component the component holding the canvas.
	 */
	protected JComponent getCanvasComponent() {
		JDrawingCanvasComponent component = new JDrawingCanvasComponent();
		canvas = component.getCanvas();
		return component;
	}
	/**
	 * @return the ToolPalette.
	 */
	public JToolBar getToolBar() {
		if ( toolBar == null ) {
			toolBar = new JToolBar();
			toolBar.setFloatable( false );
			toolBar.add(new CanvasAction("", new ImageIcon("greygrey/copy.gif", "Copy"), canvas, "copySelections"));
			toolBar.add(new CanvasAction("", new ImageIcon("greygrey/cut.gif", "Cut"), canvas, "cutSelections"));
			toolBar.add(new CanvasAction("", new ImageIcon("greygrey/paste.gif", "Paste"), canvas, "paste"));
			toolBar.add(new CanvasAction("", new ImageIcon("greygrey/systempaste.gif", "Paste From System"), canvas, "pasteFromSystem"));
		}
		return toolBar;
	}
	/**
	 * @return the ToolPalette.
	 */
	public JToolBar getToolPalette() {
		if ( toolPalette == null ) {
			toolPalette = new JToolBar();
			toolPalette.setLayout( new BoxLayout( toolPalette, BoxLayout.Y_AXIS ) );
			toolPalette.setFloatable( false );
			toolPalette.add(new CanvasToolAction("", new ImageIcon("greygrey/select.gif", "Select"), canvas, (InputEventHandler)new SelectionTool(canvas)));
			toolPalette.addSeparator();
			toolPalette.add(new CanvasToolAction("", new ImageIcon("greygrey/label.gif", "Label"), canvas, (InputEventHandler)new LabelTool(canvas)));
			toolPalette.add(new CanvasToolAction("", new ImageIcon("greygrey/line.gif", "Line"), canvas, (InputEventHandler)new ConnectingLineTool(canvas)));
			toolPalette.add(new CanvasToolAction("", new ImageIcon("greygrey/box.gif", "Box"), canvas, (InputEventHandler)new RectangleTool(canvas)));
			toolPalette.add(new CanvasToolAction("", new ImageIcon("greygrey/rrect.gif", "Rounded"), canvas, (InputEventHandler)new RectangularCreationTool(canvas,RoundedRectangleShape.class)));
			toolPalette.add(new CanvasToolAction("", new ImageIcon("greygrey/ellipse.gif", "Ellipse"), canvas, (InputEventHandler)new EllipseTool(canvas)));
			toolPalette.add(new CanvasToolAction("", new ImageIcon("greygrey/triangle.gif", "Triangle"), canvas, (InputEventHandler)new PolygonTool(canvas,3)));
			toolPalette.add(new CanvasToolAction("", new ImageIcon("greygrey/pentagon.gif", "Pentagon"), canvas, (InputEventHandler)new PolygonTool(canvas,5)));
			toolPalette.add(new CanvasToolAction("", new ImageIcon("greygrey/nsided.gif", "N-Sided"), canvas, (InputEventHandler)new AnySidedPolygonTool(canvas)));
			toolPalette.add(new CanvasToolAction("", new ImageIcon("greygrey/freehand.gif", "Smooth Line"), canvas, (InputEventHandler)new DrawLineTool(canvas)));
		}
		return toolPalette;
	}
	/**
	 * Initialize class
	 */
	protected void initialize() {
		setName("Drawlets");
		setSize(426, 240);
		//setFont(new java.awt.Font("timesroman", 0, 12));
		setLayout(new BorderLayout());
		
		// Create the canvas
		add(getCanvasComponent());

		// Set the drawing style
		DrawingStyle style = new SimpleDrawingStyle();
		style.setBackgroundColor(Color.blue);
		style.setForegroundColor(Color.white);
		canvas.setStyle(style);

		// Create the tool palette
		add(getToolPalette(),BorderLayout.WEST);
		add(getToolBar(),BorderLayout.NORTH);
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args the arguments passed to the application on entry
	 */
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new ExitingFrame( "Drawlets" );
			SimplePanel aTestCanvas;
			aTestCanvas = new SimplePanel();
			frame.getContentPane().add("Center", aTestCanvas);
			frame.setSize(aTestCanvas.getSize());
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of TestCanvas");
			exception.printStackTrace(System.out);
		}
	}
}

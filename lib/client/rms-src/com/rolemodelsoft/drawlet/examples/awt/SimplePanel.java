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
import com.rolemodelsoft.drawlet.shapes.rectangles.RectangleTool;
import com.rolemodelsoft.drawlet.shapes.RectangularCreationTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RoundedRectangleShape;
import com.rolemodelsoft.drawlet.shapes.ellipses.EllipseTool;
import com.rolemodelsoft.drawlet.shapes.polygons.AnySidedPolygonTool;
import com.rolemodelsoft.drawlet.text.LabelTool;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/**
 * Although there are plenty of ways to use the drawlet framework, it may not
 * be apparent without some examples.  Here is a very simple one which provides
 * a basic DrawingTool as an Applet.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class SimplePanel extends Panel {
	
	/**
	 * The canvas this panel will use
	 */
	protected DrawingCanvas canvas;

	/**
	 * The tool palette for this panel.
	 */
	protected ToolPalette toolPalette;

	/**
	 * The style palette for this panel.
	 */
	protected StylePalette stylePalette;
	
	/**
	 * The tool bar for actions.
	 */
	protected ToolBar toolBar;
	/**
	 * Default constructor.
	 */
	public SimplePanel() {
		super();
		initialize();
	}
	/**
	 * @param layout the layout manager to use with this application.
	 */
	public SimplePanel(java.awt.LayoutManager layout) {
		super(layout);
	}
	/**
	 * @return Component the component holding the canvas.
	 */
	protected Component getCanvasComponent() {
		canvas = new SimpleDrawingCanvas();
		DrawingCanvasComponent component = new BufferedDrawingCanvasComponent( canvas );
		return component;
	}
	/**
	 * Get an image.
	 *
	 * @param name the name of the Image to get.
	 * @return the ToolPalette.
	 */
	public Image getImage( String name ) {
		Image image = null;
		try {
			InputStream in = getClass().getResourceAsStream( "/" + name );
			if ( in == null ) {
				System.err.println( "Image not found" );
				return image;
			}

			byte[] buffer = new byte[ in.available() ];
			in.read( buffer );
			image = Toolkit.getDefaultToolkit().createImage( buffer );
		} catch ( IOException e ) {
			System.err.println( "Unable to read image" );
			e.printStackTrace();
		}
		return image;
	}
	
	/**
	 */
	public StylePalette getStylePalette() {
		if ( stylePalette == null ) {
			// Create the color palette
			stylePalette = new StylePalette(canvas);
			stylePalette.setLayout(new GridBagLayout());
			stylePalette.setBackground( Color.white );
			stylePalette.addColor(Color.blue, "blue");
			stylePalette.addColor(Color.green, "green");
			stylePalette.addColor(Color.cyan, "cyan");
			stylePalette.addColor(Color.magenta, "magenta");
			stylePalette.addColor(Color.red, "red");
			stylePalette.addColor(Color.pink, "pink");
			stylePalette.addColor(Color.orange, "orange");
			stylePalette.addColor(Color.yellow, "yellow");
			stylePalette.addColor(Color.black, "black");
			stylePalette.addColor(Color.darkGray, "dark gray");
			stylePalette.addColor(Color.gray, "gray");
			stylePalette.addColor(Color.lightGray, "light gray");
			stylePalette.addColor(Color.white, "white");
			stylePalette.addColor(null, "NO");
			stylePalette.addSetter("setFillColor", "Fill");
			stylePalette.addSetter("setLineColor", "Line");
			stylePalette.addSetter("setTextColor", "Text");
			stylePalette.addSetter("setBackgroundColor", "Back");
			stylePalette.addSetter("setForegroundColor", "Fore");
			stylePalette.addApply("Apply");
			stylePalette.addStyleViewer();
		}
		return stylePalette;
	}
	/**
	 * @return the ToolPalette.
	 */
	public ToolBar getToolBar() {
		if ( toolBar == null ) {
			Image cutImage = getImage("cut.gif");
			Image copyImage = getImage("copy.gif");
			Image pasteImage = getImage("paste.gif");
			Image systemPasteImage = getImage("systempaste.gif");
			
			// Create the tool palette
			toolBar = new ToolBar( canvas );
			toolBar.setLayout(new GridBagLayout());
			toolBar.setBackground( Color.white );
			toolBar.addButton( "cutSelections", cutImage );
			toolBar.addButton( "copySelections", copyImage );
			toolBar.addButton( "paste", pasteImage );
			toolBar.addButton( "pasteFromSystem", systemPasteImage );
		}
		return toolBar;
	}
	/**
	 * @return the ToolPalette.
	 */
	public ToolPalette getToolPalette() {
		if ( toolPalette == null ) {
			Image selectionImage = getImage("select.gif");
			Image labelImage = getImage("label.gif");
			Image lineImage = getImage("line.gif");
			Image rectangleImage = getImage("box.gif");
			Image roundedRectangleImage = getImage("rrect.gif");
			Image ellipseImage = getImage("ellipse.gif");
			Image nGonImage = getImage("nsided.gif");
			
			// Create the tool palette
			toolPalette = new ToolPalette(canvas);
			toolPalette.setLayout(new GridBagLayout());
			toolPalette.addTool(new SelectionTool(canvas), "Select", selectionImage);
			toolPalette.addTool(new LabelTool(canvas), "Label", labelImage);
			toolPalette.addTool(new ConnectingLineTool(canvas), "Line", lineImage);
			toolPalette.addTool(new RectangleTool(canvas), "Box", rectangleImage);
			toolPalette.addTool(new RectangularCreationTool(canvas,RoundedRectangleShape.class), "Rounded", roundedRectangleImage);
			toolPalette.addTool(new EllipseTool(canvas), "Ellipse", ellipseImage);
			toolPalette.addTool(new AnySidedPolygonTool(canvas), "N-gon", nGonImage);
		}
		return toolPalette;
	}
	/**
	 * Initialize class
	 */
	protected void initialize() {
		setName("Drawlets");
		setSize(420, 300);
		setLayout(new GridBagLayout());
		setBackground( Color.white );

		// Create the canvas
		Component canvasComponent = getCanvasComponent();
		GridBagConstraints canvasConstraints = new GridBagConstraints();
		canvasConstraints.gridx = 2;
		canvasConstraints.gridy = 2;
		canvasConstraints.weightx = 1.0;
		canvasConstraints.weighty = 1.0;
		canvasConstraints.fill = GridBagConstraints.BOTH;
		add(canvasComponent, canvasConstraints);

		// Set the drawing style
		DrawingStyle style = new SimpleDrawingStyle();
		style.setBackgroundColor(Color.blue);
		style.setForegroundColor(Color.white);
		canvas.setStyle(style);

		GridBagConstraints toolPaletteConstraints = new GridBagConstraints();
		toolPaletteConstraints.gridx = 1;
		toolPaletteConstraints.gridy = 2;
		toolPaletteConstraints.anchor = GridBagConstraints.NORTH;
		add(getToolPalette(), toolPaletteConstraints);

		GridBagConstraints toolBarConstraints = new GridBagConstraints();
		toolBarConstraints.gridx = 1;
		toolBarConstraints.gridy = 1;
		toolBarConstraints.gridwidth = 3;
		toolBarConstraints.anchor = GridBagConstraints.WEST;
		toolBarConstraints.fill = GridBagConstraints.HORIZONTAL;
		Panel panel = new Panel();
		panel.setLayout( new BorderLayout() );
		panel.setBackground( Color.white );
		panel.add( getToolBar(), BorderLayout.WEST );
		add(panel, toolBarConstraints);
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args the arguments passed to the application on entry.
	 */
	public static void main(java.lang.String[] args) {
		try {
			java.awt.Frame frame = new ExitingFrame( "Drawlets Panel Example" );
			SimplePanel aTestCanvas;
			aTestCanvas = new SimplePanel();
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

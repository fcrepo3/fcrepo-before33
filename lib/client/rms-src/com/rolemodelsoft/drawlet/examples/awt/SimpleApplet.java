package com.rolemodelsoft.drawlet.examples.awt;

/**
 * @(#)SimpleApplet.java
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
 
public class SimpleApplet extends java.applet.Applet {
	/**
	 * The canvas to use.
	 */
	protected DrawingCanvas canvas;

	/**
	 * The tool palette for this applet.
	 */
	protected ToolPalette toolPalette;

	/**
	 * The style palette for this applet.
	 */
	protected StylePalette stylePalette;

	/**
	 * The tool bar for actions.
	 */
	protected ToolBar toolBar;

	/**
	 * Applet info.
	 *
	 * @return a String containing information on this applet
	 */
	public String getAppletInfo() {
		return "Simple Drawlets Example v1.1.6 (30 Mar 1999), by RoleModel Software.";
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
	 * @return the StylePalette
	 */
	public StylePalette getStylePalette() {
		if ( stylePalette == null ) {
			// Create the color palette
			stylePalette = new StylePalette(canvas);
			stylePalette.setLayout(new GridBagLayout());
			stylePalette.setBackground( Color.lightGray );
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
			Image cutImage = getImage("greygrey/cut.gif");
			Image copyImage = getImage("greygrey/copy.gif");
			Image pasteImage = getImage("greygrey/paste.gif");
			Image systemPasteImage = getImage("greygrey/systempaste.gif");
			
			// Create the tool palette
			toolBar = new ToolBar( canvas );
			toolBar.setLayout(new GridBagLayout());
			toolBar.setBackground( Color.lightGray );
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
			Image selectionImage = getImage("greygrey/select.gif");
			Image labelImage = getImage("greygrey/label.gif");
			Image lineImage = getImage("greygrey/line.gif");
			Image rectangleImage = getImage("greygrey/box.gif");
			Image roundedRectangleImage = getImage("greygrey/rrect.gif");
			Image ellipseImage = getImage("greygrey/ellipse.gif");
			Image nGonImage = getImage("greygrey/nsided.gif");
			
			// Create the tool palette
			toolPalette = new ToolPalette(canvas);
			toolPalette.setLayout(new GridBagLayout());
			toolPalette.setBackground( Color.lightGray );
			toolPalette.addTool(new SelectionTool(canvas), "Select", selectionImage);
			toolPalette.addTool(new LabelTool(canvas), "Label", labelImage);
			toolPalette.addTool(new ConnectingLineTool(canvas), "Line", lineImage);
			toolPalette.addTool(new RectangleTool(canvas), "Box", rectangleImage);
			toolPalette.addTool(new RectangularCreationTool(canvas,RoundedRectangleShape.class), "Rounded", roundedRectangleImage);
			toolPalette.addTool(new EllipseTool(canvas), "Ellipse", ellipseImage);
			toolPalette.addTool(new AnySidedPolygonTool(canvas), "N-gon", nGonImage);

			// This is something of a hack to make sure that the selection tool
			// (which is the default tool for SimpleDrawingCanvas is selected
			toolPalette.actionPerformed( new ActionEvent( toolPalette.getComponent(0), ActionEvent.ACTION_PERFORMED, "SelectionTool" ) );
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
		setBackground( Color.lightGray );
		
		setLayout(new GridBagLayout());

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
		panel.setBackground( Color.lightGray );
		panel.add( getToolBar(), BorderLayout.WEST );
		add(panel, toolBarConstraints);
	}
}

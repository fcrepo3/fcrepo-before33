package com.rolemodelsoft.drawlet.examples.awt;

/**
 * @(#)SimpleModelExample.java
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
import com.rolemodelsoft.drawlet.examples.*;
import com.rolemodelsoft.drawlet.shapes.lines.ConnectingLineTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RectangleTool;
import com.rolemodelsoft.drawlet.shapes.RectangularCreationTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RoundedRectangleShape;
import com.rolemodelsoft.drawlet.shapes.ellipses.EllipseTool;
import com.rolemodelsoft.drawlet.shapes.polygons.PolygonTool;
import com.rolemodelsoft.drawlet.shapes.polygons.AnySidedPolygonTool;
import com.rolemodelsoft.drawlet.text.LabelTool;
import com.rolemodelsoft.drawlet.util.ValueAdapter;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

/**
 * Although there are plenty of ways to use the drawlet framework, it may not
 * be apparent without some examples.  Here is a very simple one which provides
 * a basic drawing tool as a Panel with a simple underlying model.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class SimpleModelPanel extends SimplePanel implements ActionListener {

	/**
	 * The model to use for this applet
	 */
	protected SingleDrawingModel model;
	
	/**
	 * The temporary file name to save to and restore from
	 */
	protected static String TempFileName = "temp.rmd";

	/**
	 * The command panel
	 */
	protected Panel commandPanel;
	/**
	 * Default constructor.
	 */
	public SimpleModelPanel() {
		super();
	}
	/**
	 * SimplePanelExample constructor comment.
	 * 
	 * @param layout the layout manager to use with this application
	 */
	public SimpleModelPanel(java.awt.LayoutManager layout) {
		super(layout);
	}
	/**
	 * @param e the event
	 */
	public void actionPerformed (java.awt.event.ActionEvent e)
	{
		// Hack to get commands working
		String commandString = e.getActionCommand();
		if (commandString.equals("Clear")) getModel().clearDrawing();
		else if (commandString.equals("Save")) getModel().saveDrawing(TempFileName);
		else if (commandString.equals("Restore")) getModel().restoreDrawing(TempFileName);
		else if (commandString.equals("Print")) getModel().printDrawing(getFrame( this ));
	}
	/**
	 * @return Component the component holding the canvas.
	 */
	protected Component getCanvasComponent() {
		canvas = new SimpleDrawingCanvas(getModel().getDrawing());
		Component component = new BufferedDrawingCanvasComponent(canvas);
		ValueAdapter adapter = new ValueAdapter(model,"getDrawing",canvas,"setDrawing");
		component.setSize(300,240);
		return component;
	}
	/**
	 * @return the Frame.
	 */
	public Panel getCommandPanel () {
		if ( commandPanel == null ) {
			// add commands, quick and dirty
			commandPanel = new Panel();
			commandPanel.setLayout(new GridLayout(1,3,2,2));
			String[] commands = {"Clear", "Save", "Restore", "Print"};
			for (int i=0; i<commands.length; i++) {
				Button button = new Button(commands[i]);
				button.setActionCommand(commands[i]);
				button.addActionListener(this);
				commandPanel.add(button);
			}
		}
		return commandPanel;
	}
	/**
	 * @return the Frame.
	 */
	public Frame getFrame ( Component c ) {
		while ( ( c = c.getParent() ) != null ) {
			if ( c instanceof Frame ) return (Frame) c;
		}
		return null;
	}
	/**
	 * @return the model
	 */
	protected SingleDrawingModel getModel() {
		if ( model == null ) {
			model = new SingleDrawingModel();
		}
		return model;
	}
	/**
	 */
	public StylePalette getStylePalette() {
		if ( stylePalette == null ) {
			// Create the color palette
			stylePalette = new StylePalette(canvas);
			stylePalette.setLayout(new GridBagLayout());
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
	 * Initialize class
	 */
	protected void initialize() {
		super.initialize();

		GridBagConstraints commandPanelConstraints = new GridBagConstraints();
		commandPanelConstraints.gridx = 2;
		commandPanelConstraints.gridy = 3;
		commandPanelConstraints.anchor = GridBagConstraints.SOUTH;
		add(getCommandPanel(), commandPanelConstraints);
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args the arguments passed to the application on entry
	 */
	public static void main(java.lang.String[] args) {
		try {
			java.awt.Frame frame = new ExitingFrame( "Simple Model Example" );
			SimpleModelPanel aTestCanvas;
			aTestCanvas = new SimpleModelPanel();
			frame.add("Center", aTestCanvas);
			frame.setSize(aTestCanvas.getSize());
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of TestCanvas");
			exception.printStackTrace(System.out);
		}
	}
}

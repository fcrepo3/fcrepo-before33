package com.rolemodelsoft.drawlet.examples.jfc;

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
import com.rolemodelsoft.drawlet.jfc.*;
import com.rolemodelsoft.drawlet.shapes.lines.ConnectingLineTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RectangleTool;
import com.rolemodelsoft.drawlet.shapes.RectangularCreationTool;
import com.rolemodelsoft.drawlet.shapes.rectangles.RoundedRectangleShape;
import com.rolemodelsoft.drawlet.shapes.ellipses.EllipseTool;
import com.rolemodelsoft.drawlet.shapes.polygons.PolygonTool;
import com.rolemodelsoft.drawlet.shapes.polygons.AnySidedPolygonTool;
import com.rolemodelsoft.drawlet.text.LabelTool;
import com.rolemodelsoft.drawlet.examples.*;
import com.rolemodelsoft.drawlet.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This is a trivial example of how one might separate some underlying model 
 * from presentation to the user.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class SimpleModelPanel extends SimplePanel {

	/**
	 * the model for this application.
	 */
	protected SingleDrawingModel model;

	/**
	 * the temporary file name that will be saved to/restore from.
	 */
	protected static String TempFileName = "temp.rmd";
	/**
	 * Default constructor.
	 */
	public SimpleModelPanel() {
		super();
	}
	/**
	 * @param layout the layout this application should use.
	 */
	public SimpleModelPanel(java.awt.LayoutManager layout) {
		super(layout);
	}
	/**
	 * SimplePanelExample constructor comment.
	 * @param layout the layout this application should use.
	 * @param isDoubleBuffered determines whether the application will be
	 * double buffered or not.
	 */
	public SimpleModelPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
	}
	/**
	 * @param isDoubleBuffered  determines whether the application will be
	 * double buffered or not.
	 */
	public SimpleModelPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
	}
	/**
	 * @return Component the component holding the canvas.
	 */
	protected JComponent getCanvasComponent() {
		canvas = new SimpleDrawingCanvas(getModel().getDrawing());
		JComponent component = new JDrawingCanvasComponent(canvas);
		ValueAdapter adapter = new ValueAdapter(getModel(),"getDrawing",canvas,"setDrawing");
		return component;
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
	 * Initialize class
	 */
	protected void initialize() {
		super.initialize();
		toolBar.addSeparator();

		class ClearAction extends AbstractAction {
			SingleDrawingModel model;
			public ClearAction(String name, SingleDrawingModel model) {
				super(name);
				this.model = model;
			}
			public void actionPerformed(ActionEvent e) {
				model.clearDrawing();
			}
		}
		class SaveAction extends AbstractAction {
			SingleDrawingModel model;
			String fileName;
			public SaveAction(String name, SingleDrawingModel model, String fileName) {
				super(name);
				this.model = model;
				this.fileName = fileName;
			}
			public void actionPerformed(ActionEvent e) {
				model.saveDrawing(fileName);
			}
		}
		class RestoreAction extends AbstractAction {
			SingleDrawingModel model;
			String fileName;
			public RestoreAction(String name, SingleDrawingModel model, String fileName) {
				super(name);
				this.model = model;
				this.fileName = fileName;
			}
			public void actionPerformed(ActionEvent e) {
				model.restoreDrawing(fileName);
			}
		}
		toolBar.add(new ClearAction("Clear", getModel()));
		toolBar.add(new SaveAction("Save",getModel(),TempFileName));
		toolBar.add(new RestoreAction("Restore",getModel(),TempFileName));
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args the arguments passed to the application on entry.
	 */
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new ExitingFrame( "Drawlets" );
			SimpleModelPanel aDrawletsExample;
			aDrawletsExample = new SimpleModelPanel();
			frame.getContentPane().add("Center", aDrawletsExample);
			frame.setSize(aDrawletsExample.getSize());
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of com.rolemodelsoft.drawlet.examples.jfc.SimpleModelExample");
			exception.printStackTrace(System.out);
		}
	}
}

package com.rolemodelsoft.drawlet.examples.awt;

/**
 * @(#)FullPanel.java
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
import com.rolemodelsoft.drawlet.awt.*;
import java.awt.*;
import com.rolemodelsoft.drawlet.shapes.polygons.PolygonTool;
import com.rolemodelsoft.drawlet.shapes.lines.DrawLineTool;

public class FullPanel extends SimplePanel {
/**
 * FullPanelExample constructor comment.
 */
public FullPanel() {
	super();
}
/**
 * FullPanelExample constructor comment.
 * @param layout java.awt.LayoutManager
 */
public FullPanel(java.awt.LayoutManager layout) {
	super(layout);
}
	/**
	 * @return the ToolPalette.
	 */
	public ToolPalette getToolPalette() {
		if ( toolPalette == null ) {
			Image triangleImage = getImage("triangle.gif");
			Image pentagonImage = getImage("pentagon.gif");
			Image freeHandImage = getImage("freehand.gif");
			
			// Create the tool palette
			toolPalette = super.getToolPalette();
			toolPalette.addTool(new PolygonTool(canvas,3), "Triangle", triangleImage);
			toolPalette.addTool(new PolygonTool(canvas,5), "Pentagon", pentagonImage);
			toolPalette.addTool(new DrawLineTool(canvas), "Smooth Line", freeHandImage);
		}
		return toolPalette;
	}
	/**
	 * Initialize class
	 */
	protected void initialize() {
		super.initialize();

		GridBagConstraints stylePaletteConstraints = new GridBagConstraints();
		stylePaletteConstraints.gridx = 3;
		stylePaletteConstraints.gridy = 2;
		stylePaletteConstraints.anchor = GridBagConstraints.NORTH;
		add(getStylePalette(), stylePaletteConstraints);
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args the arguments passed to the application on entry.
	 */
	public static void main(java.lang.String[] args) {
		try {
			java.awt.Frame frame = new ExitingFrame( "Drawlet Panel Example" );
			FullPanel aTestCanvas;
			aTestCanvas = new FullPanel();
			frame.add("Center", aTestCanvas);
			frame.setSize(aTestCanvas.getSize());
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of TestCanvas");
			exception.printStackTrace(System.out);
		}
	}
}

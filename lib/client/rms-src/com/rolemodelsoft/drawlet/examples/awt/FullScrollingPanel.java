package com.rolemodelsoft.drawlet.examples.awt;

/**
 * @(#)FullScrollingPanel.java
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
import com.rolemodelsoft.drawlet.awt.*;
import com.rolemodelsoft.drawlet.basics.*;
import java.awt.*;

public class FullScrollingPanel extends FullPanel {
/**
 * FullScrollingPanel constructor comment.
 */
public FullScrollingPanel() {
	super();
}
/**
 * FullScrollingPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public FullScrollingPanel(java.awt.LayoutManager layout) {
	super(layout);
}
	/**
	 * @return the canvas.
	 */
	protected Component getCanvasComponent() {
		canvas = new SimpleDrawingCanvas(new SimpleDrawing(500,500));
		DrawingCanvasComponent canvasComponent = new BufferedDrawingCanvasComponent( canvas );
		ScrollPane component = new ScrollPane();
		component.add( canvasComponent );
		return component;
	}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
		try {
			java.awt.Frame frame = new ExitingFrame( "Drawlet Panel Example" );
			FullScrollingPanel aTestCanvas;
			aTestCanvas = new FullScrollingPanel();
			frame.add("Center", aTestCanvas);
			frame.setSize(aTestCanvas.getSize());
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of TestCanvas");
			exception.printStackTrace(System.out);
		}
}
}

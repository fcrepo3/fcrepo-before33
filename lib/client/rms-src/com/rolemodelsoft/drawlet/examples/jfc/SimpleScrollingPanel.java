package com.rolemodelsoft.drawlet.examples.jfc;

/**
 * @(#)SimpleScrollingPanel.java
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
import javax.swing.*;
import java.awt.*;
/**
 * This is a beginning attempt at a scrolling implemenation with JFC. There are obvious problems,
 * possibly dealing with the ViewPort, at least in VisualAge (it has not yet been tested outside).
 * Hopefully it will at least give you an idea for how to implement it, and the problems may be fixed
 * either outside of VisualAge or in Java 2.
 */
public class SimpleScrollingPanel extends SimplePanel {
/**
 * SimpleScrollingPanel constructor comment.
 */
public SimpleScrollingPanel() {
	super();
}
/**
 * SimpleScrollingPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public SimpleScrollingPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * SimpleScrollingPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public SimpleScrollingPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * SimpleScrollingPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public SimpleScrollingPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
	/**
	 * @return JComponent the component holding the canvas.
	 */
	protected JComponent getCanvasComponent() {
		canvas = new SimpleDrawingCanvas( new SimpleDrawing( 500, 500 ) );
		JDrawingCanvasComponent component = new JDrawingCanvasComponent( canvas );
		JScrollPane scroller = new JScrollPane( component );
		return scroller;
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * 
	 * @param args the arguments passed to the application on entry
	 */
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new ExitingFrame( "Drawlets" );
			SimpleScrollingPanel aTestCanvas;
			aTestCanvas = new SimpleScrollingPanel();
			frame.getContentPane().add("Center", aTestCanvas);
			frame.setSize(aTestCanvas.getSize());
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of TestCanvas");
			exception.printStackTrace(System.out);
		}
	}
}

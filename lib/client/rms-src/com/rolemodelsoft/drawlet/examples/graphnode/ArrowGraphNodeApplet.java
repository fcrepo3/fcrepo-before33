package com.rolemodelsoft.drawlet.examples.graphnode;

/**
 * @(#)GraphNodeApplet.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1997 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
import java.awt.*;

/**
 * Here is a pathetically naive example of a simple Nodes and Arcs
 * Application.  It is meant to illustrate how simple it is to add your own
 * figures, and connect them.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class ArrowGraphNodeApplet extends java.applet.Applet {

	/**
	 * the canvas for this applet.
	 */
	DrawingCanvas canvas;
	/**
	 * Applet info.
	 */
	public String getAppletInfo() {
		return "ArrowGraphNode Drawlet Example v1.1.6 (19 Feb 1999), by RoleModel Software.";
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
		setLayout(new BorderLayout());
		
		// Create the canvas
		DrawingCanvasComponent canvasComponent = new BufferedDrawingCanvasComponent();
		canvas = canvasComponent.getCanvas();
		canvas.setTool(new ArrowGraphNodeTool(canvas));
		canvasComponent.setSize(getSize());
		add("Center",canvasComponent);
	}
}

package com.rolemodelsoft.drawlet.examples.graphnode;

/**
 * @(#)GraphNodeTool.java
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
import java.awt.*;
import java.awt.event.*;

/**
 * Here is a simple tool that extends the SelectionTool to create GraphNodes
 * when a doubleClick event occurs.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class GraphNodeTool extends SelectionTool {
	/**
	 * @param canvas the canvas this tool will draw on/manipulate
	 */

	public GraphNodeTool(DrawingCanvas canvas) {
		super(canvas);
	}
	/**
	 * Answer a new graph node
	 */
	protected GraphNode basicNewGraphNode() {
		return new GraphNode();
	}
	/**
	 * Called if the mouse is double-clicked.
	 *
	 * @param e the event 
	 * @see #mouseClicked
	 */
	protected void mouseDoubleClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		super.mouseDoubleClicked(e);
		if (e.isConsumed())
			return;
		Figure newFigure = basicNewGraphNode();
		newFigure.setStyle(canvas.getStyle());
		newFigure.move(e.getX(), e.getY());
		canvas.addFigure(newFigure);
		e.consume();
	}
}

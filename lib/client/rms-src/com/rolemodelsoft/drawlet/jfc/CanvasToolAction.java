package com.rolemodelsoft.drawlet.jfc;

/**
 * @(#)CanvasToolAction.java
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
import javax.swing.*;

/**
 * This class is used to create tools that will be used on 
 * the canvas (ex figure creation, figure selection etc...).
 * @version 	1.1.6, 12/29/98
 */

public class CanvasToolAction extends AbstractAction {

	/**
	 * The canvas for which the tool is to be set.
	 */
	DrawingCanvas canvas;

	/**
	 * The tool the canvas is to be set to use.
	 */
	InputEventHandler tool;
	/**
	 * Creates a new, default CanvasToolAction.
	 */
	public CanvasToolAction() {
		super();
	}
/**
 * Creates a new CanvasToolAction and initializes it with the given name.
 *
 * @param name a String representing the name.
 */
public CanvasToolAction(String name) {
	super(name);
}
	/**
	 * Create a CanvasToolAction which will set the canvases tool when performed.
	 * 
	 * @param name the name for the action
	 * @param canvas the canvas to set the tool for
	 * @param tool the tool to have the canvas use
	 */
	public CanvasToolAction(String name, DrawingCanvas canvas, InputEventHandler tool) {
		super(name);
		this.canvas = canvas;
		this.tool = tool;
	}
	/**
	 * Creates a new CanvasToolAction and initializes it with the given name and icon.
	 *
	 * @param name the name for this action
	 * @param icon the icon
	 */
	public CanvasToolAction(String name, Icon icon) {
		super(name, icon);
	}
	/**
	 * Create a CanvasToolAction which will set the canvases tool when performed.
	 * 
	 * @param name the name for this action
	 * @param icon the icon
	 * @param canvas the canvas to set the tool for
	 * @param tool the tool to have the canvas use
	 */
	public CanvasToolAction(String name, Icon icon, DrawingCanvas canvas, InputEventHandler tool) {
		super(name, icon);
		this.canvas = canvas;
		this.tool = tool;
	}
	/**
	 * Set the tool of the canvas.
	 * 
	 * @param e the event
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
			canvas.setTool(tool);
	}
}

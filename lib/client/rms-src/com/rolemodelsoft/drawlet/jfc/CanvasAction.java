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
 * Basically this class is used to invoke actions on the canvas (ex. copy, paste etc...).
 * @version 	1.1.6, 12/29/98
 */

public class CanvasAction extends AbstractAction {

	/**
	 * The canvas for which the tool is to be set.
	 */
	DrawingCanvas canvas;

	/**
	 * The action to be invoked on the canvas.
	 */
	String action;
	/**
	 * Creates a new, default CanvasToolAction.
	 */
	public CanvasAction() {
		super();
	}
/**
 * Creates a new CanvasToolAction and initializes it with the given name.
 *
 * @param name a String representing the name.
 */
public CanvasAction(String name) {
	super(name);
}
	/**
	 * Create a CanvasToolAction which will set the canvases tool when performed.
	 * 
	 * @param name the name for the action
	 * @param canvas the canvas to set the tool for
	 * @param tool the tool to have the canvas use
	 */
	public CanvasAction(String name, DrawingCanvas canvas, String action) {
		super(name);
		this.canvas = canvas;
		this.action = action;
	}
	/**
	 * Creates a new CanvasToolAction and initializes it with the given name and icon.
	 *
	 * @param name the name for this action
	 * @param icon the icon
	 */
	public CanvasAction(String name, Icon icon) {
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
	public CanvasAction(String name, Icon icon, DrawingCanvas canvas, String action) {
		super(name, icon);
		this.canvas = canvas;
		this.action = action;
	}
	/**
	 * Set the tool of the canvas.
	 * 
	 * @param e the event
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		try {
			canvas.getClass().getMethod(action, new Class[] {}).invoke( canvas, new Class[] {} );
		} catch ( Exception exception ) {
		}
	}
}

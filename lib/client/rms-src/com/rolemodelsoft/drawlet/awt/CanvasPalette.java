package com.rolemodelsoft.drawlet.awt;

/**
 * @(#)CanvasPalette.java
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

import java.awt.*;
import java.awt.event.*;
import com.rolemodelsoft.drawlet.*;

/**
 * Represents a set of objects, tied to some visual representation
 * and a method for user invocation, associated with a
 * <code>DrawingCanvas</code> which they act on.
 *
 * @version 	1.1.6, 12/28/98
 */

 public abstract class CanvasPalette extends Panel implements ActionListener {
	/**
	 * The canvas on which the tools operate.
	 */
	protected DrawingCanvas canvas;

	/**
	 * Called if an action occurs in the receiver.
	 *
	 * @param evt the event which triggered this method
	 */
	public abstract void actionPerformed(ActionEvent evt);
	/**
	 * Add a button to the receiver, assigning it the given name.
	 * 
	 * @param label the name to give the <code>Button</code>
	 * @return	the <code>Button</code> created
	 */
	protected Button addButton(String label) {
		Button button = new Button(label);
		button.addActionListener(this);
		add(button);
		return button;
	}
	/**
	 * Answer the <code>DrawingCanvas</code> to which we are applying tools.
	 * 
	 * @return	the DrawingCanvas to which we are applying tools
	 */
	public DrawingCanvas getCanvas() {
		return canvas;
	}
	/**
	 * Answer the index of the given <code>Component</code>.  
	 * 
	 * @param comp the <code>Component</code> we're looking for
	 * @return an integer representing the index of the <code>Component</code>;
	 * -1 if the <code>Component</code> is not found.
	 */
	protected int indexOf(Component comp) {
		Component myComponents[] = getComponents();
		for (int i=0; i < myComponents.length; i++) 
			if (comp == myComponents[i]) return i;
		return -1; //we don't expect this to happen
	}
	/**
	 * Set the <code>DrawingCanvas</code> to which we are applying tools.
	 *
	 * @param canvas the <code>DrawingCanvas</code> to which tools will be applied
	 */
	public void setCanvas(DrawingCanvas newCanvas) {
		this.canvas = newCanvas;
	}
}

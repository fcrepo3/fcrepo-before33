package com.rolemodelsoft.drawlet.awt;

/**
 * @(#)ToolBar.java
 *
 * Copyright (c) 1999-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
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
import java.awt.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * Represents a set of Tools, associated with a
 * <code>DrawingCanvas</code> which they act on.
 */
 
public class ToolBar extends CanvasPalette {
	/**
	 * The list of tools that correspond to the setter buttons.
	 */
	protected Vector buttons = new Vector(4);
	/** 
	 * Creates a new <code>ToolBar</code> and assocaties it
	 * with the given <code>DrawingCanvas</code>.
	 *
	 * @param canvas the <code>DrawingCanvas</code> to
	 * associate with the <code>ToolBar</code>.
	 */
	public ToolBar(DrawingCanvas canvas) {
		this.canvas = canvas;
	}
	/**
	 * Called if an action occurs in the toolbar.
	 *
	 * @param evt the event
	 */
	public void actionPerformed(java.awt.event.ActionEvent evt) {
		int index = indexOf((Component) evt.getSource());
		int numButtons = buttons.size();
		if (index < numButtons) {
			Method button = (Method) buttons.elementAt(index);
			try {
				button.invoke( canvas, new Class[] {} );
			} catch (Exception e) {
				System.out.println("Exception " + e + " when invoking " + button.getName());
			}
			return;
		}
	}
	/**
	 * Add a tool to the receiver, associating it with the given <code>Image</code>.
	 * 
	 * @param tool the tool to add
	 * @param image the <code>Image</code> to associate with the tool
	 */
	public void addButton(String tool, Image image) {
		Method method = null;
		try {
			method = canvas.getClass().getMethod(tool, new Class[0]);
			buttons.addElement(method);
		} catch (Exception e) {System.out.println("can't find method " + tool + " or the parameters");}
		ImageButton button = new ImageButton(image);
		button.addActionListener(this);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = buttons.indexOf( method ) + 1;
		constraints.gridy = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets( 2, 4, 2, 0 );
		add(button, constraints);
	}
}

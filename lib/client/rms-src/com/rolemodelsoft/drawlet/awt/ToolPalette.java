package com.rolemodelsoft.drawlet.awt;

/**
 * @(#)ToolPalette.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1996 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Although there are multiple ways a DrawingCanvas can get its tool(s), a typical
 * approach is providing a palette from which to choose them.  This class provides
 * a very simple version of one.  There is obviously room for improvement (e.g.
 * tool tips, etc.).
 * NOTE: Right now, the layout managers are kind of mixed up. They should all be
 * standardized (probably to GridBag) and tested to ensure that icons and labels can
 * be interchanged and still look good.
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class ToolPalette extends CanvasPalette {

	/**
	 * The list of tools that correspond to the buttons.
	 */
	protected Vector tools = new Vector(5);

	/**
	 * The last button invoked on this ToolPalette
	 */
	protected Component lastButton;
	/** 
	 * Creates a new <code>ToolPalette</code> and associates
	 * it with the given <code>DrawingCanvas<c/ode>.
	 *
	 * @param canvas the canvas on which to apply tools.
	 */
	public ToolPalette(DrawingCanvas canvas) {
		this.canvas = canvas;
	}
	/**
	 * Called if an action occurs in the palette.
	 * Set the tool of the canvas as appropriate.
	 *
	 * @param evt the event
	 */
	public void actionPerformed(ActionEvent evt) {
		if ( lastButton instanceof ImageButton )
			((ImageButton)lastButton).setHighlight( false );

		if ( evt.getSource() instanceof ImageButton )
			((ImageButton)evt.getSource()).setHighlight( true );

		lastButton = (Component)evt.getSource();
		canvas.setTool((InputEventHandler) tools.elementAt(indexOf((Component) evt.getSource())));
	}
	/**
	 * Add a tool to the receiver, labeling it appropriately.
	 * 
	 * @param tool the tool to add
	 * @param label the label to associate with the tool
	 */
	public void addTool(InputEventHandler tool, String label) {
		addToolButton(tool, label);
	}
	/**
	 * Add a tool to the receiver, associating it with
	 * the given label and <code>Image</code>.
	 *
	 * @param tool the EventHandler to add
	 * @param label the label to associate with the tool
	 * @param image the <code>Image</code> to associate with the tool
	 */
	public void addTool(InputEventHandler tool, String label, Image image) {
		tools.addElement(tool);
		ImageButton b = new ImageButton(label, image);
		b.addActionListener( this );
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 1;
		constraints.weighty = 1.0;
		constraints.insets = new Insets( 0, 2, 2, 2 );
		add(b, constraints);
	}
	/**
	 * Add a tool to the receiver in Button form, labeling it appropriately.
	 * 
	 * @param tool the tool to add
	 * @param label the label to associate with the tool
	 */
	protected Button addToolButton(InputEventHandler tool, String label) {
		tools.addElement(tool);
		return addButton(label);
	}
}

package com.rolemodelsoft.drawlet.awt;

/**
 * @(#)StylePalette.java
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
import java.util.*;
import java.lang.reflect.*;
import com.rolemodelsoft.drawlet.*;
import com.rolemodelsoft.drawlet.basics.SimpleDrawing;
import com.rolemodelsoft.drawlet.shapes.rectangles.RectangleShape;
import com.rolemodelsoft.drawlet.shapes.lines.Line;
import com.rolemodelsoft.drawlet.text.TextLabel;

/**
 * Represents a set of objects, including ColorButtons,
 * Setters, and a StyleViewer, associated with a
 * <code>DrawingCanvas</code> which they act on.
 *
 * @version 	1.1.6, 12/28/98
 */

 public class StylePalette extends CanvasPalette {

	/**
	 * The list of tools that correspond to the color buttons.
	 */
	protected Vector colors = new Vector(5);

	/**
	 * The currently selected color.
	 */
	protected Color color = Color.black;

	/**
	 * The list of tools that correspond to the setter buttons.
	 */
	protected Vector setters = new Vector(5);

	/**
	 * The currently selected colorButton.
	 */
	protected ColorButton currentColorButton;

	/**
	 * A flag tracking whether there are any color buttons yet or not.
	 */
	protected boolean areButtons;

	/**
	 * The drawing associated with this palette.
	 */
	protected Drawing drawing;

	/**
	 * The viewer which identifies the current style.
	 */
	protected Component viewer;
	/** 
	 * Creates a new <code>StylePalette</code> and associates it
	 * with the given <code>DrawingCanvas</code>.
	 *
	 * @param canvas the canvas on which to apply tools.
	 */
	public StylePalette(DrawingCanvas canvas) {
		this.canvas = canvas;
	}
	/**
	 * Called if an action occurs in the palette.
	 * Set the style of the canvas as appropriate.
	 *
	 * @param evt the event
	 */
	public void actionPerformed(ActionEvent evt) {
		int index = indexOf((Component) evt.getSource());
		int numColors = colors.size();
		if (index < numColors) {
			color = (Color) colors.elementAt(index);
			currentColorButton.setHighlighted( false );
			currentColorButton = (ColorButton)evt.getSource();
			currentColorButton.setHighlighted( true );
			return;
		}
		index = index - numColors;
		int numSetters = setters.size();
		DrawingStyle style = canvas.getStyle();
		if (index < numSetters) {
			Method setter = (Method) setters.elementAt(index);
			try {
				setter.invoke(style, new Object[] {color});
				updateDrawing();
				updateViewer();
			} catch (Exception e) {
				System.out.println("Exception " + e + " when invoking " + setter.getName());
			}
			canvas.setStyle(style);
			return;
		}
		Figure[] figures = canvas.getSelections();
		for (int i = 0; i < figures.length; i++) {
			figures[i].setStyle(style);
		}
		//canvas.repaint();
		return;
	}
	/**
	 * Add an applier to the receiver, labeling it appropriately.
	 * 
	 * @param label the label to associate with the applier
	 */
	public void addApply(String label) {
		Button button = new Button(label);
		button.addActionListener(this);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = ((colors.size() - ( colors.size() & 1 )) / 2) + setters.size();
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets( 2, 2, 2, 0 );
		add(button, constraints);
	}
	/**
	 * Add a <code>Color</code> to the receiver, with
	 * the given label. Basically, this creates a new
	 * <code>ColorButton</code> with the given <code>Color</code>
	 * and label.
	 * 
	 * @param color the color to add
	 * @param label the label to associate with the color
	 */
	public void addColor(Color color, String label) {
		colors.addElement(color);
		ColorButton button = new ColorButton(label, color);
		button.addActionListener(this);
		GridBagConstraints constraints = new GridBagConstraints();
		int colorIndex;
		if ( color == null ) colorIndex = colors.size() - 1;
		else colorIndex = colors.indexOf( color );
		constraints.gridx = colorIndex & 1;
		constraints.gridy = (colorIndex - (colorIndex & 1)) / 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets( 2, 2, 2, 2 );
		constraints.weightx = 1;
		constraints.weighty = 1;
		add(button, constraints);
		if ( ! areButtons ) {
			currentColorButton = button;
			button.setHighlighted( true );
			areButtons = true;
		}
	}
	/**
	 * Add a setter to the receiver, labeling it appropriately.
	 * 
	 * @param setter the setter to add
	 * @param label the label to associate with the setter
	 */
	public void addSetter(String setter, String label) {
		Method method = null;
		try {
			method = canvas.getStyle().getClass().getMethod(setter, new Class[] {Class.forName("java.awt.Color")});
			setters.addElement(method);
		} catch (Exception e) {System.out.println("can't find method " + setter + " or class Color... this should never happen!");}
		Button button = new Button(label);
		button.addActionListener(this);
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = ((colors.size() - ( colors.size() & 1 )) / 2) + setters.indexOf( method );
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.insets = new Insets( 2, 2, 2, 0 );
		add(button, constraints);
	}
	/**
	 * Add a viewer to the palette to indicate the current style.
	 */
	public void addStyleViewer() {
		drawing = new SimpleDrawing();
		drawing.setStyle(canvas.getStyle());
		drawing.addFigure(new RectangleShape(5,5,25,15));
		drawing.addFigure(new Line(0,0,15,10));
		Figure label = new TextLabel("text");
		label.move(5,5);
		drawing.addFigure(label);
		updateDrawing();
		viewer = new PaintableViewer((Paintable)drawing);
		viewer.setBackground(canvas.getStyle().getBackgroundColor());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridwidth = 2;
		constraints.insets = new Insets(4,4,4,4);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		add(viewer,constraints);
	}
	/**
	 * Make sure the drawing indicates the current style, if we have one.
	 */
	public void updateDrawing() {
		if (drawing == null)
			return;
		drawing.setStyle(canvas.getStyle());
		for (FigureEnumeration e = drawing.figures(); e.hasMoreElements(); ) 
			e.nextElement().setStyle(canvas.getStyle());
	}
	/**
	 * Make sure the viewer shows the current style, if we are showing one.
	 */
	public void updateViewer() {
		if (viewer == null)
			return;
		viewer.setBackground(canvas.getStyle().getBackgroundColor());
		viewer.repaint();
	}
}

package com.rolemodelsoft.drawlet.shapes;

/**
 * @(#)FilledShape.java
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
import com.rolemodelsoft.drawlet.basics.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * This provides basic default functionality for Figures that draw shapes which
 * may be opaque.  It provides most of its functionality based on 
 * its getBounds(), and forces concrete subclasses to define, at a minimum:
 *	paintStrokes(Graphics);
 *	paintFilled(Graphics);
 *	getBounds();
 *	basicTranslate(int,int);
 *	basicReshape(int,int,int,int);
 *
 * @version 	1.1.6, 12/29/98
 */
 
public abstract class FilledShape extends AbstractShape {

	/**
	 * The line color for drawing the figure is cached here.
	 */
	protected Color lineColor = defaultLineColor();

	/**
	 * The fill color for drawing the figure is cached here.
	 */
	protected Color fillColor = defaultFillColor();

	/**
	 * Reshapes the receiver to the specified bounding box.
	 * Subclasses should probably provide synchronized versions if they're 
	 * modifying attributes of the receiver.
	 *
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param width the width of the figure
	 * @param height the height of the figure
	 */
	protected void basicReshape(int x, int y, int width, int height)  {
	}
	/**
	 * Moves the receiver in the x and y direction.
	 * 
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 */
	protected void basicTranslate(int x, int y)  {
	}
	/**
	 * Answer the default/initial value for fillColor
	 * 
	 * @return	the default/initial value for fillColor
	 */
	protected Color defaultFillColor() {
		return Color.gray;
	}
	/**
	 * Answer the default/initial value for lineColor
	 * 
	 * @return	the default/initial value for lineColor
	 */
	protected Color defaultLineColor() {
		return Color.black;
	}
	/** 
	 * Returns the current bounds of the receiver.
	 * 
	 * @return	a Rectangle representing the current bounds of the receiver.
	 */
	public abstract Rectangle getBounds();
	/**
	 * Answer the Color to use when filling the shape.
	 * 
	 * @return	the Color to use when filling the shape
	 */
	public Color getFillColor() {
		return fillColor;
	}
	/**
	 * Answer the Color to use when drawing lines.
	 * 
	 * @return	the Color to use when drawing lines
	 */
	public Color getLineColor() {
		return lineColor;
	}
	/** 
	 * Answer the DrawingStyle which defines how to paint the figure.
	 * 
	 * @return	the DrawingStyle which defines how to paint the figure.
	 */
	public DrawingStyle getStyle()  {
		DrawingStyle style = super.getStyle();
		style.setLineColor(getLineColor());
		style.setFillColor(getFillColor());
		return style;
	}
	/**
	 * Answer whether the receiver fills in its inside 
	 * (obscuring things underneath) or not.
	 * 
	 * @return	boolean value of <code>true</code> if the receiver
	 * fills in its inside;
	 * 			<code>false</code> otherwise.
	 */
	public boolean isOpaque() {
		return fillColor != null;
	}
	/**
	 * Answer whether the receiver draws its lines
	 * (obscuring things underneath) or not.
	 * 
	 * @return boolean value of <code>true</code> if the receiver draws its lines;
	 * 			<code>false</code> otherwise.
	 */
	public boolean isStroked() {
		return (getLineColor() != null);
	}
	/** 
	 * Paints the figure.
	 *
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g)  {
		if (isOpaque()) paintFilled(g);
		if (isStroked()) paintStrokes(g);
	}
	/**
	 * Paint the shape, filling all contained area
	 *
	 * @param g the specified Graphics window
	 */
	public void paintFilled(Graphics g)  {
		g.setColor(getFillColor());
		// fill figure
	}
	/**
	 * Paint the outline of the shape
	 *
	 * @param g the specified Graphics window
	 */
	public void paintStrokes(Graphics g)  {
		g.setColor(getLineColor());
		// draw figure
	}
	/**
	 * Set the Color to use when filling the shape.
	 * 
	 * @param color the color
	 */
	public void setFillColor(Color color) {
		Color oldColor = fillColor;
		fillColor = color;
		firePropertyChange(FILL_COLOR_PROPERTY,oldColor,color);
	}
	/**
	 * Set the Color to use when drawing lines.
	 * 
	 * @param color the color
	 */
	public void setLineColor(Color color) {
		Color oldColor = lineColor;
		lineColor = color;
		firePropertyChange(LINE_COLOR_PROPERTY, oldColor, color);
	}
	/** 
	 * Set the DrawingStyle defining how to paint the figure.
	 * 
	 * @param style the specified DrawingStyle.
	 */
	public void setStyle(DrawingStyle style) {
		DrawingStyle oldStyle = getStyle();
		if (style != null) {
			setLineColor(style.getLineColor());
			setFillColor(style.getFillColor());
		}
		firePropertyChange(STYLE_PROPERTY, oldStyle, style);
	}
}

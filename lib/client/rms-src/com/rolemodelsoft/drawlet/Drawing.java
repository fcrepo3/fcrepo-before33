package com.rolemodelsoft.drawlet;

/**
 * @(#)Drawing.java
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
import com.rolemodelsoft.drawlet.util.Duplicatable;
import java.io.Serializable;
import java.beans.PropertyChangeListener;

/**
 * This interface defines a generic Drawing which holds a SequenceOfFigures
 * which can be painted and potentially manipulated.  It is expected that this
 * will be the fundamental unit to store and retrieve diagrams, pictures, etc.
 *
 * @version 	1.1.6, 12/28/98
 */
public interface Drawing extends SequenceOfFigures, Paintable {
	/** 
	 * The size property selector
	 */
	public static String SIZE_PROPERTY = "size";

	/** 
	 * The style property selector
	 */
	public static String STYLE_PROPERTY = "style";
	/** 
	 * Add a PropertyChangeListener.
	 * 
	 * @param listener the listener to add.
	 */
	public abstract void addPropertyChangeListener( PropertyChangeListener listener );
	/** 
	 * Returns the size of this drawing.
	 * 
	 * @return a Dimension representing the size of this drawing.
	 */
	public abstract Dimension getSize();
	/** 
	 * Answer the style which defines how to paint on the canvas.
	 *
	 * @return the DrawingStyle which defines how to paint on the canvas
	 */
	public abstract DrawingStyle getStyle();
	/** 
	 * Returns whether this drawing is sized dynamically or statically.
	 */
	public abstract boolean isDynamicSize();
	/** 
	 * Paints all of the parts of the drawing.
	 * 
	 * @param g the specified Graphics window
	 */
	public abstract void paint(Graphics g);
	/** 
	 * Remove the specified PropertyChangeListener.
	 * 
	 * @param listener the listener to remove.
	 */
	public abstract void removePropertyChangeListener( PropertyChangeListener listener );
	/** 
	 * Sets whether this drawing is sized dynamically or statically.
	 *
	 * @param dynamicSize boolean specifying the dynamicSize state the drawing is to have.
	 */
	public abstract void setDynamicSize( boolean dynamicSize );
	/** 
	 * Sets the size of this drawing.
	 * 
	 * @param width the width the drawing should be set to.
	 * @param height the height the drawing should be set to.
	 */
	public abstract void setSize( int width, int height );
	/** 
	 * Resizes the receiver to the specified dimension.
	 *
	 * @param d the new dimension
	 */
	public abstract void setSize(Dimension d);
	/** 
	 * Set the style defining how to paint the receiver.
	 *
	 * @param style the specified DrawingStyle
	 */
	public abstract void setStyle(DrawingStyle style);
}

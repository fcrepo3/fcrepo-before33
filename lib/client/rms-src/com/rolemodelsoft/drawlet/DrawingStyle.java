package com.rolemodelsoft.drawlet;

/**
 * @(#)DrawingStyle.java
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
import com.rolemodelsoft.drawlet.util.Duplicatable;
import java.awt.*;

/**
 * Defines a set of getters and setters for drawing styles.
 * DrawingStyles are used by Figure and DrawingCanvas to define various attributes.
 * @version 	1.1.6, 12/28/98
 */
 
public interface DrawingStyle extends Duplicatable {
	/**
	 * Answer the Color to use for the background.
	 * @return Color the Color to use for the background
	 */
	public abstract Color getBackgroundColor();
	/**
	 * Answer the Color to use when filling the figure.
	 *
	 * @return the Color to use when filling the figure
	 */
	public abstract Color getFillColor();
	/** 
	 * Answer the Font with which to paint text.
	 *
	 * @return the Font with which to paint text
	 */
	public abstract Font getFont();
	/**
	 * Answer the Color to use for the foreground.
	 *
	 * @return the Color to use for the foreground
	 */
	public abstract Color getForegroundColor();
	/**
	 * Answer the Color to use to highlight areas of the canvas.
	 *
	 * @return the Color to use to highlight areas of the canvas
	 */
	public abstract Color getHighlightColor();
	/**
	 * Answer the Color to use when drawing lines.
	 *
	 * @return the Color to use when drawing lines
	 */
	public abstract Color getLineColor();
	/**
	 * Answer the Color to use for the background when indicating selection.
	 *
	 * @return the Color to use for the background when indicating selection
	 */
	public abstract Color getSelectionBackgroundColor();
	/**
	 * Answer the Color to use for the foreground when indicating selection.
	 *
	 * @return the Color to use for the foreground when indicating selection
	 */
	public abstract Color getSelectionForegroundColor();
	/**
	 * Answer the Color to use when drawing text.
	 *
	 * @return the Color to use when drawing text
	 */
	public abstract Color getTextColor();
	/**
	 * Set the Color to use for the background.
	 *
	 * @param color the color
	 */
	public abstract void setBackgroundColor(Color color);
	/**
	 * Set the Color to use when filling the figure.
	 *
	 * @param color the color
	 */
	public abstract void setFillColor(Color color);
	/** 
	 * Set the Font with which to paint text
	 *
	 * @param newFont the Font to use for text
	 */
	public abstract void setFont(Font newFont);
	/**
	 * Set the Color to use for the foreground.
	 *
	 * @param color the color
	 */
	public abstract void setForegroundColor(Color color);
	/**
	 * Set the Color to use to highlight areas of the canvas.
	 *
	 * @param color the color
	 */
	public abstract void setHighlightColor(Color color);
	/**
	 * Set the Color to use when drawing lines.
	 *
	 * @param color the color
	 */
	public abstract void setLineColor(Color color);
	/**
	 * Set the Color to use for the background during selection.
	 *
	 * @param color the color
	 */
	public abstract void setSelectionBackgroundColor(Color color);
	/**
	 * Set the Color to use for the foreground during selection.
	 *
	 * @param color the color
	 */
	public abstract void setSelectionForegroundColor(Color color);
	/**
	 * Set the Color to use when drawing text.
	 *
	 * @param color the color
	 */
	public abstract void setTextColor(Color color);
}

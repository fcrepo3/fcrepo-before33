package com.rolemodelsoft.drawlet.basics;

/**
 * @(#)SimpleDrawingStyle.java
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
import java.awt.*;
import java.util.Hashtable;

/**
 * This provides basic functionality necessary to provide a meaningful
 * working version of a DrawingStyle. 
 *
 * @version 	1.1.6, 12/28/98
 */
 
public class SimpleDrawingStyle implements DrawingStyle {

	/**
	 * Holds this style's background color.
	 */
	protected Color backgroundColor = defaultBackgroundColor();

	/**
	 * Holds this style's foreground color.
	 */
	protected Color foregroundColor = defaultForegroundColor();

	/**
	 * Holds this style's font.
	 */
	protected Font font = defaultFont();

	/**
	 * Holds this style's fill color.
	 */
	protected Color fillColor = defaultFillColor(); 

	/**
	 * Holds this style's line color.
	 */
	protected Color lineColor; // = defaultLineColor();  Let it be the same as foregroundColor

	/**
	 * Holds this style's text color.
	 */
	protected Color textColor; // = defaultTextColor();  Let it be the same as foregroundColor

	/**
	 * Holds this style's selection background color.
	 */
	protected Color selectionBackgroundColor = defaultSelectionBackgroundColor();

	/**
	 * Holds this style's selection foreground color.
	 */
	protected Color selectionForegroundColor = defaultSelectionForegroundColor();

	/**
	 * Holds this style's highlight color.
	 */
	protected Color highlightColor = defaultHighlightColor();

	/**
	 * Answer the default/initial value for backgroundColor
	 * 
	 * @return	a Color representing the default/initial value for backgroundColor
	 */
	protected Color defaultBackgroundColor() {
		return Color.white;
	}
	/**
	 * Answer the default/initial value for fillColor
	 * 
	 * @return	a Color representing the default/initial value for fillColor
	 */
	protected Color defaultFillColor() {
		return Color.lightGray;
	}
	/**
	 * Answer the default/initial value for the font
	 * 
	 * @return	a Font representing the default/initial value for the font
	 */
	protected Font defaultFont()  {
		Font newFont; // = Font.getFont("font.default");
	//	if (newFont == null)
			newFont = new Font("TimesRoman",Font.PLAIN,12);
		return newFont;
	}
	/**
	 * Answer the default/initial value for foregroundColor
	 * 
	 * @return	a Color representing the default/initial value for foregroundColor
	 */
	protected Color defaultForegroundColor() {
	return Color.black;
	}
	/**
	 * Answer the default/initial value for selectionBackgroundColor
	 * 
	 * @return	a Color representing the default/initial value for
	 * selectionBackgroundColor
	 */
	protected Color defaultHighlightColor() {
		return Color.gray;
	}
	/**
	 * Answer the default/initial value for lineColor
	 * 
	 * @return	a Color representing the default/initial value for lineColor
	 */
	protected Color defaultLineColor() {
		return getForegroundColor();
	}
	/**
	 * Answer the default/initial value for selectionBackgroundColor
	 * 
	 * @return	a Color representing the default/initial value for
	 * selectionBackgroundColor
	 */
	protected Color defaultSelectionBackgroundColor() {
		return getForegroundColor();
	}
	/**
	 * Answer the default/initial value for selectionForegroundColor
	 * 
	 * @return	a Color representing the default/initial value for
	 * selectionForegroundColor
	 */
	protected Color defaultSelectionForegroundColor() {
		return getBackgroundColor();
	}
	/**
	 * Answer the default/initial value for textColor
	 * 
	 * @return	a Color representing the default/initial value for textColor
	 */
	protected Color defaultTextColor() {
		return getForegroundColor();
	}
	/**
	 * Duplicates the receiver.
	 * 
	 * @return	an Object which is a duplicate of the receiver
	 */
	public synchronized Object duplicate() {
		try { 
		    return clone();
		} catch (CloneNotSupportedException e) { 
		    // this shouldn't happen, since we are Cloneable
		    throw new InternalError();
		}
	}
	/**
	 * Duplicates the receiver in the given <code>Hashtable</code>.
	 * 
	 * @param duplicates the Hashtable to put the new duplicate in
	 * @return	an Object which is a duplicate of the receiver
	 */
	public synchronized Object duplicateIn(Hashtable duplicates) {
		if (duplicates.containsKey(this))
			return duplicates.get(this);
		else {
			Object dup = this.duplicate();
			duplicates.put(this,dup);
			return dup;
		}
	}
	/**
	 * Answer this style's background Color.
	 * 
	 * @return	the Color to use for the background
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	/**
	 * Answer this style's fill Color.
	 * 
	 * @return	the Color to use for filling.
	 */
	public Color getFillColor() {
		return fillColor;
	}
	/** 
	 * Answer this style's font.
	 * 
	 * @return	the font to use.
	 */
	public Font getFont()  {
		return font;
	}
	/**
	 * Answer this style's foreground Color.
	 * 
	 * @return	the Color to use for the foreground.
	 */
	public Color getForegroundColor() {
		return foregroundColor;
	}
	/**
	 * Answer this style's highlight Color.
	 * 
	 * @return	the Color to use for the highlight.
	 */
	public Color getHighlightColor()  {
		if (highlightColor == null)
			return defaultHighlightColor();
		return highlightColor;
	}
	/**
	 * Answer this style's line Color.
	 * 
	 * @return	the Color to use for the line.
	 */
	public Color getLineColor() {
		if (lineColor == null)
			return defaultLineColor();
		return lineColor;
	}
	/**
	 * Answer this style's selection background Color.
	 * 
	 * @return	the Color to use for the selection's background
	 */
	public Color getSelectionBackgroundColor()  {
		if (selectionBackgroundColor == null)
			return defaultSelectionBackgroundColor();
		return selectionBackgroundColor;
	}
	/**
	 * Answer this style's selection foreground Color.
	 * 
	 * @return	the Color to use for the selection's foreground.
	 */
	public Color getSelectionForegroundColor() {
		if (selectionForegroundColor == null)
			return defaultSelectionForegroundColor();
		return selectionForegroundColor;
	}
	/**
	 * Answer this style's text Color.
	 * 
	 * @return	the Color to use for the text.
	 */
	public Color getTextColor() {
		if (textColor == null)
			return defaultTextColor();
		return textColor;
	}
	/**
	 * After a series of objects are duplicated, this can be sent to each of the
	 * duplicates to resolve any changes it might like to reconcile.  
	 * For example, replacing observers with their duplicates, if available.
	 * 
	 * @param duplicates a Hashtable with originals as keys and duplicats as elements
	 */
	public void postDuplicate(Hashtable duplicates) {
	}
	/**
	 * Set this style's background Color.
	 * 
	 * @param color the Color to use for the background.
	 */
	public void setBackgroundColor(Color color) {
		backgroundColor = color;
	}
	/**
	 * Set this style's fill Color.
	 * 
	 * @param color the Color to use for filling.
	 */
	public void setFillColor(Color color) {
		fillColor = color;
	}
	/** 
	 * Set this style's font.
	 * 
	 * @param newFont the font to use.
	 */
	public void setFont(Font newFont)  {
		font = newFont;
	}
	/**
	 * Set this style's foreground Color.
	 * 
	 * @param color the Color to use for the foreground.
	 */
	public void setForegroundColor(Color color) {
		foregroundColor = color;
	}
	/**
	 * Set this style's highlight Color.
	 * 
	 * @param color the Color to use for the highlight.
	 */
	public void setHighlightColor(Color color) {
		highlightColor = color;
	}
	/**
	 * Set this style's line Color.
	 * 
	 * @param color the Color to use for the line.
	 */
	public void setLineColor(Color color) {
		lineColor = color;
	}
	/**
	 * Set this style's selection background Color.
	 * 
	 * @param color the Color to use for the selection's background.
	 */
	public void setSelectionBackgroundColor(Color color) {
		selectionBackgroundColor = color;
	}
	/**
	 * Set this style's selection foreground Color.
	 * 
	 * @param color the Color to use for the selection's foreground.
	 */
	public void setSelectionForegroundColor(Color color) {
		selectionForegroundColor = color;
	}
	/**
	 * Set this style's text Color.
	 * 
	 * @param color the Color to use for the text.
	 */
	public void setTextColor(Color color) {
		textColor = color;
	}
}

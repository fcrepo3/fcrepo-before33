package com.rolemodelsoft.drawlet.util;

/**
 * @(#)StringRenderer.java
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
import java.awt.*;

/**
 * This interface defines a generic interface for objects that can measure
 * and display strings.  It is expected that implementers will determine
 * what to do with special characters, wrapping, etc.
 * This should be used when wanting a bit more intelligence than 
 * FontMetrics.stringWidth() and Graphics.drawString() offer.
 * In addition to some basic protocols, a variety of flags are offered which
 * may somehow be associated with special characters to give implementers
 * help in determining what to do when those characters are discovered and
 * offer implementers the opportunity to avoid hard coding based on particular
 * characters in a "standard" way.
 *
 * @version 	1.1.6, 12/30/98
 */
public interface StringRenderer {

	/**
	 * Encourage new lines after these characters if later non-special 
	 * characters would otherwise end up past some maximum x coordinate.
	 */
	public static int BREAK_AFTER = 1;

	/**
	 * Encourage new lines before these characters if later non-special 
	 * characters would otherwise end up past some maximum x coordinate.
	 */
	public static int BREAK_BEFORE = 2;

	/**
	 * Encourage new lines if later non-special 
	 * characters would otherwise end up past some maximum x coordinate.
	 */
	public static int BREAK = BREAK_AFTER | BREAK_BEFORE;

	/**
	 * Ignore (with respect to wrapping) these characters at the end 
	 * of lines and avoid using them at the beginning of a line unless 
	 * preceded by a NEW_LINE.
	 */
	public static int END_ABSORB = 4;

	/**
	 * These characters are not displayed based on what the font says, 
	 * but rather some sort of special handling.
		 */
	public static int NO_FONT = 8;

	/**
	 * These characters force the character which immediately follows 
	 * them to appear on a new line.
	 */
	public static int NEW_LINE = 16;

	/**
	 * These characters force some manual vertical movement more 
	 * complicated than a simple NEW_LINE.
	 */
	public static int VERTICAL_MOVE = 32;

	/**
	 * These characters force some vertical movement.
	 */
	public static int VERTICAL = NEW_LINE | VERTICAL_MOVE;

	/**
	 * These characters force some vertical movement.
	 */
	public static int END_DELAY = END_ABSORB | BREAK_BEFORE;
	/**
	 * Answer the font the receiver is using.
	 *
	 * @return the Font the receiver is using.
	 */
	public Font getFont();
	/** 
	 * Answers an array of the substrings, one for each line.
	 * Don't strip off any ending white space.
	 * NOTE: This will probably be changed to return an Enumeration or Iterator.
	 *
	 * @returns an array of Strings.
	 */
	public String[] getRawStringLines();
	/**
	 * Answer the String the receiver is associated with.
	 *
	 * @returns the String the receiver is associated with.
	 */
	public String getString();
	/** 
	 * Answer the height of the composed string.
	 *
	 * @returns an integer representing the height of the composed string.
	 */
	public int getStringHeight();
	/** 
	 * Answers an array of the substrings, one for each line.
	 * NOTE: This will probably be changed to return an Enumeration or Iterator.
	 *
	 * @returns an array of Strings.
	 */
	public String[] getStringLines();
	/** 
	 * Answer the width of the composed string.
	 *
	 * @returns an integer representing the width of the composed string.
	 */
	public int getStringWidth();
	/** 
	 * Paints the string.
	 *
	 * @param g the specified Graphics window.
	 */
	public void paint(Graphics g);
	/** 
	 * Paints the string starting at the top left specified.
	 *
	 * @param g the specified Graphics window.
	 * @param x the x coordinate to display the leftmost point.
	 * @param y the y coordinate to display the topmost point.
	 */
	public void paint(Graphics g, int x, int y);
	/**
	 * Set the receiver up to compose and display based on the given font.
	 *
	 * @param font the font to use as a base.
	 */
	public void setFont(Font font);
	/**
	 * Set the receiver up to display the string.
	 *
	 * @param string the string to use as a base.
	 */
	public void setString(String newString);
}

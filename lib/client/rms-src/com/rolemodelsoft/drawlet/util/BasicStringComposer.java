package com.rolemodelsoft.drawlet.util;

/**
 * @(#)BasicStringComposer.java
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
 * This provides basic default functionality for StringRenderers that provide
 * wrapping.  The first version doesn't handle all possibilities, but works for
 * most purposes.
 * NOTE: it is possible that individual line widths could be greater than the 
 * maxWidth as widths include calculation of ending whitespace in case someone
 * wants to do something with them.  However, no non-END_ABSORB characters 
 * should show up past the maxWidth.
 *
 * @version 	1.1.6, 12/30/98
 */
public class BasicStringComposer extends BasicStringRenderer {
	/**
	 * The maximum width to allow non-END_ABSORBing (whitespace) characters
	 * before wrapping.
	 */
	protected int maxWidth = defaultMaxWidth();

	/**
	 * The maximum height to compose. ALL_VERTICAL means continue to end
	 * NOTE: If this is set to something other than the default no characters beyond
	 * those needed to get to that height will be taken into consideration.
	 */
	protected int maxHeight = defaultMaxHeight();

	/**
	 * This keeps track of the last known x coordinate that we know to
	 * to be good (i.e. not past the maxWidth) for the last character we
	 * know we can display on the current line being composed.
	 */
	protected int goodWidth;

	/** 
	 * Answer an instance prepared to render a String.
	 *
	 * @param string the string to render
	 */
	public BasicStringComposer(String string) {
		super(string);
	}
	/** 
	 * Answer an instance prepared to render a string,
	 * wrapping after a given width.
	 *
	 * @param string the string to render
	 * @param maxWidth the width at which to wrap text to a new line
	 */
	public BasicStringComposer(String string, int maxWidth) {
		this(string);
		this.maxWidth = maxWidth;
	}
	/** 
	 * Answer an instance prepared to render a string,
	 * wrapping after a given width, and ignoring text past a given height.
	 *
	 * @param string the string to render
	 * @param maxWidth the width at which to wrap text to a new line
	 * @param maxHeight the height at which to ignore other text
	 */
	public BasicStringComposer(String string, int maxWidth, int maxHeight) {
		this(string, maxWidth);
		this.maxHeight = maxHeight;
	}
	/** 
	 * Answer an instance prepared to render a string using a particular font
	 *
	 * @param string the string to render
	 * @param font the font to use when rendering
	 */
	public BasicStringComposer(String string, Font font) {
		super(string, font);
	}
	/** 
	 * Answer an instance prepared to render a string using a particular font,
	 * wrapping after a given width.
	 *
	 * @param string the string to render
	 * @param font the font to use when rendering
	 * @param maxWidth the width at which to wrap text to a new line
	 */
	public BasicStringComposer(String string, Font font, int maxWidth) {
		this(string, font);
		this.maxWidth = maxWidth;
	}
	/** 
	 * Answer an instance prepared to render a string using a particular font,
	 * wrapping after a given width, and ignoring text past a given height.
	 *
	 * @param string the string to render
	 * @param font the font to use when rendering
	 * @param maxWidth the width at which to wrap text to a new line
	 * @param maxHeight the height at which to ignore other text
	 */
	public BasicStringComposer(String string, Font font, int maxWidth, int maxHeight) {
		this(string, font, maxWidth);
		this.maxHeight = maxHeight;
	}
	/** 
	 * Record the current settings as the end of a line.
	 * Leave the running counters alone so others may use them before preparing
	 * to start the next line.
	 * Used during composition.
	 * @see beginNextLine
	 */
	protected void addLine() {
		// everything else below should be same as super unless otherwise noted
		int newEnds[] = new int[ends.length + 1];
		int newBegins[] = new int[begins.length + 1];
		int newManuals[][] = new int[manuals.length + 1][];
		int newWidths[] = new int[widths.length + 1];
		System.arraycopy(ends, 0, newEnds, 0, ends.length);
		System.arraycopy(begins, 0, newBegins, 0, begins.length);
		System.arraycopy(manuals, 0, newManuals, 0, manuals.length);
		System.arraycopy(widths, 0, newWidths, 0, widths.length);
		newEnds[ends.length] = end;
		//this is the only line that is different
		newWidths[ends.length] = goodWidth;
		ends = newEnds;
		begins = newBegins;
		manuals = newManuals;
		widths = newWidths;
		height += getLineHeight();
	}
	/** 
	 * Set up to compose the next line.
	 * Reset the running counters and buffers which may be needed.
	 * Used during composition.  Often after addLine().
	 * @see addLine()
	 */
	protected void beginNextLine() {
		super.beginNextLine();
		goodWidth = width;
	}
	/** 
	 * Make the last/pending line into a "real one".
	 */
	protected void closeLastLine() {
		if (end == 0)
			addLine();
		else if (end == last && !isFullyComposed()) {
			// if the last character is a BREAK_BEFORE,
			// include its width
			if ((getSpecialFlags(string, end - 1) & BREAK_BEFORE) != 0) {
				adjustWidthForSpecial(string, end - 1);
				goodWidth = width;
			}
			newLine();
		}
	}
	/** 
	 * Compose the text in such a way as to produce all lines necessary
	 * to display text properly.
	 */
	protected void compose() {
		compose(maxHeight);
	}
	/** 
	 * Compose the text in such a way as to produce the lines necessary
	 * to display text between the given vertical coordinates.
	 *
	 * @param yBegin the vertical point at which we need to begin composing.
	 * @param yEnd the vertical point at which we'll stop composing.
	 */
	protected void compose(int yBegin, int yEnd) {
		composeVerticalArea(yBegin, yEnd);
		closeLastLine();
	}
	/** 
	 * Compose the text in such a way as to produce the lines necessary
	 * to display text between the given vertical coordinates.  However, 
	 * don't assume that the last character scanned is the end of a real line.
	 * It is just the end of the current line as known so far.
	 *
	 * @param yBegin the vertical point at which we need to begin composing.
	 * @param yEnd the vertical point at which we'll stop composing.
	 */
	protected void composeVerticalArea(int yBegin, int yEnd) {
		/*
		 * since we are wrapping there is no way to predict exactly what goes
		 * on the first lines, so just keep composing until we've reached the 
		 * last line needed.
		 */
		boolean breakBefore = false;
		while (end < last && (yEnd == ALL_VERTICAL || height < yEnd)) {
			if (breakBefore) //ended on BREAK_BEFORE... add width now before continuing
				adjustWidthForSpecial(string, end - 1);
			int special = scanToSpecial(string, end, last);
			if (width > maxWidth) {
				if (breakBefore) {
					end = end - 1;
					breakBefore = false;
				}
				if (end == begin) {
					/*
					 * by the first special character, we're too wide.
					 * Recursively back up from the last character 
					 * scanned and start over, making sure at least 
					 * one character gets on the line.
					 */
					int newLast = (special == NO_INDEX) ? last - 1 : special;
					if (newLast <= begin) {
						end = begin + 1;
						goodWidth = width;
						next = end;
						newLine();
					} else {
						int oldLast = last;
						width = 0;
						last = newLast;
						composeVerticalArea(yBegin, yEnd);
						last = oldLast;
					}
				} else {
					/*
					 * Since we are now past the end of a legal line,
					 * the previous scan should be the end of a line.
					 * Make it so.  The next time through the loop,
					 * We will rescan what we just scanned
					 * as the beginning of the next line.
					 */
					next = end;
					newLine();
				}
			} else {
				breakBefore = false;
				if (special == NO_INDEX) {
					goodWidth = width;
					end = last;
				} else {
					boolean newLine = handleSpecial(string, special);
					goodWidth = width;
					if (newLine)
						newLine();
					else {
						if ((getSpecialFlags(string, special) & BREAK_BEFORE) != 0)
							breakBefore = true;
						end = special + 1;
					}
				}
			}
		}
	}
	/**
	 * Answer the default/initial value for maxHeight.
	 *
	 * @return an integer representing the default/initial maxHeight.
	 */
	protected int defaultMaxHeight() {
		return ALL_VERTICAL;
	}
	/**
	 * Answer the default/initial value for maxWidth.
	 *
	 * @return an integer representing the default/initial maxWidth.
	 */
	protected int defaultMaxWidth() {
		return Integer.MAX_VALUE;
	}
	/**
	 * Answer the default/initial value for the array where characters can be marked as special.
	 * By default, we assume ASCII.  Subclasses can easily override this but may wish to address special
	 * characters in a more compact way (perhaps as a different implementer of the base interface).
	 *
	 * @return an array of ints.
	 */
	protected int[] defaultSpecials() {
		int specialChars[] = new int[256];
		specialChars['\n'] = NEW_LINE | NO_FONT;
		specialChars['\r'] = NEW_LINE | NO_FONT;
		specialChars['\f'] = NEW_LINE | NO_FONT;
		specialChars['\t'] = NO_FONT | END_ABSORB | BREAK_AFTER;
		specialChars[' '] = END_ABSORB | BREAK_AFTER;
		//	specialChars['\b'] = ???;
		specialChars['-'] = BREAK_AFTER;
		specialChars['!'] = BREAK_AFTER;
		specialChars['@'] = BREAK_AFTER;
		specialChars['%'] = BREAK_AFTER;
		specialChars['&'] = BREAK_AFTER;
		specialChars['*'] = BREAK_AFTER;
		specialChars['_'] = BREAK_AFTER;
		specialChars['+'] = BREAK_AFTER;
		specialChars['='] = BREAK_AFTER;
		specialChars['|'] = BREAK_AFTER;
		specialChars['}'] = BREAK_AFTER;
		specialChars[']'] = BREAK_AFTER;
		specialChars[')'] = BREAK_AFTER;
		specialChars[':'] = BREAK_AFTER;
		specialChars[';'] = BREAK_AFTER;
		specialChars['?'] = BREAK_AFTER;
		specialChars['/'] = BREAK_AFTER;
		specialChars['\\'] = BREAK_AFTER;
		specialChars['>'] = BREAK_AFTER;
		specialChars[','] = BREAK_AFTER;
		specialChars['.'] = BREAK_AFTER;
		specialChars['~'] = BREAK_BEFORE;
		specialChars['#'] = BREAK_BEFORE;
		specialChars['$'] = BREAK_BEFORE;
		specialChars['^'] = BREAK_BEFORE;
		specialChars['<'] = BREAK_BEFORE;
		specialChars['('] = BREAK_BEFORE;
		specialChars['['] = BREAK_BEFORE;
		specialChars['{'] = BREAK_BEFORE;
		return specialChars;
	}
	/** 
	 * Handles the special character at index during composition.  
	 * Answer true if a new line is necessary.
	 *
	 * @param string the string in which to find the special character
	 * @param index index of special character
	 * @return boolean whether or not a new line is necessary.
	 */
	protected boolean handleSpecial(String string, int index) {
		if (index == NO_INDEX)
			return true;
		int flags = getSpecialFlags(string, index);
		int oldWidth = width;
		if ((flags & END_DELAY) != 0)
			adjustWidthForSpecial(string, index);
		if ((flags & NEW_LINE) != 0) {
			end = index;
			next = index + 1;
			return true;
		} else {
			// Assumes END_ABSORB will also be a BREAK, if not we could end up with problems?
			if (((flags & BREAK) != 0) && (width > maxWidth)) {
				if ((flags & BREAK_BEFORE) != 0) {
					width = oldWidth;
					end = index;
				} else
					// BREAK_AFTER ???
					end = index + 1;
				next = end;
				return true;
			}
		}
		// Someday need to handle VERTICAL_MOVE ???
		
		if ((flags & BREAK_BEFORE) != 0)
			width = oldWidth;
		if ((flags & NO_FONT) != 0)
			handleManually(index);
		return false;
	}
	/** 
	 * Answer whether the string has been fully composed.
	 *
	 * @return a boolean value of true if the line is fully composed; false otherwise.
	 */
	protected boolean isFullyComposed() {
		if ((maxHeight != ALL_VERTICAL) && (maxHeight <= height))
			return true;
		return super.isFullyComposed();
	}
	/** 
	 * Paints the string.
	 *
	 * @param g the specified Graphics window.
	 */
	public void paint(Graphics g) {
		verifyMetrics(g);
		
		/*
		 * Determine what lines we really need to display
		 */
		int minY, maxY;
		Rectangle clip = g.getClipBounds();
		minY = startY;
		maxY = (maxHeight == -1) ? -1 : startY + getLineHeight() + maxHeight;
		if (clip.height != -1) {
			minY = Math.max(minY, clip.y - getLineHeight());
			int maxClipY = clip.y + clip.height + getLineHeight();
			maxY = (maxHeight == -1) ? maxClipY : Math.min(maxClipY, maxY);
		}
		
		paintTextBetween(g, minY, maxY);
	}
	/**
	 * Returns the String representation of the receiver's values.
	 *
	 * @return a String representing the receiver's values.
	 */
	public String toString() {
		return getClass().getName() + "[begin=" + begin + ",end=" + end + ",next=" + next + ",last=" + last + ",goodWidth=" + goodWidth + ",width=" + width + ",height=" + height + ",lines=" + ends.length + "]";
	}
}

package com.rolemodelsoft.drawlet.util;

/**
 * @(#)BasicStringRenderer.java
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
 * This provides basic default functionality for StringRenderer.
 * It doesn't do any wrapping.  However, it handles tabs and new-lines and
 * provides hooks for a lot of subclasses which might want to handle more
 * complicated StringRendering.
 * NOTE: If a font is not supplied when constructed, it will be determined
 * by default or by the current font the first time it is asked to paint,
 * whichever comes first.
 *
 * There are basically two main responsibilities of this class:
 *	1. compose the characters of a string into lines and information about those lines
 *    that will be meaningful before, during, and after displaying them.
 *  2. paint the parts of the composed string that should be visible when given a
 *    given area and medium on which to display.
 *
 * These two responsibilities are intertwined for efficiency.  Nothing will be composed
 * before it is necessary to either paint or respond to some query.  It is very possible that
 * only a small portion of the string will ever be composed, e.g. if only asked to paint the first
 * few lines of a long string with no other action that would change the viewing area to display more.
 * On the other hand, just because something wasn't ever completely viewed, doesn't mean it will
 * not be totally composed (e.g. if asked for width, all lines must be composed in order to figure
 * out what line is widest).  Subclasses may provide for constraints that will make various scenarios
 * more or less efficient as they change the amount of services dependent on composition. 
 *
 * @version 	1.1.6, 12/30/98
 */
public class BasicStringRenderer implements StringRenderer {
	protected String string;
	/**
	 * NOTE: We use lazy initialization for metrics to avoid problems of
	 *  1. changing metrics mid-composition (as line height etc. is affected by the metrics),
	 *  2. performance problems of calculating default when one was not explicitly set.
	 *  3. delaying identification of metrics (and resetting of all composition-related attributes)
	 *		until composition actually begins.
	 */
	protected FontMetrics metrics;

	/**
	 * This determines the starting x location to begin rendering the string;
	 */
	protected int startX = defaultStartX();

	/**
	 * This determines the starting y location to begin rendering the string;
	 */
	protected int startY = defaultStartY();
	
	/**
	 * These hold information necessary to determine the beginning, end, and width of each line
	 * along with which indexes of characters within that line need manual intervention when displaying
	 * (e.g. tabs are usually not displayed as characters, but rather an amount of space determined based
	 * on current placement must be determined... other special characters must also need special handling).
	 * All integers in these arrays (except widths) indicate indexes of characters within the entire string;
	 */
	protected int begins[];
	protected int ends[];
	protected int widths[];
	protected int manuals[][]; // first dimension: line number; second dimension: array of 0..N index of specials.
	
	/**
	 * Provides a map of character values to types of special handling (see static attributes of StringRenderer)
	 * which are bitORed together.
	 */
	protected int specials[] = defaultSpecials();

	/**
	 * The following are all used to keep track of running counters when composing lines.
	 * NOTE: "end", "next", "last" are often manipulated to a point that clouds their literal meaning.
	 * For example, "end" (of line) is set to be the last character of the string until it is determined that
	 * this assumption is invalid.
	 */
	protected int begin, end, next, last, width, height;

	/**
	 * Symbolic name for result of not finding an index when searched.
	 */
	protected static int NO_INDEX = -1;
	
	/**
	 * Symbolic name for identifying all vertical space to be composed or painted.
	 */
	protected static int ALL_VERTICAL = -1;
 
/** 
 * Answer an instance prepared to render a String.
 *
 * @param string the string to render
 */
public BasicStringRenderer(String string) {
	this.setString(string);
}
	/** 
	 * Answer an instance prepared to render the given string using a particular font.
	 *
	 * @param string the string to render.
	 * @param font the font to use when rendering.
	 */
	public BasicStringRenderer(String string, Font font) {
		this.string = string;
		this.setFont(font);
	}
	/** 
	 * Adjusts the theoretical end of the line to absorb whitespace in order to speed
	 * up display of the lines later.  Width of characters are still accounted for.
	 */
	protected void absorbEnd() {
		// peek back for other dead end space
		int back = end - 1;
		while (begin < back && ((getSpecialFlags(string, back) & END_ABSORB) != 0)) {
			end = back;
			back = end - 1;
		}
		// peek ahead for other dead end space that does not initiate vertical movement
		if (((getSpecialFlags(string, next-1)) & VERTICAL) == 0) {
			while ((next < last) && (((getSpecialFlags(string, next)) & (VERTICAL | END_ABSORB)) == END_ABSORB)) {
				adjustWidthForSpecial(string, next);
				next = next + 1;
			}
		}
	}
	/** 
	 * Record the current settings as the end of a line.
	 * Leave the running counters alone so others may use them before preparing
	 * to start the next line.
	 * Used during composition.
	 *
	 * @see beginNextLine
	 */
	protected void addLine() {
		int newEnds[] = new int[ends.length + 1];
		int newBegins[] = new int[begins.length + 1];
		int newManuals[][] = new int[manuals.length + 1][];
		int newWidths[] = new int[widths.length + 1];
		System.arraycopy(ends, 0, newEnds, 0, ends.length);
		System.arraycopy(begins, 0, newBegins, 0, begins.length);
		System.arraycopy(manuals, 0, newManuals, 0, manuals.length);
		System.arraycopy(widths, 0, newWidths, 0, widths.length);
		newEnds[ends.length] = end;
		newWidths[ends.length] = width;
		ends = newEnds;
		begins = newBegins;
		manuals = newManuals;
		widths = newWidths;
		height += getLineHeight();
	}
	/** 
	 * Adjust the width to account for the special character at the specified index.
	 * Used during composition.
	 *
	 * @param string string to check
	 * @param index the index of the special character
	 */
	protected void adjustWidthForSpecial(String string, int index) {
		if (index == NO_INDEX)
			return;
		char c = string.charAt(index);
		int flags = getSpecialFlags(c);
		if ((flags & NO_FONT) == 0) {
			width += metrics.charWidth(c);
			return;
		}
		switch (c) {
			case '\t' :
				width = nextTabStop(width);
				break;
			default : // what's left, new lines?? if so, do nothing
		}
	}
	/** 
	 * Answer whether all of the characters in the string between start (inclusive) and stop (exclusive)
	 * are END_ABSORBed characters.
	 *
	 * @param start an integer representing the starting index, inclusive.
	 * @param stop an integer representing the stopping index, exclusive.
	 * @return boolean
	 */
	protected boolean areAllAbsorbed(int start, int stop) {
		for (int i=start; i < stop; i++) {
			if ((getSpecialFlags(string,i) & END_ABSORB) == 0)
				return false;
		}
		return true;
	}
	/** 
	 * Set up to compose the next line.
	 * Reset the running counters and buffers which may be needed.
	 * Used during composition.  Often after addLine().
	 *
	 * @see addLine()
	 */
	protected void beginNextLine() {
		begin = next;
		end = begin;
		begins[begins.length - 1] = begin;
		manuals[manuals.length - 1] = new int[0];
		width = 0;
		next = last;
	}
	/** 
	 * Make the last/pending line into a "real one" if indicated.
	 * This will be only when the string is empty.
	 */
	protected void closeLastLine() {
		if (end == last && last == 0 && !isFullyComposed())
			addLine();
	}
	/** 
	 * Compose the text in such a way as to produce all lines necessary
	 * to display text properly.
	 */
	protected void compose() {
		compose(ALL_VERTICAL);
	}
	/** 
	 * Compose the text in such a way as to produce the lines necessary
	 * to display text up to the given vertical coordinate.
	 *
	 * @param ySize the vertical coordinate at which we'll stop composing.
	 */
	protected void compose(int ySize) {
		compose(0, ySize);
	}
	/** 
	 * Compose the text in such a way as to produce the lines necessary
	 * to display text between the given vertical coordinates.
	 *
	 * @param yBegin the vertical coordinate at which we need to begin composing.
	 * @param yEnd the vertical coordinate at which we'll stop composing.
	 */
	protected void compose(int yBegin, int yEnd) {
		while (end < last && (yEnd == ALL_VERTICAL || height < yEnd)) {
			int special = scanToSpecial(string, end, last);
			if (special == NO_INDEX)
				end = last;
			else {
				if (handleSpecial(string, special))
					newLine();
				else
					end = special + 1;
			}
			if (end == last)  // this will close the last line
				newLine();
		}
		closeLastLine();  // this is really only relevant in the special case of an empty string
	}
	/** 
	 * Assume something has changed that makes the current composition bogus.
	 * Do it again.
	 */
	protected void composeAll() {
		reset();
		compose();
	}
	/** 
	 * If no composing has happened yet, make it happen.
	 * If something has changed since the last time the string was composed,
	 * do it again.
	 * No matter what, make sure the entire string is composed.
	 */
	protected void composeIfNecessary() {
		if (!isFullyComposed())
			compose();
	}
	/**
	 * Answer the default/initial value for the Font.
	 *
	 * @return the default/initial value for the Font.
	 */
	protected Font defaultFont() {
		String fontName = Toolkit.getDefaultToolkit().getFontList()[0];
		return new Font(fontName, Font.PLAIN, 12);
	}
	/**
	 * Answer the default/initial value for metrics.
	 *
	 * @return the default/initial value for metrics.
	 */
	protected FontMetrics defaultMetrics() {
		return Toolkit.getDefaultToolkit().getFontMetrics(defaultFont());
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
		specialChars['\t'] = NO_FONT | END_ABSORB;
		specialChars[' '] = END_ABSORB;
		return specialChars;
	}
	/**
	 * Answer the default/initial value for the x coordinate.
	 *
	 * @return the default/initial value for the x coordinate.
	 */
	protected int defaultStartX() {
		return 0;
	}
	/**
	 * Answer the default/initial value for the y coordinate.
	 *
	 * @return the default/initial value for the y coordinate.
	 */
	protected int defaultStartY() {
		return 0;
	}
	/** 
	 * Answer the baseline (coordinates in y direction from top) to display text.
	 * NOTE: Assumes metrics have been set.
	 * Used during painting.
	 *
	 * @return an integer representing the baseline from which to display text.
	 */
	protected int getBaseline() {
		return metrics.getMaxAscent();
	}
	/**
	 * Answer the Font the receiver is using.
	 *
	 * @return the Font the receiver is using.
	 */
	public Font getFont() {
		if (metrics == null)
			return defaultFont();
		return metrics.getFont();
	}
	/** 
	 * Answer the height of a line.
	 * Used during composition.
	 *
	 * @return an integer representing the height of a line.
	 */
	protected int getLineHeight() {
		return getMetrics().getHeight();
	}
	/**
	 * Answer the font metrics the receiver is using.
	 *
	 * @return the FontMetrics the receiver is using.
	 */
	protected FontMetrics getMetrics() {
		if (metrics == null)
			metrics = defaultMetrics();
		return metrics;
	}
	/** 
	 * Answers an array of the substrings, one for each line.
	 * Don't strip off any ending white space.
	 * NOTE: This will probably be changed to return an Enumeration or Iterator.
	 *
	 * @return an array of Strings.
	 */
	public String[] getRawStringLines() {
		composeIfNecessary();
		String array[] = new String[ends.length];
		for (int i = 0; i < array.length; i++)
			array[i] = string.substring(begins[i], begins[i + 1]);
		return array;
	}
	/** 
	 * Answer the flags associated with a particular character.
	 *
	 * @param c the character.
	 * @return the flags associated with the given character, encoded in an integer.
	 */
	protected int getSpecialFlags(char c) {
		return specials[c];
	}
	/** 
	 * Answer the flags associated with a particular character.
	 *
	 * @param string string to check.
	 * @param index point to look at.
	 * @return the flags associated with the given character, encoded in an integer.
	 */
	protected int getSpecialFlags(String string, int index) {
		return specials[string.charAt(index)];
	}
	/**
	 * Answer the String associated with the receiver.
	 *
	 * @return the String associated with the receiver.
	 */
	public String getString() {
		return string;
	}
	/** 
	 * Answer the height of the composed string.
	 *
	 * @return an integer representing the height of the composed string.
	 */
	public int getStringHeight() {
		composeIfNecessary();
		return height;
	}
	/** 
	 * Answers an array of the substrings, one for each line.
	 * This will strip off any ending characters identified as END_ABSORB.
	 * NOTE: This will probably be changed to return an Enumeration or Iterator.
	 *
	 * @return an array of Strings.
	 */
	public String[] getStringLines() {
		composeIfNecessary();
		String array[] = new String[ends.length];
		for (int i = 0; i < array.length; i++)
			array[i] = string.substring(begins[i], ends[i]);
		return array;
	}
	/** 
	 * Answer the width of the composed string.
	 *
	 * @return an integer representing the width of the composed string.
	 */
	public int getStringWidth() {
		composeIfNecessary();
		int max = 0;
		for (int i = 0; i < widths.length; i++)
			max = Math.max(max, widths[i]);
		return max;
	}
	/** 
	 * Flags the special character at index as one to display manually.
	 * Used during composition.  
	 * Assumes the special character is part of current/last line composed.
	 *
	 * @param index index of special character to flag.
	 */
	protected void handleManually(int index) {
		int previous[] = manuals[manuals.length - 1];
		int current[] = new int[previous.length + 1];
		System.arraycopy(previous, 0, current, 0, previous.length);
		current[previous.length] = index;
		manuals[manuals.length - 1] = current;
	}
	/** 
	 * Handles the special character at index during composition.  
	 * Answer true if a new line is necessary.
	 *
	 * @param string the string in which to find the special character.
	 * @param index index of special character.
	 * @return boolean whether or not a new line is necessary.
	 */
	protected boolean handleSpecial(String string, int index) {
		if (index == NO_INDEX) // end of string
			return true;
		int flags = getSpecialFlags(string, index);
		if ((flags & END_DELAY) != 0)
			adjustWidthForSpecial(string, index);
		if ((flags & NEW_LINE) != 0) {
			end = index;
			next = index + 1;
			return true;
		}
		if ((flags & NO_FONT) != 0)
			handleManually(index);
		return false;
	}
	/** 
	 * Answers the index of the first special character in the specified range which is not
	 * END_ABSORB only, or -1 if none.
	 * Used during composition.
	 *
	 * @param string string to check.
	 * @param start starting point to look.
	 * @param ending point to look.
	 * @return an integer representing the index of the first special character.
	 */
	protected int indexOfSpecial(String string, int start, int stop) {
		for (int i = start; i < stop; i++) {
			int flags = getSpecialFlags(string, i);
			if ((flags != 0) && (flags != END_ABSORB))
				return i;
		}
		return NO_INDEX;
	}
	/** 
	 * Answer whether the string has been fully composed.
	 *
	 * @return a boolean value of true if the string is fully composed; false otherwise.
	 */
	protected boolean isFullyComposed() {
		int numberOfLines = ends.length;
		/*
		 * The following is an attempt to verify that we've composed everything... 
		 * it seems too complicated, but other alternatives I can come up with right now
		 * seem to offer their own ugliness, so we'll live with this for now.
		 * Here is a description of the logic below:
		 *  Even an empty string should take up one line after being composed... no lines, not composed.
		 *  If the last character of last line is last character of string, we've composed everything.
		 *  If not, we might be in a situation where the last characters of the string are absorbed, i.e. not
		 *     worth displaying.  If this is the case with the last line, we've composed everything
		 */
		return ((numberOfLines != 0) &&
			(ends[numberOfLines - 1] == string.length() || // 
				(begins[numberOfLines] == string.length() && (areAllAbsorbed(ends[numberOfLines - 1],string.length())))));
	}
	/** 
	 * Record the current settings as the end of a line, 
	 * and set up to scan the next line.
	 * Used during composition.
	 */
	protected void newLine() {
		absorbEnd();
		addLine();
		beginNextLine();
	}
	/**
	 * Answer the next tab stop after the x position.
	 *
	 * @param x the x position from which to tab.
	 * @return an integer representing the next tab stop.
	 */
	protected int nextTabStop(int x) {
		int tabSpace = 40;
		return ((x / tabSpace) + 1) * tabSpace;
	}
	/** 
	 * Paints the string.
	 *
	 * @param g the specified Graphics window
	 */
	public void paint(Graphics g) {
		verifyMetrics(g);
		/*
		 * Determine what lines we really need to display
		 */
		int minY, maxY;
		Rectangle clip = g.getClipBounds();
		if (clip == null || clip.height == -1) {
			minY = startY;
			maxY = ALL_VERTICAL;
		} else {
			minY = clip.y - getLineHeight();
			maxY = clip.y + clip.height + getLineHeight();
		}
		
		paintTextBetween(g, minY, maxY);
	}
	/** 
	 * Paints the string starting at the top left specified.
	 *
	 * @param g the specified Graphics window.
	 * @param x the X coordinate at which to start.
	 * @param y the Y coordinate at which to start.
	 */
	public void paint(Graphics g, int x, int y) {
		startX = x;
		startY = y;
		paint(g);
	}
	/** 
	 * Paints the special character at the specified index at the appropriate
	 * place.  Answer the new x coordinate.
	 *
	 * @param g the specified Graphics window.
	 * @param index the index of the special character.
	 * @param x the x coordinate.
	 * @param y the y coordinate.
	 * @returns an integer representing the new x coordinate after acting on the special character.
	 */
	protected int paintSpecial(Graphics g, int index, int x, int y) {
		// for now, we only handle tabs
		if (string.charAt(index) != '\t')
			throw new IllegalArgumentException("Only tabs are supported for manual display");
		// just move the x coordinate, we don't need to display anything
		int currentWidth = x - startX;
		int nextTab = nextTabStop(currentWidth);
		return startX + nextTab;
	}
	/** 
	 * Paints the string between the given horizontal window.
	 *
	 * @param g the specified Graphics window.
	 * @param topY int identifying the top of the area in which we would like to paint.
	 * @param bottomY int identifying the bottom of the area in which we would like to paint.
	 */
	protected void paintTextBetween(Graphics g, int topY, int bottomY) {
		/*
		 * Make sure the lines we need to display are composed.
		 * For now, assume that if the metrics are set, 
		 * we are using the right font.
		 */
		compose(topY - startY, (bottomY == -1) ? -1 : bottomY - startY);
		/*
		 * Display the lines of text
		 */
		int x = startX;
		int y = startY + getBaseline();
		int maxY = (bottomY == -1) ? Integer.MAX_VALUE : bottomY;
		for (int i = 0; y < maxY && i < ends.length; i++) {
			if (y > topY) {
				int special[] = manuals[i];
				if (special.length == 0)
					g.drawString(string.substring(begins[i], ends[i]), x, y);
				else {
					int startIndex = begins[i];
					int x2 = x;
					int stopIndex;
					for (int j = 0; j < special.length; j++) {
						stopIndex = special[j];
						if (stopIndex > startIndex) {
							String sub = string.substring(startIndex, stopIndex);
							g.drawString(sub, x2, y);
							x2 += metrics.stringWidth(sub);
						}
						x2 = paintSpecial(g, stopIndex, x2, y);
						startIndex = stopIndex + 1;
					}
					stopIndex = ends[i];
					if (stopIndex > startIndex)
						g.drawString(string.substring(startIndex, stopIndex), x2, y);
				}
			}
			y += getLineHeight();
		}
	}
	/**
	 * Reset the receiver to start composing.
	 */
	protected void reset() {
		if (ends == null || (ends.length != 0)) {
			ends = new int[0];
			begins = new int[1];
			manuals = new int[1][0];
			widths = new int[0];
			height = 0;
		}
		// the following are all used to compose lines
		next = 0;
		last = string.length();
		beginNextLine();
	}
	/** 
	 * Scan the characters from start to stop, adding to the width until
	 * a special character is found.
	 * Answers the index of the first special character, or NO_INDEX if none.
	 *
	 * @param string string to check
	 * @param start starting point to look, inclusive
	 * @param stop stopping point to look, exclusive
	 * @return an integer representing the first special character.
	 */
	protected int scanToSpecial(String string, int start, int stop) {
		int special = indexOfSpecial(string, start, stop);
		int actualStop = special;
		if (special == -1) {
			special = NO_INDEX;
			actualStop = stop;
		}
		if (actualStop > start)
			width += getMetrics().stringWidth(string.substring(start, actualStop));
		if (special == NO_INDEX)
			return special;
		/*
		 * delay adjusting the width for special characters that won't get put
		 * at the end of the line until we've determined if we are there or not
		 */
		int flags = getSpecialFlags(string, special);
		if ((flags & END_DELAY) == 0)
			adjustWidthForSpecial(string, special);
		return special;
	}
	/**
	 * Set the receiver up to compose and display based on the given font.
	 *
	 * @param font the font to use as a base.
	 */
	public void setFont(Font font) {
		if (font != getFont()) {
			metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
			reset();
		}
	}
	/**
	 * Set the receiver up to display the string.
	 *
	 * @param string the string to use as a base.
	 */
	public void setString(String newString) {
		if (string != newString) {
			string = newString;
			reset();
		}
	}
	/**
	 * Returns the String representation of the receiver's values.
	 *
	 * @return a String representation of the receiver's values.
	 */
	public String toString() {
		return getClass().getName() + "[begin=" + begin + ",end=" + end + ",next=" + next + ",last=" + last + ",width=" + width + ",height=" + height + ",lines=" + ends.length + "]";
	}
	/** 
	 * Verify that the metrics used to compose the text is 
	 * consistent with the font/metrics about to be used to render
	 * the string.
	 *
	 * @param g the specified Graphics window.
	 */
	protected void verifyMetrics(Graphics g) {
		/*
		 * Make sure we are using the right font.  If we are, assume the metrics are the same.
		 */
		if (metrics == null || (getFont().equals(g.getFontMetrics().getFont()))) {
			metrics = g.getFontMetrics();
			reset();
		}
	}
} 

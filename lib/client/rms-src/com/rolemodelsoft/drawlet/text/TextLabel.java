package com.rolemodelsoft.drawlet.text;

/**
 * @(#)TextLabel.java
 *
 * Copyright (c) 1998-2001 RoleModel Software, Inc. (RMS). All Rights Reserved.
 * Copyright (c) 1996-1997 Knowledge Systems Corporation (KSC). All Rights Reserved.
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
import com.rolemodelsoft.drawlet.util.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 * This provides a basic implementation of a figure which displays simple text.
 * It is assumed that this figure may be connected to another figure
 * via its locator, but it can be used as just raw text also.
 *
 * @version 	1.1.6, 12/29/98
 */
 
public class TextLabel extends AbstractFigure implements LabelHolder, RelatedLocationListener {
	static final long serialVersionUID = 2853476878632285634L;
	
	/**
	 * The string which serves as the label.
	 */
	protected String string = defaultString();

	/**
	 * The font with which to paint the label.
	 */
	protected Font font = defaultFont();

	/**
	 * The color with which to paint the label.
	 */
	protected Color textColor = defaultTextColor();

	/**
	 * The location of the label, the topLeft of where the string shows up.
	 */
	protected MovableLocator locator = defaultLocator();

	/**
	 * The renderer used to actually display the label.
	 */
	protected transient StringRenderer renderer;

	/**
	 * Since ints are not objects and zero could be valid for any cache, 
	 * we need to come up with "unset" values for each.  For width and 
	 * height, -1 would be fine, but those might be valid for top/left,
	 * as could basically any other integer.  However, Integer.MIN_VALUE 
	 * seems to be as unlikely as anything to be practical.  So, we use it.
	 */
	protected static int unset = Integer.MIN_VALUE;

	/**
	 * Used to cache the width value to calc bounds faster.
	 */
	protected int width = unset;

	/**
	 * Used to cache the height value to calc bounds faster.
	 */
	protected int height = unset;

	/**
	 * Used to cache the left value to calc bounds faster.
	 */
	protected int left = unset;

	/**
	 * Used to cache the top value to calc bounds faster.
	 */
	protected int top = unset;

	/** 
	 * Constructs a new instance of a label.
	 */
	public TextLabel() {
	}
	/** 
	 * Constructs and initializes a new instance of a label with the given Color.
	 *
	 * @param color the color with which to display the label
	 */
	public TextLabel(Color color) {
		setTextColor(color);
	}
	/** 
	 * Constructs and initializes a new instance of a label with the given Font.
	 *
	 * @param font the font with which to display the label.
	 */
	public TextLabel(Font font) {
		setFont(font);
	}
	/** 
	 * Constructs and initializes a new instance of a label with
	 * the given Font and Color.
	 *
	 * @param font the Font with which to display the label.
	 * @param color the Color with which to display the label.
	 */
	public TextLabel(Font font, Color color) {
		this(font);
		setTextColor(color);
	}
	/** 
	 * Constructs and initializes a new instance of a label with the
	 * given String.
	 *
	 * @param string the String to display as the label.
	 */
	public TextLabel(String string) {
		setString(string);
	}
	/** 
	 * Constructs and initializes a new instance of a label with the given
	 * String and Font.
	 *
	 * @param string the String to display as the label.
	 * @param font the Font with which to display the label.
	 */
	public TextLabel(String string, Font font) {
		this(string);
		setFont(font);
	}
	/** 
	 * Constructs and initializes a new instance of a label with the given
	 * String, Font, and Color.
	 *
	 * @param string the String to display as the label.
	 * @param font the Font with which to display the label.
	 * @param color the Color with which to display the label.
	 */
	public TextLabel(String string, Font font, Color color) {
		this(string, font);
		setTextColor(color);
	}
	/** 
	 * Moves the receiver to a new location.
	 *
	 * @param newLocator the Locator to base the figure's position
	 * @see #getLocator
	 * @see #move
	 */
	protected synchronized void basicMove(Locator newLocator) {
		Figure oldFigure = figureFromLocator(locator);
		Figure newFigure = figureFromLocator(newLocator);
		if (oldFigure != newFigure) {
			if (oldFigure != null)
				oldFigure.removeRelatedLocationListener(this);
			if (newFigure != null)
				newFigure.addRelatedLocationListener(this);
		}
		if (newLocator instanceof MovableLocator)
			locator = (MovableLocator) newLocator;
		else
			locator = new RelativePoint(newLocator, 0, 0);
	}
	/** 
	 * Moves the receiver in the x and y direction.
	 * Subclasses should probably provide synchronized versions if they're 
	 * modifying attributes of the receiver.
	 *
	 * @param x amount to move in the x direction
	 * @param y amount to move in the y direction
	 * @see #getLocator
	 */
	protected void basicTranslate(int x, int y)  {
		locator.translate(x,y);
	}
	/**
	 * Answer the default/initial value for the Font.
	 *
	 * @return the default/initial Font.
	 */
	protected Font defaultFont()  {
		// it would be nice if there was a simple way to get the system font
		Font newFont; // = Font.getFont("font.default");
		//	if (newFont == null)
		newFont = new Font("TimesRoman",Font.PLAIN,12);
		return newFont;
	}
	/**
	 * Answer the default/initial value for the Locator.
	 *
	 * @return the default/initial MovableLocator.
	 */
	protected MovableLocator defaultLocator() {
		return new DrawingPoint(0,0);
	}
	/**
	 * Answer the default/initial value for the String.
	 *
	 * @return the default/initial String.
	 */
	protected String defaultString() {
		return "label";
	}
	/**
	 * Answer the default/initial value for the color to paint the text.
	 *
	 * @return the default/initial Color for painting text.
	 */
	protected Color defaultTextColor() {
		return Color.black;
	}
	/**
	 * Duplicates the receiver and places the duplicate in the given Hashtable.
	 *
	 * @param duplicates the Hashtable to put the duplicate in.
	 * @return the duplicate.
	 */
	public synchronized Object duplicateIn(Hashtable duplicates) {
		TextLabel myDuplicate = (TextLabel)super.duplicateIn(duplicates);
		myDuplicate.locator = (MovableLocator)locator.duplicateIn(duplicates);
		myDuplicate.renderer = null;
		myDuplicate.resetBoundsCache();
		return myDuplicate;
	}
	/** 
	 * Answers a Handle that will provide editing capabilities on the receiver.
	 *
	 * @param x the x coordinate to potentially begin editing.
	 * @param y the y coordinate to potentially begin editing.
	 */
	public Handle editTool(int x, int y)  {
		return new LabelEditHandle(this);
	}
	/**
	 * Remove any dependence on the Figure.
	 *
	 * @param figure the Figure to disassociate.
	 */
	protected synchronized void freeFromFigure(Figure figure)  {
		if (figureFromLocator(locator) == figure) 
			locator = new DrawingPoint(locator.x(),locator.y());
	}
	/** 
	 * Returns the current bounds of the receiver.
	 *
	 * @return a Rectangle representing the current bounds.
	 */
	public Rectangle getBounds()  {
		return new Rectangle(getLeft(), getTop(), getWidth(), getHeight());
	}
	/** 
	 * Answer the Font with which the receiver paints.
	 *
	 * @return the Font in use.
	 */
	public Font getFont()  {
		return font;
	}
	/** 
	 * Answer the Handles associated with the receiver.
	 *
	 * @return an array of Handles.
	 */
	public Handle[] getHandles() {
		Handle handles[] = new Handle[1];
		handles[0] = new LocatorConnectionHandle(this);
		return handles;
	}
	/** 
	 * Returns the height of the receiver.
	 *
	 * @return an integer representing the height of the receiver.
	 */
	public int getHeight() {
		if (height == unset)
			height = getRenderer().getStringHeight();
		return height;
	}
	/** 
	 * Returns the current bounds of the label.
	 *
	 * @return an integer representing the bounds of the label.
	 */
	public Rectangle getLabelBounds()  {
		return new Rectangle(getLabelLeft(), getLabelTop(), getLabelWidth(), getLabelHeight());
	}
	/** 
	 * Returns the height of the label of this figure.
	 *
	 * @return an integer representing the height of the label.
	 */
	protected int getLabelHeight() {
		return getHeight();
	}
	/** 
	 * Returns the leftmost coordinate of the label of this figure.
	 *
	 * @return an integer representing the leftmost coordinate of the label.
	 */
	protected int getLabelLeft() {
		return getLeft() + xMargin();
	}
	/** 
	 * Returns the topmost coordinate of the label of this figure.
	 *
	 * @return an integer representing the topmost coordinate of the label.
	 */
	protected int getLabelTop() {
		return getTop();
	}
	/** 
	 * Returns the width of the label of this figure.
	 *
	 * @return an integer representing the width of the label.
	 */
	protected int getLabelWidth() {
		return getWidth() - (2 * xMargin());
	}
	/** 
	 * Returns the leftmost coordinate of the receiver.
	 *
	 * @return an integer representing the leftmost coordinate of the receiver.
	 */
	public int getLeft() {
		if (left == unset)
			left = locator.x() - xMargin();
		return left;
	}
	/** 
	 * Returns the current locator of the receiver.
	 * Answer a duplicate for security.
	 *
	 * @return a duplicate of the current Locator of the receiver.
	 */
	public Locator getLocator() {
		return (Locator)locator.duplicate();
	}
	/**
	 * Answer the renderer to use to display the label.
	 *
	 * @return the StringRenderer used to display the label.
	 */
	protected StringRenderer getRenderer() {
		if (renderer == null) {
			renderer = new BasicStringRenderer(getString(), getFont());
		}
		return renderer;
	}
	/** 
	 * Answer the String the receiver paints.
	 *
	 * @return the String the receiver paints.
	 */
	public String getString()  {
		return string;
	}
	/** 
	 * Answer the DrawingStyle which defines how to paint the receiver.
	 *
	 * @return the DrawingStyle defining how to paint the receiver.
	 */
	public DrawingStyle getStyle()  {
		DrawingStyle style = super.getStyle();
		style.setTextColor(getTextColor());
		style.setFont(getFont());
		return style;
	}
	/**
	 * Answer the Color to use when drawing text.
	 *
	 * @return the Color to use when drawing text.
	 */
	public Color getTextColor() {
		return textColor;
	}
	/** 
	 * Returns the topmost coordinate of the receiver.
	 *
	 * @return an integer representing the topmost coordinate of the receiver.
	 */
	public int getTop() {
		if (top == unset)
			top = locator.y();
		return top;
	}
	/** 
	 * Returns the width of the receiver.
	 *
	 * @return an integer representing the width of the receiver.
	 */
	public int getWidth() {
		if (width == unset)
			width = getRenderer().getStringWidth() + (2 * xMargin());
		return width;
	}
	/** 
	 * Answers whether the receiver is listening to the figure directly 
	 * or indirectly (via chain of listeners).
	 *
	 * @param figure the Figure to test.
	 * @return a boolean value of true if we are listening; false otherwise.
	 */
	protected boolean isListening(Figure figure) {
		for (Enumeration e = figure.relatedLocationListeners(); e.hasMoreElements();) {
			RelatedLocationListener listener = (RelatedLocationListener) e.nextElement();
			if (listener == this)
				return true;
			else
				if (listener instanceof Figure)
					if (isListening((Figure) listener))
						return true;
		}
		return false;
	}
	/** 
	 * Answers whether the receiver is obsolete
	 * True if some event has happened that makes this a meaningless object.
	 *
	 * @return boolean value of true if the receiver is obselete; false otherwise.
	 */
	public boolean isObsolete() {
		return string.length() == 0;
	}
	/**
	 * Called when the location of something the receiver is listening to has changed.
	 *
	 * @param event the event describing the change.
	 */
	public void locationChanged(PropertyChangeEvent event) {
		updateLocation();
	}
	/** 
	 * Moves the receiver to a new location.
	 * This is a TemplateMethod with hooks:
	 * 	resetLocationCache()
	 * 	basicMove()
	 *  changedLocation()
	 *
	 * @param locator the Locator which identifies the desired x, y coordinates.
	 * @see #getLocator
	 * @see #basicMove
	 */
	public void move(Locator locator) {
		Point oldLocation = getLocation();
		resetLocationCache();
		basicMove(locator);
		changedLocation(oldLocation);
	}
	/** 
	 * Paints the receiver.
	 *
	 * @param g the specified Graphics window.
	 */
	public void paint(Graphics g)  {
		g.setColor(textColor);
		g.setFont(font);
		getRenderer().paint(g, getLabelLeft(), getLabelTop());
	}
	/**
	 * After a series of Figures are duplicated, this can be sent to each of the
	 * duplicates to resolve any changes it might like to reconcile.
	 * 
	 * In this case, remove any dependency on any figures defining original 
	 * points.  If there is an available duplicate corresponding to the original, 
	 * use it as the original was used.  If not, convert it to a non-Figure-
	 * dependent point.  Copy remaining points as they are.
	 *
	 * @param duplicates a Hashtable with originals as keys and duplicates as elements.
	 */
	public void postDuplicate(Hashtable duplicates) {
		super.postDuplicate(duplicates);
		Figure myFigure = figureFromLocator(locator);
		if (myFigure != null) {
			if (!duplicates.containsKey(myFigure))
				locator = new DrawingPoint(locator.x(),locator.y());
		}
		locator.postDuplicate(duplicates);
	}
	/**
	 * Called when the relation of something the receiver is listening to has changed.
	 *
	 * @param event the event describing the change.
	 */
	public void relationChanged(PropertyChangeEvent event) {
		freeFromFigure((Figure)event.getSource());
	}
	/** 
	 * Answers a Locator corresponding to a significant point on the receiver 
	 * that will serve as a connection to the requesting Figure.
	 *
	 * @param x the x coordinate of the requested locator
	 * @param y the y coordinate of the requested locator
	 */
	public Locator requestConnection(Figure requestor, int x, int y) {
		// make sure we aren't already connected to the locator 
		// which is trying to connect to us 
		if (isListening(requestor))
			return null;
		return locatorAt(x,y);
	}
	/**
	 * Flush caches with respect to determining bounds. 
	 */
	protected void resetBoundsCache() {
		resetLocationCache();
		resetSizeCache();
	}
	/**
	 * Flush caches with respect to determining location
	 */
	protected void resetLocationCache() {
		left = unset;
		top = unset;
	}
	/**
	 * Flush caches with respect to determining size
	 */
	protected void resetSizeCache() {
		width = unset;
		height = unset;
	}
	/** 
	 * Set the Font with which to paint text
	 * This is a TemplateMethod with hooks:
	 * 	resetBoundsCache()
	 *  changedShape()
	 *
	 * @param newFont the Font to use for text.
	 */
	public void setFont(Font newFont)  {
		Rectangle oldBounds = getBounds();
		resetBoundsCache();
		font = newFont;
		getRenderer().setFont(font);
		changedShape(oldBounds);
	}
	/** 
	 * Set the string the figure paints.
	 * This is a TemplateMethod with hooks:
	 * 	resetSizeCache()
	 *  changedSize()
	 *
	 * @param newString the string to paint.
	 */
	public void setString(String newString)  {
		Dimension oldSize = getSize();
		resetSizeCache();
		string = newString;
		getRenderer().setString(string);
		changedSize(oldSize);
	}
	/** 
	 * Set the style defining how to paint the figure.
	 *
	 * @param style the specified DrawingStyle.
	 */
	public void setStyle(DrawingStyle style) {
		DrawingStyle oldStyle = getStyle();
		if (style != null) {
			setFont(style.getFont());
			setTextColor(style.getTextColor());
		}
		firePropertyChange(STYLE_PROPERTY, oldStyle, style);
	}
	/**
	 * Set the Color to use when drawing text.
	 *
	 * @param color the Color.
	 */
	public void setTextColor(Color color) {
		Color oldColor = textColor;
		textColor = color;
		firePropertyChange(TEXT_COLOR_PROPERTY, oldColor, color);
	}
	/**
	 * Called when the shape of something the receiver is listening to has changed.
	 *
	 * @param event the event describing the change.
	 */
	public void shapeChanged(PropertyChangeEvent event) {
		updateLocation();
	}
	/**
	 * Called when the size of something the receiver is listening to has changed.
	 *
	 * @param event the event describing the change.
	 */
	public void sizeChanged(PropertyChangeEvent event) {
		updateLocation();
	}
	/**
	 * The Figure has notified the receiver of a change.
	 * Assume our location has changed due to some movement/reshaping
	 * of the Figure.
	 */
	protected void updateLocation() {
		Point oldLocation = getLocation();
		resetLocationCache();
		changedLocation(oldLocation);
	}
	/**
	 * Answer the margin to use in the x direction.
	 *
	 * @return an integer representing the margin to use in the x direction.
	 */
	protected int xMargin()  {
		return 3;
	}
}

/*
 * @(#) JHSecondaryViewer.java 1.20 - last change made 05/29/01
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package com.sun.java.help.impl;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Dialog;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Font;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.border.EmptyBorder;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;
import javax.help.*;
import javax.help.Map.ID;
import java.awt.event.*;
import java.awt.Dimension;
import java.awt.Insets;
import java.net.*;
import java.util.Hashtable;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.awt.Cursor;
/**
 * This class is a secondary viewer to be included in HTML content within
 * JHContentViewer. The ViewerType can either be a SecondaryWindow or a Popup.
 * Activation is done be either a Button or a mouse enabled Label. Both Button
 * and Label support Text, Icon, or Text and Icon.
 * <p>
 * To use this class within HTML content use the &ltobject&gt tag. Below is an
 * example usage:
 * <p><pre>
 * &ltobject CLASSID="java:com.sun.java.help.impl.JHSecondaryViewer"&gt
 * &ltparam name="content" value="secondary_contents.html"&gt
 * &ltparam name="viewerActivator" value="javax.help.LinkLabel"&gt
 * &ltparam name="viewerStyle" value="javax.help.Popup"&gt
 * &ltparam name="viewerSize" value="300,400"&gt
 * &ltparam name="text" value="Click here"&gt
 * &ltparam name="textFontFamily" value="SansSerif"&gt
 * &ltparam name="textFontSize" value="x-large"&gt
 * &ltparam name="textFontWeight" value="plain"&gt
 * &ltparam name="textFontStyle" value="italic"&gt
 * &ltparam name="textColor" value="red"&gt
 * &lt/object&gt
 * </pre><p>
 * Valid parameters are:
 * <ul>
 * <li>content - a valid url, can be relative to the current viewer
 * @see setContent
 * <li>id - a valid id from the current HelpSet
 * @see setId
 * <li>viewerName - the name of the SecondaryWindow to display the content in
 * @see setViewerName
 * <li>viewerActivator - the activator type for the viewer.
 * Either "javax.help.LinkButton" or "javax.help.LinkLabel"
 * @see setViewerActivator
 * <li>viewerStyle - the style of the viewer. Either "SecondaryWindow" or "Popup"
 * @see setViewerStyle
 * <li>viewerLocation - the x,y coordinates applied to a secondary window.
 * @see setViewerLocation
 * <li>viewerSize - the width, height applied to a viewer.
 * @see setViewerSize
 * <li>iconByName - the url or id of a icon to be displayed in the activator.
 * The url is relative to the base address of the document
 * @see setIconByName
 * <li>text - the text of the activator
 * @see setText
 * <li>textFontFamily - the font family of the activator text
 * @see setTextFontFamily
 * <li>textFontSize - the size of the activator text font. Size is specified
 * in a css termonolgy. See the setTextFontSize for acceptable syntax
 * @see setTextFontSize
 * <li>textFontWeight - the activator text font weight
 * @see setTextFontWeight
 * <li>textFontStyle - the activator text font style
 * @see setTextFontStyle
 * <li>textColor - the activator text color
 * @see setTextColor
 * <ul>
 * @see JHSecondaryWindowBeanInfo
 *
 * @author Roger D. Brinkley
 * @version	1.20	05/29/01
 */
public class JHSecondaryViewer extends JButton implements ActionListener, ViewAwareComponent, PropertyChangeListener {
    
    private View myView;
    private HelpSet hs;
    private SimpleAttributeSet textAttribs;
    private URL base;
    private HTMLDocument doc;

    static public String POPUP = "javax.help.Popup";
    static public String SECONDARY_WINDOW = "javax.help.SecondaryWindow";
    static public String LINK_BUTTON = "javax.help.LinkButton";
    static public String LINK_LABEL = "javax.help.LinkLabel";
    private final static String buttonPropertyPrefix = "Button" + ".";
    private final static String editorPropertyPrefix = "EditorPane" + ".";
    private final static Cursor handCursor =
	Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private Cursor origCursor;

    /**
     * Create a secondaryviewer. By default the viewer creates a button with
     * the text of ">"
     */
    public JHSecondaryViewer() {
	super();
	setText(">");
	setMargin(new Insets(0,0,0,0));
	createLinkButton();
	addActionListener(this);
	origCursor = getCursor();
	addMouseListener(new MouseListener() {
	    public void mouseClicked(MouseEvent e) {
	    }

	    public void mouseEntered(MouseEvent e) {
		setCursor(handCursor);
	    }

	    public void mouseExited(MouseEvent e) {
		setCursor(origCursor);
	    }

	    public void mousePressed(MouseEvent e) {
	    }

	    public void mouseReleased(MouseEvent e) {
	    }
	});
    }

    /**
     * Sets data optained from the View
     */
    public void setViewData(View v) {
	myView = v;
	doc = (HTMLDocument) myView.getDocument();
	base = doc.getBase();

	// Set the current font information in the local text attributes
	Font font = getFont();
	textAttribs = new SimpleAttributeSet();
	textAttribs.removeAttribute(StyleConstants.FontSize);
	textAttribs.removeAttribute(StyleConstants.Bold);
	textAttribs.removeAttribute(StyleConstants.Italic);
	textAttribs.addAttribute(StyleConstants.FontFamily,
				 font.getName());
	textAttribs.addAttribute(StyleConstants.FontSize,
				 new Integer(font.getSize()));
	textAttribs.addAttribute(StyleConstants.Bold,
				 new Boolean(font.isBold()));
	textAttribs.addAttribute(StyleConstants.Italic,
				 new Boolean(font.isItalic()));


	// Loop through and find the JHelpContentViewer
	Component c = container = (Component) myView.getContainer();
	while (c != null) {
	    if (c instanceof JHelpContentViewer) {
		break;
	    }
	    c = c.getParent();
	}

	// Get the helpset if there was JHelpContentViewer
	if (c !=null) {
	    TextHelpModel thm = ((JHelpContentViewer)c).getModel();
	    if (thm != null) {
		hs = thm.getHelpSet();
	    }
	}
    }

    static private SecondaryView popupView;
    static private Component container;
    static private Hashtable secondaryWins = new Hashtable();

    class SecondaryView {

	public JFrame frame;
	public JPopupMenu popup;
	public JEditorPane editor;
	public JHelpContentViewer jheditor;
	public String name;

	public SecondaryView (JFrame frame, JPopupMenu popup, 
			      JEditorPane editor, 
			      JHelpContentViewer jheditor,
			      String name) {
	    this.frame = frame;
	    this.popup = popup;
	    this.editor = editor;
	    this.jheditor = jheditor;
	    this.name = name;
	}

    }

    /**
     *  properties
     */
    private int viewerHeight = 0;
    private int viewerWidth = 0;
    private int viewerX = 0;
    private int viewerY = 0;
    private String viewerName = "";
    private int viewerActivator = 0;
    private int viewerStyle = 0;
    private Icon viewerIcon;
    private String content = "";
    private Map.ID ident;

    /**
     * Set the content for the secondary viewer
     * @param content a valid URL
     */
    public void setContent(String content) {
	this.content = content;
	ident = null;
    }

    /**
     * Returns the content of the secondary viewer
     */
    public String getContent() {

	// return the URL of the ident if set
	if (ident != null) {
	    Map map = hs.getCombinedMap();
	    try {
	    URL url = map.getURLFromID(ident);
	    return (url.toExternalForm());
	    } catch (Exception ex) {
	    }
	}

	// just return the content even if ""
	return content;
    }

    /**
     * Set the ID for content in the secondary viewer
     * @param content a valid URL
     */
    public void setId(String id) {
	ident = Map.ID.create(id, hs);
	content = "";
    }

    /**
     * Returns the ID of the secondary viewer
     */
    public String getId() {
	return ident.id;
    }

    /**
     * Sets the viewer name to display the content in.
     * Viewer is only valid for ViewerStyle "SecondaryWindow"
     */
    public void setViewerName(String name) {
	viewerName = name;
    }

    /**
     * Returns the viewer name
     */
    public String getViewerName() {
	return viewerName;
    }

    /**
     * Sets the viewer activator.
     * Valid activators are
     * <ul>
     * <li>javax.help.LinkButton
     * <li>javax.help.LinkLabel
     * </ul>
     * The new activatory type will be used the next time a view is displayed.
     */
    public void setViewerActivator(String activator) {
	if (activator.compareTo(LINK_BUTTON) == 0 &&
	    viewerActivator != 0) {
	    viewerActivator = 0;
	    createLinkButton();
	} else if (activator.compareTo(LINK_LABEL) == 0 &&
		      viewerActivator != 1) {
	    viewerActivator = 1;
	    createLinkLabel();
	}
    }

    /**
     * Returns the viewer activator
     */
    public String getViewerActivator() {
	switch (viewerActivator) {
	case 0:
	    return LINK_BUTTON;
	case 1:
	    return LINK_LABEL;
	}
	return "unknownStyle";
    }

    /**
     * Creates a link button. This is just a JButton with default settings.
     */
    private void createLinkButton() {
	LookAndFeel.installBorder(this, buttonPropertyPrefix + "border");
	setBorderPainted(true);
	setFocusPainted(true);
	setAlignmentY(Container.CENTER_ALIGNMENT);
	setContentAreaFilled(true);
	setBackground(UIManager.getColor(buttonPropertyPrefix + "background"));
	if (textAttribs != null && 
	    textAttribs.isDefined(StyleConstants.Foreground)) {
	    setForeground((Color)textAttribs.getAttribute(StyleConstants.Foreground));
	} else {
	    setForeground(UIManager.getColor(buttonPropertyPrefix + "foreground"));
	}
	invalidate();
    }
	
    /**
     * Creates a link label. A link label is a form of a JButton but without a
     * button like appearance.
     */
    private void createLinkLabel() {
	setBorder(new EmptyBorder(1,1,1,1));
	setBorderPainted(false);
	setFocusPainted(false);
	setAlignmentY(getPreferredLabelAlignment());
	setContentAreaFilled(false);
	setBackground(UIManager.getColor(editorPropertyPrefix + "background"));
	if (textAttribs != null &&
	    textAttribs.isDefined(StyleConstants.Foreground)) {
	    setForeground((Color)textAttribs.getAttribute(StyleConstants.Foreground));
	} else {
	    setForeground(Color.blue);
	}
	invalidate();
    }

    /**
     * Determine the alignment offset so the text is aligned with other views
     * correctly.
     */
    private float getPreferredLabelAlignment() {
        Icon icon = (Icon)getIcon();
        String text = getText();

        Font font = getFont();
        FontMetrics fm = getToolkit().getFontMetrics(font);
          
        Rectangle iconR = new Rectangle();
        Rectangle textR = new Rectangle();
        Rectangle viewR = new Rectangle(Short.MAX_VALUE, Short.MAX_VALUE);

        SwingUtilities.layoutCompoundLabel(
            this, fm, text, icon,
            getVerticalAlignment(), getHorizontalAlignment(),
            getVerticalTextPosition(), getHorizontalTextPosition(),
            viewR, iconR, textR,
	    (text == null ? 0 : ((BasicButtonUI)ui).getDefaultTextIconGap(this))
        );

        /* The preferred size of the button is the size of 
         * the text and icon rectangles plus the buttons insets.
         */
        Rectangle r = iconR.union(textR);

        Insets insets = getInsets();
        r.height += insets.top + insets.bottom;

        /* Ensure that the height of the button is odd,
         * to allow for the focus line.
         */
        if(r.height % 2 == 0) { 
	    r.height += 1;
	}

	float offAmt = fm.getMaxAscent() + insets.top;
	return offAmt/(float)r.height;
    }
	
    /**
     * Sets the viewer style. There are two valid viewer styles:
     * <ul>
     * <li>javax.help.SecondaryWindow
     * <li>javax.help.Popup
     * <ul>
     * <p>
     * Viewer style is updated the next time the viewer is made visible
     * @param style a valid ViewerStyle
     */
    public void setViewerStyle(String style) {
	if (style.compareTo(SECONDARY_WINDOW) == 0) {
	    viewerStyle = 0;
	} else if (style.compareTo(POPUP) == 0) {
	    viewerStyle = 1;
	}
    }

    /**
     * Returns the current ViewerStyle
     */
    public String getViewerStyle() {
	switch (viewerStyle) {
	case 0:
	    return SECONDARY_WINDOW;
	case 1:
	    return POPUP;
	}
	return "unknownStyle";
    }

    /**
     * Sets the viewer's location to display the content in.
     * Location is relative to the screen or a modal dialog box
     * The String must be in the form of "x,y". If no viewer location
     * is established the default is 0,0 for secondary windows. Location
     * is ignored for popups.
     * @see getViewerLocation
     */
    public void setViewerLocation(String location) {
	int comma = location.indexOf(",");
	if (comma != -1) {
	    String x = location.substring(0,comma).trim();
	    String y = location.substring(comma+1).trim();
	    if (x != null && y != null) {
		viewerX = Integer.parseInt(x);
		viewerY = Integer.parseInt(y);
	    }
	}
    }

    /**
     * Returns the viewer Location. Location is relative to the screen or
     * a modal dialog box. The form of the location is "x,y". 
     * The default location is 0,0.
     * @see setViewerLocation
     */
    public String getViewerLocation() {
	SecondaryView view;
	String retval = Integer.toString(viewerX) + "," +
	    Integer.toString(viewerY);

	// try to return the acutal viewer locations if it exists
	switch (viewerStyle) {
	case 0:
	    view = (SecondaryView)secondaryWins.get(viewerName);
	    if (view != null) {
		retval = Integer.toString(view.frame.getLocation().x) + "," +
		    Integer.toString(view.frame.getLocation().y);
	    }
	    break;
	case 1:
	    // ignored in popups
	    break;
	}
	return retval;
    }

    /**
     * Sets the viewer's size to display the content in.
     * The String must be in the form of "width,heigt". 
     * If no size is set the default is 200,200.
     * @see getViewerSize
     */
    public void setViewerSize(String size) {
	int comma = size.indexOf(",");
	if (comma != -1) {
	    String width = size.substring(0,comma).trim();
	    String height = size.substring(comma+1).trim();
	    if (width != null && height != null) {
		viewerWidth = Integer.parseInt(width);
		viewerHeight = Integer.parseInt(height);
	    }
	}
    }

    /**
     * Returns the viewer's Size. 
     * The form of the size is "width,height". 
     * @see setViewerSize
     */
    public String getViewerSize() {
	SecondaryView view;
	String retval;
	if (viewerWidth != 0) {
	    retval = Integer.toString(viewerWidth) + "," +
		Integer.toString(viewerHeight);
	} else {
	    retval = "200,200";
	}

	// try to return the acutal viewer locations if it exists
	switch (viewerStyle) {
	case 0:
	    view = (SecondaryView)secondaryWins.get(viewerName);
	    if (view != null && view.frame.isVisible()) {
		retval = Integer.toString(view.frame.getSize().width) + "," +
		    Integer.toString(view.frame.getSize().height);
	    }
	    break;
	case 1:
	    if (popupView != null && popupView.popup.isVisible()) {
		retval = Integer.toString(popupView.popup.getSize().width) + "," +
		    Integer.toString(popupView.popup.getSize().height);
	    }
	    break;
	}
	return retval;
    }

    /**
     * Sets the icon in the activator by url or id. The url is relative to the
     * base address of the document.
     * @see getIcon
     */
    public void setIconByName(String name) {
	ImageIcon ig = null;
	URL url=null;
	// try to get it from the base address
	try {
	    url = new URL (base, name); 
	} catch (java.net.MalformedURLException ex1) {
	    return;
	}

	// Valid URLs try the Icon
	ig = new ImageIcon(url);
	if (ig != null) {
	    setIcon(ig);
	    // if the text is the default text then make it blank
	    String text = getText();
	    if (text.compareTo(">") == 0) {
		setText("");
	    }
	}
    }

    /**
     * Sets the icon in the activator by id. 
     * @see getIcon
     */
    public void setIconByID(String name) {
	ImageIcon ig = null;
	URL url=null;

	Map map = hs.getCombinedMap();
	try {
	    url = map.getURLFromID(ID.create(name, hs)); 
	} catch (java.net.MalformedURLException e2) {
	    return;
	}

	// Valid URLs try the Icon
	ig = new ImageIcon(url);
	if (ig != null) {
	    setIcon(ig);
	    // if the text is the default text then make it blank
	    String text = getText();
	    if (text.compareTo(">") == 0) {
		setText("");
	    }
	}
    }

    /**
     * Sets the text Font family for the activator text.
     * For JDK 1.1 this must a family name of Dialog, DialogInput, Monospaced, 
     * Serif, SansSerif, or Symbol.
     */
    public void setTextFontFamily(String family) {
	textAttribs.removeAttribute(StyleConstants.FontFamily);
	textAttribs.addAttribute(StyleConstants.FontFamily, family);
	setFont(getAttributeSetFont(textAttribs));
	Font font = getFont();
    }

    /**
     * Returns the text Font family name of the activator text
     */
    public String getTextFontFamily() {
	return StyleConstants.getFontFamily(textAttribs);
    }

    /**
     * Sets the text size for the activator text.
     * The String size is a valid Cascading Style Sheet value for
     * text size. Acceptable values are as follows:
     * <ul>
     * <li>xx-small
     * <li>x-small
     * <li>small
     * <li>medium
     * <li>large
     * <li>x-large
     * <li>xx-large
     * <li>bigger - increase the current base font size by 1
     * <li>smaller - decrease the current base font size by 1
     * <li>xxpt - set the font size to a specific pt value of "xx"
     * <li>+x - increase the current base font size by a value of "x"
     * <li>-x - decrease the current base font size by a value of "x"
     * <li>x - set the font size to the point size associated with 
     * the index "x"
     * </ul>
     */
    public void setTextFontSize(String size) {
	int newsize, tmp;
	StyleSheet css = doc.getStyleSheet();
	try {
	    if (size.equals("xx-small")) {
		newsize = (int)css.getPointSize(0);
	    } else if (size.equals("x-small")) {
		newsize = (int)css.getPointSize(1);
	    } else if (size.equals("small")) {
		newsize = (int)css.getPointSize(2);
	    } else if (size.equals("medium")) {
		newsize = (int)css.getPointSize(3);
	    } else if (size.equals("large")) {
		newsize = (int)css.getPointSize(4);
	    } else if (size.equals("x-large")) {
		newsize = (int)css.getPointSize(5);
	    } else if (size.equals("xx-large")) {
		newsize = (int)css.getPointSize(6);
	    } else if (size.equals("bigger")) {
		newsize = (int)css.getPointSize("+1");
	    } else if (size.equals("smaller")) {
		newsize = (int)css.getPointSize("-1");
	    } else if (size.endsWith("pt")) {
		String sz = size.substring(0, size.length() - 2);
		newsize = Integer.parseInt(sz);
	    } else {
		newsize = (int) css.getPointSize(size);
	    }
	} catch (NumberFormatException nfe) {
	    return;
	}
	if (newsize == 0) {
	    return;
	}
	textAttribs.removeAttribute(StyleConstants.FontSize);
	textAttribs.addAttribute(StyleConstants.FontSize,
				 new Integer(newsize));
	setFont(getAttributeSetFont(textAttribs));
	Font font = getFont();
    }

    /**
     * Returns the text Font family name of the activator text
     */
    public String getTextFontSize() {
	return Integer.toString(StyleConstants.getFontSize(textAttribs));
    }

    /**
     * Sets the text Font Weigth for the activator text.
     * Valid weights are
     * <ul>
     * <li>plain
     * <li>bold
     * </ul>
     */
    public void setTextFontWeight(String weight) {
	boolean isBold=false;
	if (weight.compareTo("bold") == 0) {
	    isBold = true;
	} else {
	    isBold = false;
	}
	textAttribs.removeAttribute(StyleConstants.Bold);
	textAttribs.addAttribute(StyleConstants.Bold, new Boolean(isBold));
	setFont(getAttributeSetFont(textAttribs));
	Font font = getFont();
    }

    /**
     * Returns the text Font weight of the activator text
     */
    public String getTextFontWeight() {
	if (StyleConstants.isBold(textAttribs)) {
	    return "bold";
	}
	return "plain";
    }

    /**
     * Sets the text Font Style for the activator text.
     * Valid font styles are
     * <ul>
     * <li>plain
     * <li>italic
     * </ul>
     */
    public void setTextFontStyle(String style) {
	boolean isItalic=false;
	if (style.compareTo("italic") == 0) {
	    isItalic = true;
	} else {
	    isItalic = false;
	}
	textAttribs.removeAttribute(StyleConstants.Italic);
	textAttribs.addAttribute(StyleConstants.Italic, new Boolean(isItalic));
	setFont(getAttributeSetFont(textAttribs));
	Font font = getFont();
    }

    /**
     * Returns the text Font style of the activator text
     */
    public String getTextFontStyle() {
	if (StyleConstants.isItalic(textAttribs)) {
	    return "italic";
	}
	return "plain";
    }

    /**
     * Sets the text Color for the activator text.
     * The following is a list of supported Color names
     * <ul>
     * <li>black
     * <li>blue
     * <li>cyan
     * <li>darkGray
     * <li>gray
     * <li>green
     * <li>lightGray
     * <li>magenta
     * <li>orange
     * <li>pink
     * <li>red
     * <li>white
     * <li>yellow
     * </ul>
     */
    public void setTextColor(String name) {
	Color color=null;
	if (name.compareTo("black") == 0) {
	    color = Color.black;
	} else if (name.compareTo("blue") == 0) {
	    color = Color.blue;
	} else if (name.compareTo("cyan") == 0) {
	    color = Color.cyan;
	} else if (name.compareTo("darkGray") == 0) {
	    color = Color.darkGray;
	} else if (name.compareTo("gray") == 0) {
	    color = Color.gray;
	} else if (name.compareTo("green") == 0) {
	    color = Color.green;
	} else if (name.compareTo("lightGray") == 0) {
	    color = Color.lightGray;
	} else if (name.compareTo("magenta") == 0) {
	    color = Color.magenta;
	} else if (name.compareTo("orange") == 0) {
	    color = Color.orange;
	} else if (name.compareTo("pink") == 0) {
	    color = Color.pink;
	} else if (name.compareTo("red") == 0) {
	    color = Color.red;
	} else if (name.compareTo("white") == 0) {
	    color = Color.white;
	} else if (name.compareTo("yellow") == 0) {
	    color = Color.yellow;
	}

	if (color == null) {
	    return;
	}
	textAttribs.removeAttribute(StyleConstants.Foreground);
	textAttribs.addAttribute(StyleConstants.Foreground, color);
	setForeground(color);
    }

    /**
     * Returns the text Color of the activator text
     */
    public String getTextColor() {
	Color color = getForeground();
	return color.toString();
    }

    /**
     * Gets the font from an attribute set.  This is
     * implemented to try and fetch a cached font
     * for the given AttributeSet, and if that fails 
     * the font features are resolved and the
     * font is fetched from the low-level font cache.
     * Font's are cached in the StyleSheet of a document
     *
     * @param attr the attribute set
     * @return the font
     */
    private Font getAttributeSetFont(AttributeSet attr) {
        // PENDING(prinz) add cache behavior
        int style = Font.PLAIN;
        if (StyleConstants.isBold(attr)) {
            style |= Font.BOLD;
        }
        if (StyleConstants.isItalic(attr)) {
            style |= Font.ITALIC;
        }
        String family = StyleConstants.getFontFamily(attr);
        int size = StyleConstants.getFontSize(attr);

	/**
	 * if either superscript or subscript is
	 * is set, we need to reduce the font size
	 * by 2.
	 */
	if (StyleConstants.isSuperscript(attr) ||
	    StyleConstants.isSubscript(attr)) {
	    size -= 2;
	}

	// fonts are cached in the StyleSheet so use that
        return doc.getStyleSheet().getFont(family, style, size);
    }

    public void propertyChange(PropertyChangeEvent event) {
	String changeName = event.getPropertyName();
	if (changeName.equals("page")) {
	    String title;
	    switch (viewerStyle) {
	    case 0:
		SecondaryView view =
		    (SecondaryView) secondaryWins.get(viewerName);
		if (view.jheditor != null) {
		    title = view.jheditor.getDocumentTitle();
		} else {
		    Document doc = view.editor.getDocument();
		    title = (String)doc.getProperty(Document.TitleProperty);
		}
		if (title == null) {
		    title = "";
		}
		view.frame.setTitle(title);
	    }
	}
    }

    /**
     * Set the page to be displayed
     */
    private void setPage(SecondaryView view) {
	String title = "";
	if (ident != null) {
	    if (view.jheditor != null) {
		try {
		    view.jheditor.setCurrentID(ident);
		} catch (Exception ex1) {
		}
	    } else {
		Map map = hs.getCombinedMap();
		try {
		    URL url = map.getURLFromID(ident);
		    view.editor.setPage(url);
		} catch (Exception ex) {
		}
	    }
	} else if (content != "") {
	    URL url = null;
	    try {
		url = new URL(base, content);
		if (view.jheditor != null) {
		    view.jheditor.setCurrentURL(url);
		} else {
		    view.editor.setPage(url);
		    Document doc = view.editor.getDocument();
		}
	    } catch (Exception ex) {
		System.err.println("setPage failed, ex: "+ex);
		return;
	    }
	}
    }

    /**
     * Displays the viewer according to the viewerType
     */
    public void actionPerformed(ActionEvent e) {
	JFrame frame = null;
	JEditorPane editor= null;
	JHelpContentViewer jheditor=null;
	JPopupMenu popup = null;

	//	System.err.println("clicked on me...");
	switch (viewerStyle) {
	case 0:
	    SecondaryView view = 
		(SecondaryView) secondaryWins.get(viewerName);
	    if (view == null) {
		frame = new JFrame();
		WindowListener l = new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {removeView(e.getWindow());}
		    public void windowClosed(WindowEvent e) {removeView(e.getWindow());}
		};
		frame.addWindowListener(l);

		if (hs != null) {
		    jheditor = new JHelpContentViewer(hs);
		    jheditor.addPropertyChangeListener(this);
		    frame.getContentPane().add(jheditor);
		} else {
		    editor = new JEditorPane();
		    editor.setEditable(false);
		    editor.addPropertyChangeListener(this);
		    frame.getContentPane().add(editor);
		}
		frame.pack();
		view = new SecondaryView(frame, null, editor,
					 jheditor, viewerName);
		secondaryWins.put(viewerName, view);
	    }
	    setPage(view);
	    if (viewerX != -1) {
		view.frame.setLocation(viewerX, viewerY);
	    }
	    // By default the frameSize is 200,200
	    Dimension frameSize = new Dimension(200, 200);
	    if (viewerWidth != 0) {
		frameSize.setSize(viewerWidth, viewerHeight); 
	    }
	    view.frame.setSize(frameSize);
	    view.frame.setVisible(true);
	    break;
	case 1:
	    if (popupView == null) {
		popup = new JPopupMenu();
		popup.setLayout (new BoxLayout(popup, BoxLayout.Y_AXIS));
		popup.setBorderPainted (true);
		popup.setBorder (BorderFactory.createLineBorder(Color.black));
		popup.setOpaque(false);
		popup.setDoubleBuffered(true);
		popup.registerKeyboardAction(new AbstractAction() 
					     { public void actionPerformed(ActionEvent ae) {
						 hidePopup();
					     }},
					     KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
					     JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		if (hs != null) {
		    jheditor = new JHelpContentViewer(hs);
		    jheditor.addPropertyChangeListener(this);
		    popup.add(jheditor);
		} else {
		    editor = new JEditorPane();
		    editor.setEditable(false);
		    editor.addPropertyChangeListener(this);
		    popup.add(editor);
		}
		popupView = new SecondaryView(null, popup, editor,
					      jheditor, null);
	    }
            // hold invoker of popup window if this JHSecondaryViewer has been 
            // already in popup window, bug #4463602
            if (SwingUtilities.getAncestorOfClass(JPopupMenu.class, this) != popupView.popup) {
                popupView.popup.setInvoker(this);
            }
	    setPage(popupView);
	    showPopup();
	    break;
	default:
	    System.out.println ("Unknown viewerStyle");
	}
    }

    private void removeView(Window win) {
	if (win instanceof JFrame) {
	    JFrame frame = (JFrame) win;
	    Enumeration views = secondaryWins.elements();
	    while (views.hasMoreElements()) {
		SecondaryView view = (SecondaryView) views.nextElement();
		if (view.frame == frame) {
		    // this is the element to remove
		    view.editor = null;
		    view.jheditor = null;
		    secondaryWins.remove(view.name);
		    break;
		}
	    }
	}
    }

    /**
     * show the popup
     */
    private void showPopup() {

        popupView.popup.setLightWeightPopupEnabled(true);

        // invoker of popup window
        JHSecondaryViewer invoker = this;
        if (popupView.popup.getInvoker() instanceof JHSecondaryViewer) {
            invoker = (JHSecondaryViewer)popupView.popup.getInvoker();
        }

	// for now the default size is 200,200
	// I'd like to use the preferred size of the editor(s) but that
	// isn't working correctly
        Dimension popupSize =  new Dimension((viewerWidth == 0) ? 200 : viewerWidth,
                                             (viewerHeight == 0) ? 200 : viewerHeight);

        Rectangle popupBounds = invoker.computePopupBounds(popupSize);

	// set the preferred size on popup so it will change size
	popupView.popup.setPreferredSize(popupBounds.getSize());
	Point point = popupBounds.getLocation();
	SwingUtilities.convertPointFromScreen(point, invoker);
        popupView.popup.setVisible(false);
        popupView.popup.show(invoker, point.x, point.y);
	if (popupView.jheditor != null) {
	    popupView.jheditor.requestFocus();
	} else {
	    popupView.editor.requestFocus();
	}
    }

    /**
     * hide the popup
     */
    public void hidePopup() {
	popupView.popup.setVisible(false);
        container.repaint();
    }

    protected Rectangle computePopupBounds(Dimension popupSize) {
	// Note all Points in computePopupBounds are either relative to
	// the screen. The desired boundry must fit on the screen. If it is
	// in a Modal Dialg it has to fit within the dialog

        Rectangle absBounds;
	Point p;

	// Calculate the absolute boundry. Modal Dialogs must be within the 
	// Dialog.
        boolean inModalDialog = inModalDialog();
        /** Workaround for modal dialogs. See also JPopupMenu.java **/
        if ( inModalDialog ) {
            Dialog dlg = getDialog();
            if ( dlg instanceof JDialog ) {
                JRootPane rp = ((JDialog)dlg).getRootPane();
                p = rp.getLocationOnScreen();
                absBounds = rp.getBounds();
                absBounds.x = p.x;
                absBounds.y = p.y;
            } else {
                absBounds = dlg.getBounds();
	    }
        } else {
            Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
            absBounds = new Rectangle(0, 0, scrSize.width, scrSize.height);
        }

	// Get a rectangle below and to the right and see if it fits
	p = new Point(0, this.getBounds().height);
	SwingUtilities.convertPointToScreen(p, this);
        Rectangle br = new Rectangle(p.x, p.y, 
				     popupSize.width, popupSize.height);
        if ( SwingUtilities.isRectangleContainingRectangle(absBounds, br) ) {
	    // below and to the right fits return the rectangle
            return br;
        }

	// below and right failed - try to adjust the right side to fit
	Rectangle bradjust = new Rectangle(br);
	bradjust.setLocation(br.x - ((br.x + br.width)- absBounds.width), 
			     br.y);
	if ( SwingUtilities.isRectangleContainingRectangle(absBounds, 
							   bradjust) ) {
	    // below and to the right fits return the rectangle
            return bradjust;
	} 

	// below and right adjust failed - try above right
	Rectangle ar = new Rectangle(br.x, 
				     br.y-(br.height+this.getBounds().height),
				     br.width, br.height);
	if (SwingUtilities.isRectangleContainingRectangle(absBounds, ar)) {
	    // above and to the right fits return the rectangle
            return ar;
	} 

	// above and right failed - adjust the right side to fit
	Rectangle aradjust = new Rectangle(ar);
	aradjust.setLocation(ar.x - ((ar.x + ar.width) - absBounds.width), 
			     aradjust.y);
	if ( SwingUtilities.isRectangleContainingRectangle(absBounds, 
							   aradjust) ) {
	    // below and to the right fits return the rectangle
            return aradjust;
	} 

	// above and right adjust failed - try left below
	p = new Point(this.getBounds().width, 0);
	SwingUtilities.convertPointToScreen(p, this);
	Rectangle lb = new Rectangle(p.x, p.y, 
				     popupSize.width, popupSize.height);
	if (SwingUtilities.isRectangleContainingRectangle(absBounds, lb)) {
	    // left and below fits return the rectangle
            return lb;
	} 

	// left and below failed - adjust the top side to fit
	Rectangle lbadjust = new Rectangle(lb);
	lbadjust.setLocation(lbadjust.x,
			     lb.y - ((lb.y + lb.height) - absBounds.height)); 
	if ( SwingUtilities.isRectangleContainingRectangle(absBounds,
							   lbadjust) ) {
	    // left and below adjusted fits return the rectangle
            return lbadjust;
	} 

	// left and below adjust failed - try right and below
	Rectangle rb = new Rectangle(lb.x-(lb.width+this.getBounds().width), 
				     lb.y,
				     lb.width, lb.height);
	if (SwingUtilities.isRectangleContainingRectangle(absBounds, rb)) {
	    // right and below fits return the rectangle
            return rb;
	} 

	// right and below failed - adjust the top side to fit
	Rectangle rbadjust = new Rectangle(rb);
	rbadjust.setLocation(rbadjust.x,
			     rb.y - ((rb.y + rb.height) - absBounds.height)); 
	if ( SwingUtilities.isRectangleContainingRectangle(absBounds, 
							   rbadjust) ) {
	    // right and below fits return the rectangle
            return rbadjust;
	} 

	// Bummer - tried all around the object so no try covering it up
	// Nothing fancy here upper left corner
	Rectangle cover = new Rectangle(0, 0, 
					popupSize.width, 
					popupSize.height);
	if (SwingUtilities.isRectangleContainingRectangle(absBounds, cover)) {
	    // covering up the object fits return the rectangle
            return cover;
	} 

	// Humm. The desired size is just to large. Shrink to fit.
	SwingUtilities.computeIntersection(absBounds.x,
					   absBounds.y,
					   absBounds.width,
					   absBounds.height,
					   cover);
	return cover;
    }

    private Dialog getDialog() {
        Container parent;
        for ( parent = container.getParent() ; parent != null && !(parent instanceof Dialog)
            && !(parent instanceof Window) ; parent = parent.getParent() );
        if ( parent instanceof Dialog )
            return (Dialog) parent;
        else
            return null;
    }

    private boolean inModalDialog() {
        return (getDialog() != null);
    }

    /**
     * For printf debugging.
     */
    private final static boolean debug = true;
    private static void debug(String str) {
        if (debug) {
            System.out.println("JHSecondaryWindow: " + str);
        }
    }
}

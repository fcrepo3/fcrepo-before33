/*
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
/*
 * @(#) BasicIndexCellRenderer.java 1.19 - last change made 09/23/99
 */

package javax.help.plaf.basic;

/**
 * Cell Renderer for the index UI.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version   %I     09/23/99
 */
import javax.swing.*;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.PrintStream;
import java.util.Locale;
import javax.help.HelpUtilities;
import javax.help.IndexItem;

public class BasicIndexCellRenderer extends JLabel implements TreeCellRenderer
{
    protected static final Color selColor = new Color(0, 0, 128);
    protected static final Color defaultColor = Color.white;
    protected boolean selected;

    // Colors
    protected Color textSelectionColor;
    protected Color textNonSelectionColor;
    protected Color backgroundSelectionColor;
    protected Color backgroundNonSelectionColor;
    protected Color borderSelectionColor;
    protected Color foregroundSelectionColor;
    protected Color foregroundColor;

    public BasicIndexCellRenderer()
    {
	setBorder (new EmptyBorder(0, 2, 0, 2));
        setHorizontalAlignment(JLabel.LEFT);

	setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
	setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
	setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
	setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
	setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));

    }

    /**
      * Sets the color the text is drawn with when the node is selected.
      */
    public void setTextSelectionColor(Color newColor) {
	textSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node is selected.
      */
    public Color getTextSelectionColor() {
	return textSelectionColor;
    }

    /**
      * Sets the color the text is drawn with when the node is not selected.
      */
    public void setTextNonSelectionColor(Color newColor) {
	textNonSelectionColor = newColor;
    }

    /**
      * Returns the color the text is drawn with when the node is not selected.
      */
    public Color getTextNonSelectionColor() {
	return textNonSelectionColor;
    }

    /**
      * Sets the color to use for the background if the node is selected.
      */
    public void setBackgroundSelectionColor(Color newColor) {
	backgroundSelectionColor = newColor;
    }


    /**
      * Returns the color to use for the background if the node is selected.
      */
    public Color getBackgroundSelectionColor() {
	return backgroundSelectionColor;
    }

    /**
      * Sets the background color to be used for unselected nodes.
      */
    public void setBackgroundNonSelectionColor(Color newColor) {
	backgroundNonSelectionColor = newColor;
    }

    /**
      * Returns the background color to be used for unselected nodes.
      */
    public Color getBackgroundNonSelectionColor() {
	return backgroundNonSelectionColor;
    }

    /**
      * Sets the color of the border.
      */
    public void setBorderSelectionColor(Color newColor) {
	borderSelectionColor = newColor;
    }

    /**
      * Returns the color of the border.
      */
    public Color getBorderSelectionColor() {
	return borderSelectionColor;
    }

    /**
     * Subclassed to only accept the font if it is not a FontUIResource.
     */
    public void setFont(Font font) {
	if(font instanceof FontUIResource)
	    font = null;
	super.setFont(font);
    }

    /**
     * Subclassed to only accept the color if it is not a ColorUIResource.
     */
    public void setBackground(Color color) {
	if(color instanceof ColorUIResource)
	    color = null;
	super.setBackground(color);
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus)
    {
	IndexItem item
	    = (IndexItem) ((DefaultMutableTreeNode) value).getUserObject();

	String stringValue = "";

	if (item != null) {
	    stringValue = item.getName();
	}

        setText(stringValue);
        if (sel)
            setForeground(getTextSelectionColor());
        else
            setForeground(getTextNonSelectionColor());
        selected = sel;

	// Set the locale of this if there is a lang value
	if (item != null) {
	    Locale locale = item.getLocale();
	    if (locale != null) {
		setLocale(locale);
	    }
	}

	return this;
    }

    /**
     * Paints the value. The background is filled based on the selected color.
     */
    public void paint(Graphics g)
    {
        Color bColor;
        if (selected) {
            bColor = getBackgroundSelectionColor();
        } else {
            bColor = getBackgroundNonSelectionColor();
            if (bColor == null)
                bColor = getBackground();
        }
        if (bColor != null) {
            g.setColor(bColor);
	    g.fillRect(0, 0, getWidth() - 1, getHeight());
        }
        if (selected) {
            g.setColor(getBorderSelectionColor());
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        super.paint(g);
    }

    public Dimension getPreferredSize() {
	Dimension        retDimension = super.getPreferredSize();

	if(retDimension != null)
	    retDimension = new Dimension(retDimension.width + 3,
					 retDimension.height);
	return retDimension;
    }

}


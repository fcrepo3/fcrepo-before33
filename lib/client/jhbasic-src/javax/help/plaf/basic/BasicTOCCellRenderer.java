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
 * @(#) BasicTOCCellRenderer.java 1.22 - last change made 03/19/99
 */

package javax.help.plaf.basic;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.net.URL;
import java.util.Locale;
import javax.help.TOCItem;
import javax.help.Map;
import javax.help.HelpUtilities;
import javax.help.Map.ID;

/**
 * Basic cell renderer for TOC UI.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version   1.22     03/19/99
 */

public class BasicTOCCellRenderer extends DefaultTreeCellRenderer
{
    protected Map map;

    public BasicTOCCellRenderer(Map map) {
	super();
	this.map = map;
    }

    /**
      * Configures the renderer based on the components passed in.
      * Sets the value from messaging value with toString().
      * The foreground color is set based on the selection and the icon
      * is set based on on leaf and expanded.
      */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean sel,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus)
    {
	// Note: I cant set the focus or drawsFocusBorderAroundIcon
	// variable because their private to DefaultTreeCellRenderer.
	// This means there won't be a focus indication

	String stringValue = "";

	TOCItem item
	    = (TOCItem) ((DefaultMutableTreeNode) value).getUserObject();

	if (item != null) {
	    stringValue = item.getName();
	}

	setText(stringValue);
	if (sel) {
	    setForeground(getTextSelectionColor());
	} else {
	    setForeground(getTextNonSelectionColor());
	}

	ImageIcon icon = null;
	if (item != null) {
	    ID id = item.getImageID();
	    if (id != null) {
		try {
		    URL url = map.getURLFromID(id);
		    icon = new ImageIcon(url);
		} catch (Exception e) {
		}
	    }
	}

	// Set the locale of this if there is a lang value
	if (item != null) {
	    Locale locale = item.getLocale();
	    if (locale != null) {
		setLocale(locale);
	    }
	}

	// determine which icon to display
	if (icon != null) {
	    setIcon(icon);
	} else if (leaf) {
	    setIcon(getLeafIcon());
	} else if (expanded) {
	    setIcon(getOpenIcon());
	} else {
	    setIcon(getClosedIcon());
	}
	    
	selected = sel;

	return this;
    }

}

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

package javax.help;

import javax.help.Map.ID;
import java.util.Locale;

/**
 * The base items known to both TOC and Index Navigators.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @(#)TreeItem.java 1.16 01/29/99
 */

public class TreeItem
{
    private String name;
    private ID id;
    private Locale locale;

    /**
     * Creates a TreeItem.
     *
     * @param id ID for the item. Null is a valid ID.
     * @param The lang for this item. A null is valid and indicates the default
     * locale.
     */
    public TreeItem(ID id, Locale locale){
	this.id = id;
	this.locale = locale;
    }

    /**
     * Sets the name of the item.
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Returns the name of the item.
     */
    public String getName() {
	return name;
    }
    
    /**
     * Returns the ID for the item.
     */
    public ID getID() {
	return id;
    }

    /**
     * Returns the locale for the item.
     */
    public Locale getLocale() {
	return locale;
    }

    /**
     * Returns a String used when displaying the object.
     * Used by CellRenderers.
     *
     * @see TOCCellRenderer
     */
    public String toString() {
	return (id+"("+name+")");
    }
}


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

import java.util.Vector;
import java.util.Locale;
import javax.help.Map.ID;

/**
 * A class for individual index items.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.16	03/16/99
 */

public class IndexItem extends TreeItem {
    private HelpSet hs;		// the helpset scoping this entry

    /**
     * Create an IndexItem.
     *
     * @param id ID for the item. The ID can be null.
     * @param hs A HelpSet scoping this item.
     * @param locale The locale for this item
     */
    public IndexItem(ID id, HelpSet hs, Locale locale) {
	super(id, locale);
	this.hs = hs;
    }

    /**
     * Create an IndexItem defaulting the HelpSet to that of its ID.
     *
     * @param id ID for the item. The ID can be null.
     * @param locale The locale to use for this item.
     */
    public IndexItem(ID id, Locale locale) {
	super(id, locale);
	if (id != null) {
	    this.hs = id.hs;
	} else {
	    this.hs = null;
	}
    }

    /**
     * Create a default IndexItem.
     */
    public IndexItem() {
	super(null, null);
	this.hs = null;
    }

    /**
     * Returns the HelpSet scoping this IndexItem.
     */
    public HelpSet getHelpSet() {
	 return hs;
    }
}

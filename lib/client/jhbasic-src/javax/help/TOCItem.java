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
 * A class for individual TOC items
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-llopart
 * @(#)TOCItem.java 1.13 01/29/99
 */

public class TOCItem extends TreeItem { 
    private ID imageID;
    private HelpSet hs;

    /**
     * Creates a TOCItem.
     *
     * @param id ID for the item. A null ID is valid.
     * @param image The ID for image to be displayed for this item. A null
     * image is valid.
     * @param hs The HelpSet scoping this item.  In almost all cases
     * this is the same as the HelpSet of the id field. A null ID is valid.
     * @param lang The locale for this item. A null locale indicates the
     * default locale.
     */
    public TOCItem(ID id, ID imageID, HelpSet hs, Locale locale) {
	super(id, locale);
	this.imageID = imageID;
	this.hs = hs;
    }

    /**
     * Creates a TOCItem with a default HelpSet based on its ID.
     *
     * @param id ID for the item. The ID can be null.
     * @param image The image to be displayed for this item.
     * @param lang The locale for this item
     */
    public TOCItem(ID id, ID imageID, Locale locale){
	super(id, locale);
	if (id != null) {
	    this.hs = id.hs;
	} else {
	    this.hs = null;
	}
	this.imageID = imageID;
	this.hs = hs;
    }

    /**
     * Creates a default TOCItem.
     */

    public TOCItem() {
	super(null, null);
	this.imageID = null;
	this.hs = null;
    }

    /**
     * Returns the image for this TOCItem.
     */
    public ID getImageID() {
	 return imageID;
    }

    /**
     * Returns the HelpSet scoping this TOCItem.
     */
    public HelpSet getHelpSet() {
	 return hs;
    }
}

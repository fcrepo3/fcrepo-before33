/*
 * @(#) SearchView.java 1.5 - last change made 01/29/99
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

package javax.help;

import java.util.Hashtable;
import java.awt.Component;
import java.util.Locale;

/**
 * Navigational View information for a Search
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.5	01/29/99
 */

public class SearchView extends NavigatorView {
    /**
     * Constructs a SearchView with some given data.  Locale of the View defaults
     * to that of the HelpSet.
     *
     * @param hs The HelpSet that provides context information.
     * @param name The name of the View.
     * @param label The label (to show the user) of the View.
     * @param locale The default locale to interpret the data in this TOC.
    * @param params A hashtable that provides different key/values for this type.
    */
    public SearchView(HelpSet hs,
		      String name,
		      String label,
		      Hashtable params) {
	super(hs, name, label, hs.getLocale(), params);
    }

    /**
     * Constructs a SearchView with some given data.
     *
     * @param hs The HelpSet that provides context information.
     * @param name The name of the View.
     * @param label The label (to show the user) of the View.
     * @param locale The default locale to interpret the data in this TOC.
    * @param params A hashtable that provides different key/values for this type.
    */
    public SearchView(HelpSet hs,
		      String name,
		      String label,
		      Locale locale,
		      Hashtable params) {
	super(hs, name, label, locale, params);
    }

    /**
     * Creates a navigator for a given model.
     */
    public Component createNavigator(HelpModel model) {
	return new JHelpSearchNavigator(this, model);
    }
    

}

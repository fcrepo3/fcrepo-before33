/*
 * @(#) InvalidNavigatorViewException.java 1.11 - last change made 03/19/99
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
import java.util.Locale;

/**
 * JHelpNavigator cannot deal with given NavigatorView.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.11	03/19/99
 */

public class InvalidNavigatorViewException extends Exception 
{
    /**
     * Create an exception. All parameters accept null values.
     * 
     * @param msg The message. If msg is null it is the same as if
     * no detailed message was specified.
     */
    public InvalidNavigatorViewException(String msg,
					 HelpSet hs,
					 String name,
					 String label,
					 Locale locale,
					 String className,
					 Hashtable params) {
	super(msg);
	this.hs = hs;
	this.name = name;
	this.label = label;
	this.locale = locale;
	this.className = className;
	this.params = params;
    }

    /**
     * @return The helpset
     */
    public HelpSet getHelpSet() {
	return hs;
    }

    /**
     * @return The name
     */
    public String getName() {
	return name;
    }

    /**
     * @return The label
     */
    public String getLabel() {
	return label;
    }

    /**
     * @return The locale
     */
    public Locale getLocale() {
	return locale;
    }

    /**
     * @return The className
     */
    public String getClassName() {
	return className;
    }

    /**
     * @return The parameters
     */
    public Hashtable getParams() {
	return params;
    }

    private HelpSet hs;
    private String name;
    private String label;
    private Locale locale;
    private String className;
    private Hashtable params;
}

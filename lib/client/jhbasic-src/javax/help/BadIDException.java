/*
 * @(#) BadIDException.java 1.14 - last change made 03/16/99
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

/**
 * An ID was attempted to be created with incorrect arguments
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version	1.14	03/16/99
 */

public class BadIDException extends IllegalArgumentException {
    private Map map;
    private String id;
    private HelpSet hs;

    /**
     * Create the exception. Null values are allowed for each parameter.
     * 
     * @param map The Map in which the ID wasn't found
     * @param msg A generic message
     * @param id The ID in Map that wasn't found
     * @see javax.help.Map
     */
    public BadIDException(String msg, Map map, String id, HelpSet hs) {
	super(msg);
	this.map = map;
	this.id = id;
	this.hs = hs;
    }

    /**
     * The HelpSet in which the ID wasn't found
     */
    public Map getMap() {
	return map;
    }

    /**
     * The ID that wasn't found in the Map
     */
    public String getID() {
	return id;
    }

    /**
     * The HelpSet that wasn't found in the Map
     */
    public HelpSet getHelpSet() {
	return hs;
    }
}


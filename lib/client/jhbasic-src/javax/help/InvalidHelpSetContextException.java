/*
 * @(#) InvalidHelpSetContextException.java 1.3 - last change made 01/29/99
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
 * The HelpSet is not a (transitive) sub-HelpSet of some context HelpSet.
 *
 * For example, setting an ID to a HelpModel.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.3	01/29/99
 */
public class InvalidHelpSetContextException extends Exception {
    private HelpSet context;
    private HelpSet hs;

    /**
     * Create the exception. All parameters accept null values.
     * 
     * @param msg The message. If msg is null it is the same as if
     * no detailed message was specified.
     */
    public InvalidHelpSetContextException(String msg,
					  HelpSet context,
					  HelpSet hs) {
	super(msg);
	this.context = context;
	this.hs = hs;
    }

    /**
     * Get the context HelpSet
     */
    public HelpSet getContext() {
	return context;
    }

    /**
     * Get the offending HelpSet
     */
    public HelpSet getHelpSet() {
	return hs;
    }
}

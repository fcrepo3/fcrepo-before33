/*
 * @(#) HelpSetEvent.java 1.10 - last change made 03/10/99
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

package javax.help.event;

import javax.help.HelpSet;

/**
 * Conveys information when a HelpSet is added/removed.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.10	03/10/99
 */

public class HelpSetEvent extends java.util.EventObject {

    /**
     * Creates a HelpSetEvent.
     *
     * @param source Source of this Event.
     * @param helpset The HelpSet being added/removed.
     * @param action HELPSET_ADDED or HELPSET_REMOVED.
     * @throws IllegalArgumentException if source is null or if action is not
     * a valid action.
     */
     public HelpSetEvent(Object source, HelpSet helpset, int action) {
	 super(source);
         this.helpset = helpset;
	 if (helpset == null) {
	     throw new NullPointerException("helpset");
	 }
         this.action = action;
	 if (action < 0 || action > 1) {
	     throw new IllegalArgumentException("invalid action");
	 }
     }

    /**
     * A HelpSet was added
     */
     public static final int HELPSET_ADDED = 0;

    /**
     * A HelpSet was removed
     */
     public static final int HELPSET_REMOVED = 1;

    /**
     * @return The HelpSet.
     */
     public HelpSet getHelpSet() {
	return helpset;
     }

    /**
     * @return The action
     */
     public int getAction() {
        return action;
     }

    /**
     * Returns textual about the instance. 
     */
    public String toString() {
	if (action==HELPSET_ADDED) {
	    return "HelpSetEvent("+source+", "+helpset+"; added";
	} else {
	    return "HelpSetEvent("+source+", "+helpset+"; removed";
	}
    }

     private HelpSet helpset;
     private int action;
}

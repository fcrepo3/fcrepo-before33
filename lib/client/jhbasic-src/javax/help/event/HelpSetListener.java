/*
 * @(#) HelpSetListener.java 1.8 - last change made 01/29/99
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

/**
 * Defines the interface of an object that listens to 
 * changes in the HelpSet.
 *
 * @author Eduardo Pelegri-Llopart
 * @version	1.8	01/29/99
 */

public interface HelpSetListener extends java.util.EventListener {

    /**
     * Invoked when a new HelpSet is added.
     *
     * @param e The event
     */
    public void helpSetAdded(HelpSetEvent e);

    /**
     * Invoked when a new HelpSet is removed.
     *
     * @param e The event
     */
    public void helpSetRemoved(HelpSetEvent e);
}

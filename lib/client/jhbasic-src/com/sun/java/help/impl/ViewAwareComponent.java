/*
 * @(#) ViewAwareComponent.java 1.4 - last change made 01/29/99
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

package com.sun.java.help.impl;

import javax.swing.text.*;

/**
 * Interface that a Component should support if it wants to play in the View
 * hierachy.
 *
 * WARNING!! This is an experimental feature of the JavaHelp reference
 * implemenation and may change in future versions of the implementation.
 *
 * @author Eduardo Pelegri-Llopart
 * @version 1.4 01/29/99 */


public interface ViewAwareComponent {
    /**
     * Set the View that corresponds to this object
     * This gives access to a wealth of information.
     */
    public void setViewData(View v);

    /**
    * May need something to react to changes (in my view?)
    */
}

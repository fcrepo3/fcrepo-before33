/*
 * @(#) TextHelpModelEvent.java 1.5 - last change made 03/19/99
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

import java.net.URL;
import java.util.Vector;
import java.util.Enumeration;
import javax.help.HelpSet;
import javax.help.Map.ID;

/**
 * Notifies interested parties when a change in a
 * TextHelpModel occurs.  The listener should query the source
 * for details.
 *
 * @author Eduardo Pelegri-Llopart
 * @author Roger D. Brinkley
 * @version	1.5	03/19/99
 */

public class TextHelpModelEvent extends java.util.EventObject {
    /**
     * Creates a TextHelpModelEvent.
     *
     * @param source The source for this event.  Implements TextHelpModel
     * @throws IllegalArgumentException if source is null.
     */
    public TextHelpModelEvent(Object source) {
	super (source);
    }
}

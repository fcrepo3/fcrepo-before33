/*
 * @(#) HelpUI.java 1.20 - last change made 03/19/99
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

package javax.help.plaf;

import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.*;
import javax.help.JHelpNavigator;
import java.util.Enumeration;

/**
 * UI factory interface for JHelp.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @version   1.20     03/19/99
 */

public abstract class HelpUI extends ComponentUI {

    /**
     * Adds a Navigator.
     *
     * @param nav the Navigator to add
     */
   public abstract void addNavigator(JHelpNavigator nav);

    /**
     * Remove a Navigator.
     *
     * @param nav The Navigator to remove.
     */
   public abstract void removeNavigator(JHelpNavigator nav);

    /**
     * Sets the current Navigator.
     *
     * @param nav The current Navigator to show.
     */
    public abstract void setCurrentNavigator(JHelpNavigator nav);

    /**
     * Gets the current Navigator.
     *
     * @param nav The current Navigator to show.
     */
    public abstract JHelpNavigator getCurrentNavigator();

}

/*
 * @(#) HelpNavigatorUI.java 1.24 - last change made 02/21/01
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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.help.HelpModel;
import javax.help.event.HelpModelListener;
import javax.help.event.HelpModelEvent;
import javax.help.NavigatorView;
import javax.help.Map;
import java.net.URL;

/**
 * UI factory interface for JHelpNavigator.
 *
 * @author Roger D. Brinkley
 * @author Eduardo Pelegri-Llopart
 * @author Stepan Marek
 * @version   %I     02/21/01
 */

public abstract class HelpNavigatorUI extends ComponentUI {

    private Icon icon;

    /**
     * Sets the icon for this HelpNavigator.
     *
     * @param icon the Icon
     */
    public void setIcon(Icon icon) {
	this.icon = icon;
    }

    /**
     * @return the Icon for this HelpNavigator
     */
    public Icon getIcon() {
	return icon;
    }

    /**
     * Merges a Navigator View.
     */
    public abstract void merge(NavigatorView view);

    /**
     * Removes a Navigator View.
     */
    public abstract void remove(NavigatorView view);
    
    /**
     * Returns icon associated with the view.
     *
     * @param view the view
     * @return the ImageIcon for the view
     */
    public ImageIcon getImageIcon(NavigatorView view) {
        ImageIcon icon = null;
        Map.ID id = view.getImageID();
        if (id != null) {
            try {
                Map map = view.getHelpSet().getCombinedMap();
                URL url = map.getURLFromID(id);
                icon = new ImageIcon(url);
		} catch (Exception e) {
		}
        }
        return icon;
    }
}

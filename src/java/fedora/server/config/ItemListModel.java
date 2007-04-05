/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.config;

import java.util.*;
import javax.swing.*;

/**
 * A ListModel, backed by a java-util-List.
 *
 */
public class ItemListModel extends DefaultListModel {

	private static final long serialVersionUID = 1L;
	
    public ItemListModel(List items) {
        for (int i = 0; i < items.size(); i++) {
            addElement(items.get(i));
        }
    }

    public List toList() {
        ArrayList out = new ArrayList();
        Object[] array = toArray();
        for (int i = 0; i < array.length; i++) {
            out.add(array[i]);
        }
        return out;
    }

}

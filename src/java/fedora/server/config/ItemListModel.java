package fedora.server.config;

import java.util.*;
import javax.swing.*;

/**
 * A ListModel, backed by a java-util-List.
 */
public class ItemListModel extends DefaultListModel {

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

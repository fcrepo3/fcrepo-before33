package fedora.server.config;

import java.util.*;
import javax.swing.*;

/**
 * A ListModel, backed by a java-util-List.
 */
public class ParameterListModel extends DefaultListModel {

    public ParameterListModel(List parameters) {
        for (int i = 0; i < parameters.size(); i++) {
            addElement((Parameter) parameters.get(i));
        }
    }

    public List toList() {
        ArrayList out = new ArrayList();
        Object[] array = toArray();
        for (int i = 0; i < array.length; i++) {
            out.add((Parameter) array[i]);
        }
        return out;
    }

}

/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.bmech;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author Sandy Payette
 */
public class ComboBoxRenderer
        extends JComboBox
        implements TableCellRenderer {

    private static final long serialVersionUID = 1L;

    JComboBox component;

    public ComboBoxRenderer(String[] items) {
        super(items);
        component = new JComboBox(items);
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column) {

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        component.setSelectedItem(value);
        return component;
    }
}

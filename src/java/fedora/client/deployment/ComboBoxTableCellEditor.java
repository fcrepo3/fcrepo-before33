/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.deployment;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * @author Sandy Payette
 */
public class ComboBoxTableCellEditor
        extends AbstractCellEditor
        implements TableCellEditor {

    private static final long serialVersionUID = 1L;

    // This is the component that will handle the editing of the cell value
    JComponent component;

    public ComboBoxTableCellEditor(String[] items) {
        super();
        component = new JComboBox(items);

    }

    // This method is called when a cell value is edited by the user.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int rowIndex,
                                                 int vColIndex) {

        if (isSelected) {
            // cell (and perhaps other cells) are selected
        }

        // Configure the component with the specified value
        ((JComboBox) component).setSelectedItem(value);

        // Return the configured component
        return component;
    }

    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.
    public Object getCellEditorValue() {
        return ((JComboBox) component).getSelectedItem();
    }

}

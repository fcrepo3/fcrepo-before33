package fedora.client.bmech;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import javax.swing.table.TableCellEditor;

/*
    public class ComboBoxTableCellEditor extends DefaultCellEditor
    {
        public ComboBoxTableCellEditor(String[] items) {
            super(new JComboBox(items));
        }
    }
  */
    public class ComboBoxTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        // This is the component that will handle the editing of the
        // cell value
        JComponent component;


        public ComboBoxTableCellEditor(String[] items)
        {
          super();
          component = new JComboBox(items);

        }

        // This method is called when a cell value is edited by the user.
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int rowIndex, int vColIndex) {

            if (isSelected)
            {
                // cell (and perhaps other cells) are selected
            }

            // Configure the component with the specified value
            ((JComboBox)component).setSelectedItem((String)value);

            // Return the configured component
            return component;
        }

        // This method is called when editing is completed.
        // It must return the new value to be stored in the cell.
        public Object getCellEditorValue() {
            return ((JComboBox)component).getSelectedItem();
        }

    }

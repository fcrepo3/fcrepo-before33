package fedora.client.bmech;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellEditor;

    /**
     *
     * <p><b>Title:</b> ComboBoxTableCellEditor.java</p>
     * <p><b>Description:</b> </p>
     *
     * -----------------------------------------------------------------------------
     *
     * <p><b>License and Copyright: </b>The contents of this file are subject to the
     * Mozilla Public License Version 1.1 (the "License"); you may not use this file
     * except in compliance with the License. You may obtain a copy of the License
     * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
     *
     * <p>Software distributed under the License is distributed on an "AS IS" basis,
     * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
     * the specific language governing rights and limitations under the License.</p>
     *
     * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
     * Rector and Visitors of the University of Virginia and Cornell University.
     * All rights reserved.</p>
     *
     * -----------------------------------------------------------------------------
     *
     * @author payette@cs.cornell.edu
     * @version $Id$
     */
    public class ComboBoxTableCellEditor extends AbstractCellEditor implements TableCellEditor {
        // This is the component that will handle the editing of the cell value
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

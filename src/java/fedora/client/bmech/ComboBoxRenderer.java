package fedora.client.bmech;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

  /**
   *
   * <p><b>Title:</b> ComboBoxRenderer.java</p>
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
   * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
   * Rector and Visitors of the University of Virginia and Cornell University.
   * All rights reserved.</p>
   *
   * -----------------------------------------------------------------------------
   *
   * @author payette@cs.cornell.edu
   * @version $Id$
   */
  public class ComboBoxRenderer extends JComboBox
            implements TableCellRenderer {

	 JComboBox component; 
	 
     public ComboBoxRenderer(String[] items) {
        super(items);
		component = new JComboBox(items);
     }

     public Component getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

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

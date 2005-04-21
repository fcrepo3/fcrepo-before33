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

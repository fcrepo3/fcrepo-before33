package fedora.client.bmech;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;


  import java.awt.Component;
  import javax.swing.JComboBox;
  import javax.swing.JTable;
  import javax.swing.table.TableCellRenderer;

  public class ComboBoxRenderer extends JComboBox
            implements TableCellRenderer {

     public ComboBoxRenderer(String[] items) {
        super(items);
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

        // Select the current value
        setSelectedItem(value);
        return this;
      }
   }

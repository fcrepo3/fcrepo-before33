package fedora.swing.jtable;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * <p><b>Title:</b> SortHeaderRenderer.java</p>
 * <p><b>Description:</b>
 * <p>
 *
 * -----------------------------------------------------------------------------
 *
 * Portions created by Claude Duguay are Copyright &copy;
 * Claude Duguay, originally made available at
 * http://www.fawcette.com/javapro/2002_08/magazine/columns/visualcomponents/</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author Claude Duguay, cwilper@cs.cornell.edu
 * @version $Id$
 */
public class SortHeaderRenderer
  extends DefaultTableCellRenderer
{
  public static Icon NONSORTED =
    new SortArrowIcon(SortArrowIcon.NONE);
  public static Icon ASCENDING =
    new SortArrowIcon(SortArrowIcon.ASCENDING);
  public static Icon DECENDING =
    new SortArrowIcon(SortArrowIcon.DECENDING);

  public SortHeaderRenderer()
  {
    setHorizontalTextPosition(LEFT);
    setHorizontalAlignment(CENTER);
  }

  public Component getTableCellRendererComponent(
    JTable table, Object value, boolean isSelected,
    boolean hasFocus, int row, int col)
  {
    int index = -1;
    boolean ascending = true;
    if (table instanceof JSortTable)
    {
      JSortTable sortTable = (JSortTable)table;
      index = sortTable.getSortedColumnIndex();
      ascending = sortTable.isSortedColumnAscending();
    }
    if (table != null)
    {
      JTableHeader header = table.getTableHeader();
      if (header != null)
      {
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
      }
    }
    Icon icon = ascending ? ASCENDING : DECENDING;
    setIcon(col == index ? icon : NONSORTED);
    setText((value == null) ? "" : value.toString());
    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    return this;
  }
}


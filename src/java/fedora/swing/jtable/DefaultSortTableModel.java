package fedora.swing.jtable;

import java.util.*;
import javax.swing.table.*;

public class DefaultSortTableModel
  extends DefaultTableModel
  implements SortTableModel
{
  public DefaultSortTableModel() {}
  
  public DefaultSortTableModel(int rows, int cols)
  {
    super(rows, cols);
  }
  
  public DefaultSortTableModel(Object[][] data, Object[] names)
  {
    super(data, names);
  }
  
  public DefaultSortTableModel(Object[] names, int rows)
  {
    super(names, rows);
  }
  
  public DefaultSortTableModel(Vector names, int rows)
  {
    super(names, rows);
  }
  
  public DefaultSortTableModel(Vector data, Vector names)
  {
    super(data, names);
  }
  
  public boolean isSortable(int col)
  {
    //return true;   // FIXME: columns can't be sorted till the how-do-i-get-the-pid-if-its-not-part-of-the-table-model-and-the-model-has-been-sorted problem is solved
    return false;
  }
  
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
  
  public void sortColumn(int col, boolean ascending)
  {
    Collections.sort(getDataVector(),
      new ColumnComparator(col, ascending));
  }
}


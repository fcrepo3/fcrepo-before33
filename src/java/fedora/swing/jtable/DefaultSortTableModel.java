package fedora.swing.jtable;

import java.util.*;
import javax.swing.table.*;

/**
 * <p><b>Title:</b> DefaultSortableTableModel.java</p>
 * <p><b>Description:</b>
 * <p>
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
 * <p>Copyright &copy; 2002, 2003 by The Rector and Visitors of the University of
 * Virginia and Cornell University. All rights reserved.
 * Portions created by Claude Duguay are Copyright &copy;
 * Claude Duguay, originally made available at
 * http://www.fawcette.com/javapro/2002_08/magazine/columns/visualcomponents/</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author Claude Duguay, cwilper@cs.cornell.edu
 * @version $Id$
 */
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


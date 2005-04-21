package fedora.swing.jtable;

import javax.swing.table.*;

/**
 * <p><b>Title:</b> SortTableModel.java</p>
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
public interface SortTableModel
  extends TableModel
{
  public boolean isSortable(int col);
  public void sortColumn(int col, boolean ascending);
}


package fedora.swing.jtable;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * <p><b>Title:</b> JSortTable.java</p>
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
 * <p>Copyright &copy; 2002-2005 by The Rector and Visitors of the University of
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
public class JSortTable extends JTable
  implements MouseListener
{
  protected int sortedColumnIndex = -1;
  protected boolean sortedColumnAscending = true;

  public JSortTable()
  {
    this(new DefaultSortTableModel());
  }

  public JSortTable(int rows, int cols)
  {
    this(new DefaultSortTableModel(rows, cols));
  }

  public JSortTable(Object[][] data, Object[] names)
  {
    this(new DefaultSortTableModel(data, names));
  }

  public JSortTable(Vector data, Vector names)
  {
    this(new DefaultSortTableModel(data, names));
  }

  public JSortTable(SortTableModel model)
  {
    super(model);
    initSortHeader();
  }

  public JSortTable(SortTableModel model,
    TableColumnModel colModel)
  {
    super(model, colModel);
    initSortHeader();
  }

  public JSortTable(SortTableModel model,
    TableColumnModel colModel,
    ListSelectionModel selModel)
  {
    super(model, colModel, selModel);
    initSortHeader();
  }

  protected void initSortHeader()
  {
    JTableHeader header = getTableHeader();
    header.setDefaultRenderer(new SortHeaderRenderer());
    header.addMouseListener(this);
  }

  public int getSortedColumnIndex()
  {
    return sortedColumnIndex;
  }

  public boolean isSortedColumnAscending()
  {
    return sortedColumnAscending;
  }

  public void mouseReleased(MouseEvent event)
  {
    TableColumnModel colModel = getColumnModel();
    int index = colModel.getColumnIndexAtX(event.getX());
    int modelIndex = colModel.getColumn(index).getModelIndex();

    SortTableModel model = (SortTableModel)getModel();
    if (model.isSortable(modelIndex))
    {
      // toggle ascension, if already sorted
      if (sortedColumnIndex == index)
      {
        sortedColumnAscending = !sortedColumnAscending;
      }
      sortedColumnIndex = index;

      model.sortColumn(modelIndex, sortedColumnAscending);
    }
  }

  public void mousePressed(MouseEvent event) {}
  public void mouseClicked(MouseEvent event) {}
  public void mouseEntered(MouseEvent event) {}
  public void mouseExited(MouseEvent event) {}
}


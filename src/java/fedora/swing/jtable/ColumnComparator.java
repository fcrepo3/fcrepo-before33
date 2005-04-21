package fedora.swing.jtable;

import java.util.*;

/**
 * <p><b>Title:</b> ColumnComparator.java</p>
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
public class ColumnComparator
  implements Comparator
{
  protected int index;
  protected boolean ascending;

  public ColumnComparator(int index, boolean ascending)
  {
    this.index = index;
    this.ascending = ascending;
  }

  public int compare(Object one, Object two)
  {
    if (one instanceof Vector &&
        two instanceof Vector)
    {
      Vector vOne = (Vector)one;
      Vector vTwo = (Vector)two;
      Object oOne = vOne.elementAt(index);
      Object oTwo = vTwo.elementAt(index);
      if (oOne instanceof Comparable &&
          oTwo instanceof Comparable)
      {
        Comparable cOne = (Comparable)oOne;
        Comparable cTwo = (Comparable)oTwo;
        if (ascending)
        {
          return cOne.compareTo(cTwo);
        }
        else
        {
          return cTwo.compareTo(cOne);
        }
      }
    }
    return 1;
  }
}


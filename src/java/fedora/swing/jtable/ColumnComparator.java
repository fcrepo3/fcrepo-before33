package fedora.swing.jtable;

import java.util.*;

/**
 * <p><b>Title:</b> ColumnComparator.java</p>
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
 * @author cwilper
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


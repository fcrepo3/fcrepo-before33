package fedora.swing.jtable;

import java.awt.*;
import java.util.*;
import javax.swing.*;

/**
 * <p><b>Title:</b> JSortTableTest.java</p>
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
 * <p>Copyright &copy; 2002-2004 by The Rector and Visitors of the University of
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
public class JSortTableTest
  extends JPanel
{
  public JSortTableTest()
  {
    setLayout(new GridLayout());
    setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    setPreferredSize(new Dimension(400, 400));
    add(new JScrollPane(new JSortTable(makeModel())));
  }

  protected SortTableModel makeModel()
  {
    Vector data = new Vector();
    for (int i = 0; i < 25; i++)
    {
      Vector row = new Vector();
      for (int j = 0; j < 5; j++)
      {
        row.add(new Integer((int)(Math.random() * 256)));
      }
      data.add(row);
    }

    Vector names = new Vector();
    names.add("One");
    names.add("Two");
    names.add("Three");
    names.add("Four");
    names.add("Five");

    return new DefaultSortTableModel(data, names);
  }

  public static void main(String[] args)
  {
    JFrame frame = new JFrame("JSortTable Test");
    frame.getContentPane().setLayout(new GridLayout());
    frame.getContentPane().add(new JSortTableTest());
    frame.pack();
    frame.show();
  }
}


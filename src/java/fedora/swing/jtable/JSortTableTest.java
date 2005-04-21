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


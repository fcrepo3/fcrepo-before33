package fedora.client.bmech;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComponent;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
//import java.util.HashMap;
import java.util.Vector;

import fedora.client.bmech.data.DCElement;

/**
 *
 * <p><b>Title:</b> GeneralPane.java</p>
 * <p><b>Description:</b> </p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version 1.0
 */
public class GeneralPane
        extends JPanel {

    private JTextField bDefPID;
    private JTextField bMechLabel;
    private JTextField bMechName;
    private JTable dcTable;
    protected DefaultTableModel model;

    public GeneralPane()
    {
        //setSize(600, 400);
        setLayout(new BorderLayout());

        // Text Fields Panel
        JPanel textPanel = new JPanel();
        textPanel.setBorder(new TitledBorder("Service Contract"));
        textPanel.setLayout(new GridLayout(6,2));
        textPanel.add(new JLabel(""));
        textPanel.add(new JLabel(""));
        textPanel.add(new JLabel("Behavior Mechanism PID: "));
        textPanel.add(new JLabel("system assigned"));
        textPanel.add(new JLabel("Behavior Definition Contract (bDefPID): "));
        textPanel.add(bDefPID = new JTextField());
        textPanel.add(new JLabel("Behavior Mechanism Label: "));
        textPanel.add(bMechLabel = new JTextField());
        textPanel.add(new JLabel("Behavior Mechanism Nickname (1 word): "));
        textPanel.add(bMechName = new JTextField());
        textPanel.add(new JLabel(""));
        textPanel.add(new JLabel(""));

        // Table Panel
        model = new DefaultTableModel();
        dcTable = new JTable(model);
        dcTable.setColumnSelectionAllowed(false);
        dcTable.setRowSelectionAllowed(true);
        model.addColumn("DC Element Name");
        model.addColumn("Value");
        model.addRow(new Object[]{"title", ""});
        model.addRow(new Object[]{"creator", ""});
        model.addRow(new Object[]{"subject", ""});
        model.addRow(new Object[]{"publisher", ""});
        model.addRow(new Object[]{"contributor", ""});
        model.addRow(new Object[]{"date", ""});
        model.addRow(new Object[]{"type", ""});
        model.addRow(new Object[]{"format", ""});
        model.addRow(new Object[]{"identifier", ""});
        model.addRow(new Object[]{"source", ""});
        model.addRow(new Object[]{"language", ""});
        model.addRow(new Object[]{"relation", ""});
        model.addRow(new Object[]{"coverage", ""});
        model.addRow(new Object[]{"rights", ""});
        JScrollPane scrollpane = new JScrollPane(dcTable);

        // Table Buttons Panel
        JButton jb1 = new JButton("New");
        jb1.setMinimumSize(new Dimension(100,30));
        jb1.setMaximumSize(new Dimension(100,30));
        jb1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addTableRow();
          }
        } );
        JButton jb2 = new JButton("Insert");
        jb2.setMinimumSize(new Dimension(100,30));
        jb2.setMaximumSize(new Dimension(100,30));
        jb2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            insertTableRow();
            //insertTableRow(model);
          }
        } );
        JButton jb3 = new JButton("Delete");
        jb3.setMinimumSize(new Dimension(100,30));
        jb3.setMaximumSize(new Dimension(100,30));
        jb3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            deleteTableRow();
          }
        } );
        JPanel t_buttonPanel = new JPanel();
        //t_buttonPanel.setLayout(new GridLayout(3,1));
        t_buttonPanel.setLayout(new BoxLayout(t_buttonPanel, BoxLayout.Y_AXIS));
        t_buttonPanel.add(jb1);
        t_buttonPanel.add(jb2);
        t_buttonPanel.add(jb3);

        JPanel dcPanel = new JPanel(new BorderLayout());
        dcPanel.setBorder(new TitledBorder("Dublin Core Metadata for Mechanism:"));
        dcPanel.add(scrollpane, BorderLayout.CENTER);
        dcPanel.add(t_buttonPanel, BorderLayout.EAST);

        add(textPanel, BorderLayout.NORTH);
        //add(scrollpane, BorderLayout.CENTER);
        //add(t_buttonPanel, BorderLayout.EAST);
        add(dcPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public String getBDefPID()
    {
      return bDefPID.getText();
    }

    public String getBMechLabel()
    {
      return bMechLabel.getText();
    }

    public String getBMechName()
    {
      return bMechName.getText();
    }

    public DCElement[] getDCElements()
    {
      if (dcTable.isEditing())
      {
        dcTable.getCellEditor().stopCellEditing();
      }
      //HashMap elements = new HashMap();
      Vector elements = new Vector();
      int rowcount = dcTable.getModel().getRowCount();
      System.out.println("dcTable rowcount=" + rowcount);
      for (int i=0; i<rowcount; i++)
      {
        DCElement dcElement = new DCElement();
        dcElement.elementName = (String)dcTable.getValueAt(i,0);
        dcElement.elementValue = (String)dcTable.getValueAt(i,1);
        if ((dcElement.elementName != null) &&
          !(dcElement.elementName.trim().equals("")) &&
           (dcElement.elementValue != null) &&
          !(dcElement.elementValue.trim().equals("")))
        {
          elements.add(dcElement);
        }
      }
      return (DCElement[])elements.toArray(new DCElement[0]);
    }

    private void addTableRow()
    {
      // Append a row
      model.addRow(new Object[]{"", ""});
    }

    private void insertTableRow()
    {
      model.insertRow((dcTable.getSelectedRow() + 1), new Object[]{"",""});
    }

    private void deleteTableRow()
    {
      model.removeRow(dcTable.getSelectedRow());
    }
}
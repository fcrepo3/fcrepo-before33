package fedora.client.bmech;

import javax.swing.JInternalFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.StringTokenizer;
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */

public class GeneralPane extends JPanel
{
    private JInternalFrame parent;
    private JTextField bDefPID;
    protected JTextField bObjectPID;
    private JRadioButton rb_sysPID;
    private JRadioButton rb_retainPID;
    private final ButtonGroup rb_buttonGroup = new ButtonGroup();
    protected String rb_chosen;
    private JTextField bObjectLabel;
    private JTextField bObjectName;
    private JTable dcTable;
    protected DefaultTableModel model;

    public GeneralPane(BMechBuilder parent)
    {
        this.parent = parent;
        setLayout(new BorderLayout());
        JPanel contractPanel = new JPanel();
        contractPanel.setLayout(new GridBagLayout());
        contractPanel.setBorder(new TitledBorder("Behavior Definition Contract"));
        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.anchor = GridBagConstraints.WEST;
        gbc2.gridy = 0;
        gbc2.gridx = 0;
        contractPanel.add(
          new JLabel("Behavior Definition PID:                         "),
          gbc2);
        gbc2.gridx = 1;
        contractPanel.add(bDefPID = new JTextField(20), gbc2);
        gbc2.gridx = 2;
        contractPanel.add(new JLabel("                             "), gbc2);
        gbc2.gridx = 3;
        contractPanel.add(new JLabel("                             "), gbc2);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2,1));
        topPanel.add(setDescriptionPanel());
        topPanel.add(contractPanel);

        add(topPanel, BorderLayout.NORTH);
        add(setDCPanel(), BorderLayout.CENTER);
        setVisible(true);
    }

    public GeneralPane(BDefBuilder parent)
    {
        this.parent = parent;
        setLayout(new BorderLayout());
        add(setDescriptionPanel(), BorderLayout.NORTH);
        add(setDCPanel(), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel setDescriptionPanel()
    {
        ActionListener rb_listen = new PIDActionListener();
        rb_sysPID = new JRadioButton("system assigned", true);
        rb_sysPID.setActionCommand("sysPID");
        rb_sysPID.addActionListener(rb_listen);
        rb_chosen = "sysPID";
        rb_retainPID = new JRadioButton("use PID", false);
        rb_retainPID.setActionCommand("retainPID");
        rb_retainPID.addActionListener(rb_listen);
        //rb_buttonGroup = new ButtonGroup();
        rb_buttonGroup.add(rb_sysPID);
        rb_buttonGroup.add(rb_retainPID);
        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new GridBagLayout());
        descriptionPanel.setBorder(new TitledBorder("Behavior Mechanism Description"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy = 0;
        gbc.gridx = 0;
        descriptionPanel.add(new JLabel("Behavior Object PID: "), gbc);
        gbc.gridx = 1;
        descriptionPanel.add(rb_sysPID, gbc);
        gbc.gridx = 2;
        descriptionPanel.add(rb_retainPID, gbc);
        gbc.gridx = 3;
        descriptionPanel.add(bObjectPID = new JTextField(10), gbc);
        bObjectPID.setToolTipText("The repository will accept test PIDs"
          + " with the prefixes 'test:' or 'demo:' (e.g., 'demo:1').");
        bObjectPID.setEnabled(false);
        gbc.gridy = 1;
        gbc.gridx = 0;
        descriptionPanel.add(new JLabel("Behavior Object Name (1 word): "), gbc);
        gbc.gridx = 1;
        descriptionPanel.add(bObjectName = new JTextField(20), gbc);
        bObjectName.setToolTipText("This one-word name will be used in metadata"
          + " within the behavior object (e.g., in WSDL as the service name).");
        gbc.gridy = 2;
        gbc.gridx = 0;
        descriptionPanel.add(new JLabel("Behavior Object Description: "), gbc);
        gbc.gridx = 1;
        descriptionPanel.add(bObjectLabel = new JTextField(20), gbc);
        bObjectLabel.setToolTipText("This is a free-form textual description of the"
          + "behavior object.");
        gbc.gridy = 3;
        gbc.gridx = 0;
        descriptionPanel.add(new JLabel(" "), gbc);
        return descriptionPanel;
    }

    private JPanel setDCPanel()
    {
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
        dcPanel.setBorder(new TitledBorder("Dublin Core Metadata"));
        dcPanel.add(scrollpane, BorderLayout.CENTER);
        dcPanel.add(t_buttonPanel, BorderLayout.EAST);
        return dcPanel;
    }

    public String getBDefContractPID()
    {
      if (parent.getClass().getName().equalsIgnoreCase(
        "fedora.client.bmech.BMechBuilder"))
      {
        return bDefPID.getText();
      }
      return null;
    }

    public String getBObjectPID()
    {
      return bObjectPID.getText();
    }

    public String getBObjectLabel()
    {
      return bObjectLabel.getText();
    }

    public String getBObjectName()
    {
      String s = bObjectName.getText();
      StringTokenizer st = new StringTokenizer(s," ",false);
      String nameNoSpaces = "";
      while (st.hasMoreElements()) nameNoSpaces += st.nextElement();
      return nameNoSpaces;
    }

    public DCElement[] getDCElements()
    {
      if (dcTable.isEditing())
      {
        dcTable.getCellEditor().stopCellEditing();
      }
      Vector elements = new Vector();
      int rowcount = dcTable.getModel().getRowCount();
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
	
    // Action Listener for button group
    class PIDActionListener implements ActionListener
    {
      public void actionPerformed(ActionEvent e)
      {
        rb_chosen = rb_buttonGroup.getSelection().getActionCommand();
        if (rb_chosen.equalsIgnoreCase("retainPID"))
        {
          bObjectPID.setEnabled(true);
        }
        else if (rb_chosen.equalsIgnoreCase("sysPID"))
        {
          bObjectPID.setEnabled(false);
          bObjectPID.setText("");
        }
      }
    }
}

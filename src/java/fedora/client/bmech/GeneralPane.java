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
import java.util.HashMap;

public class GeneralPane
        extends JPanel {

    private JTextField bDefPID;
    private JTextField bMechLabel;
    private JTable dcTable;
    protected DefaultTableModel model;

    public GeneralPane()
    {
        //setSize(600, 400);
        setLayout(new BorderLayout());

        // Text Fields Panel
        JPanel textPanel = new JPanel();
        textPanel.setBorder(new TitledBorder("Service Contract"));
        textPanel.setLayout(new GridLayout(5,2));
        textPanel.add(new JLabel(""));
        textPanel.add(new JLabel(""));
        textPanel.add(new JLabel("Behavior Mechanism PID: "));
        textPanel.add(new JLabel("unassigned"));
        textPanel.add(new JLabel("Enter Behavior Definition Contract (bDefPID): "));
        textPanel.add(bDefPID = new JTextField());
        textPanel.add(new JLabel("Enter Behavior Mechanism Label: "));
        textPanel.add(bMechLabel = new JTextField());
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
        JButton jb1 = new JButton("Add");
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

    public HashMap getDCElements()
    {
      HashMap elements = new HashMap();
      int rowcount = dcTable.getModel().getRowCount();
      System.out.println("dcTable rowcount=" + rowcount);
      for (int i=0; i<rowcount; i++)
      {
        if (dcTable.getValueAt(i,0) != null && dcTable.getValueAt(i,0) != "")
        {
          elements.put(dcTable.getValueAt(i,0), dcTable.getValueAt(i,1));
        }
      }
      return elements;
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

    private int[] getSelectedRows(JTable table)
    {
        // Ensure that Row selection is enabled
        if (!table.getColumnSelectionAllowed()
              && table.getRowSelectionAllowed())
        {
          // Get the indices of the selected rows
          return table.getSelectedRows();
        }
        return new int[0];
    }
}
package fedora.client.bmech;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.ButtonGroup;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.JComponent;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Collection;
import java.util.Vector;

import fedora.client.bmech.data.*;

public class MethodPropertiesDialog extends JDialog
{
    private MethodsPane parent;
    private String methodName;
    private JTable parmTable;
    private JRadioButton rb_http;
    private JTextField rb_http_URL;
    private JRadioButton rb_soap;

    // These are the combobox values
    private String[] parmTypes = new String[]{"USER", "DEFAULT", "DATASTREAM"};
    private String[] parmReq = new String[] {"YES", "NO"};
    private String[] parmPassBy = new String[] {"REF", "VALUE"};


    public MethodPropertiesDialog(MethodsPane parent, String methodName,
      MethodProperties methodProperties)
    {
        this.parent = parent;
        this.methodName = methodName;
        renderCurrentProperties(methodProperties);
        setTitle("Method Properties");
        setSize(600, 300);
        setModal(true);
        getContentPane().setLayout(new BorderLayout());

        // Method Binding Panel
        rb_http = new JRadioButton("HTTP Binding URL: ", true);
        rb_http.setActionCommand("http");
        // http url entry box
        rb_http_URL = new JTextField(30);
        rb_http_URL.setToolTipText("Enter the full URL for the service method." +
          "The URL should use the replacement syntax described in Help.");
        // soap checkbox
        rb_soap = new JRadioButton("SOAP Binding (auto-generated)", false);
        rb_soap.setActionCommand("soap");
        ButtonGroup rb_buttonGroup = new ButtonGroup();
        rb_buttonGroup.add(rb_http);
        rb_buttonGroup.add(rb_soap);
        JPanel bindingPanel = new JPanel();
        bindingPanel.setBorder(new TitledBorder("Method Binding"));
        bindingPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(2,2,2,2);
        gbc.gridy = 0;
        gbc.gridx = 0;
        bindingPanel.add(rb_http, gbc);
        gbc.gridx = 1;
        bindingPanel.add(rb_http_URL, gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        bindingPanel.add(rb_soap, gbc);

        // User Parms Table Panel
        parmTable = new JTable(10,7);
        parmTable.setColumnSelectionAllowed(false);
        parmTable.setRowSelectionAllowed(true);
        parmTable.setRowHeight(18);

        TableColumn tc0 = parmTable.getColumnModel().getColumn(0);
        tc0.setHeaderValue("Parm Name");
        // Set the ParmType column to be rendered and edited with JComboBox
        TableColumn tc1 = parmTable.getColumnModel().getColumn(1);
        tc1.setHeaderValue("Parm Type");
        tc1.setCellRenderer(new ComboBoxRenderer(parmTypes));
        tc1.setCellEditor(new ComboBoxTableCellEditor(parmTypes));
        // Set the ParmReq column to be rendered and edited with JComboBox
        TableColumn tc2 = parmTable.getColumnModel().getColumn(2);
        tc2.setHeaderValue("Parm Req'd");
        tc2.setCellRenderer(new ComboBoxRenderer(parmReq));
        tc2.setCellEditor(new ComboBoxTableCellEditor(parmReq));
        // Set the PassBy column to be rendered and edited with JComboBox
        TableColumn tc3 = parmTable.getColumnModel().getColumn(3);
        tc3.setHeaderValue("Pass By");
        tc3.setCellRenderer(new ComboBoxRenderer(parmPassBy));
        tc3.setCellEditor(new ComboBoxTableCellEditor(parmPassBy));
        TableColumn tc4 = parmTable.getColumnModel().getColumn(4);
        tc4.setHeaderValue("Default Value");
        TableColumn tc5 = parmTable.getColumnModel().getColumn(5);
        tc5.setHeaderValue("Description");
        TableColumn tc6 = parmTable.getColumnModel().getColumn(6);
        tc6.setHeaderValue("Valid Values");

        JScrollPane scrollpane = new JScrollPane(parmTable);
        scrollpane.setColumnHeaderView(parmTable.getTableHeader());
        scrollpane.getViewport().setBackground(Color.white);

        // Table Buttons Panel
        JButton jb1 = new JButton("Add");
        jb1.setMinimumSize(new Dimension(100,30));
        jb1.setMaximumSize(new Dimension(100,30));
        jb1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addTableRow();
          }
        } );
        JButton jb2 = new JButton("Delete");
        jb2.setMinimumSize(new Dimension(100,30));
        jb2.setMaximumSize(new Dimension(100,30));
        jb2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            deleteTableRow();
          }
        } );
        JButton jb3 = new JButton("Help");
        jb3.setMinimumSize(new Dimension(100,30));
        jb3.setMaximumSize(new Dimension(100,30));
        jb3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            helpTable();
          }
        } );

        JPanel t_buttonPanel = new JPanel();
        //t_buttonPanel.setLayout(new GridLayout(3,1));
        t_buttonPanel.setLayout(new BoxLayout(t_buttonPanel, BoxLayout.Y_AXIS));
        t_buttonPanel.add(jb1);
        t_buttonPanel.add(jb2);
        t_buttonPanel.add(jb3);


        // Dialog Buttons Panel
        JButton done = new JButton("Save");
        done.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            saveProperties();
          }
        } );
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            cancelProperties();
          }
        } );
        JPanel mainButtonPanel = new JPanel();
        mainButtonPanel.add(done);
        mainButtonPanel.add(cancel);

        getContentPane().add(bindingPanel, BorderLayout.NORTH);
        getContentPane().add(scrollpane, BorderLayout.CENTER);
        getContentPane().add(t_buttonPanel, BorderLayout.EAST);
        getContentPane().add(mainButtonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void addTableRow()
    {
      // Append a row
      ((DefaultTableModel)parmTable.getModel()).addRow(
        new Object[]{"", "", "", "", ""});
    }

    private void deleteTableRow()
    {
      ((DefaultTableModel)parmTable.getModel()).removeRow(parmTable.getSelectedRow());
    }

    private void helpTable()
    {
      return;
    }

    private void saveProperties()
    {
      System.out.println("MethodPropertiesDialog.saveProperties: " +
        "Saving values back in parent");
      parent.setBMechMethodProperties(methodName, getMethodProperties());
      //((MethodsPane)this.getParent()).setBMechMethodProperties(methodName, getMethodProperties());
      if (validMethodProperties())
      {
        setVisible(false);
        dispose();
      }
    }

    private void cancelProperties()
    {
      setVisible(false);
      dispose();
    }

    private boolean validMethodProperties()
    {
      if (rb_http.isSelected())
      {
        if (rb_http_URL.getText().equals(""))
        {
          parent.assertMethodPropertiesMsg("You must enter an HTTP binding URL for method!");
          return false;
        }
      }
      return true;
    }

    private MethodProperties getMethodProperties()
    {
      MethodProperties mp = new MethodProperties();
      if (rb_http.isSelected())
      {
        mp.methodFullURL = rb_http_URL.getText().trim();
        mp.methodRelativeURL = null;
        mp.protocolType = "HTTP";
      }
      else
      {
        mp.protocolType = "SOAP";
      }

      mp.methodParms = getMethodParms();
      mp.dsBindingKeys = getDSBindingKeys(mp.methodParms);
      return mp;
    }

    private String[] getDSBindingKeys(MethodParm[] parms)
    {
      Vector dsbindkeys = new Vector();
      for (int i=0; i<parms.length; i++)
      {
        if (parms[i].parmType.equalsIgnoreCase("DATASTREAM"))
        {
          dsbindkeys.add(parms[i].parmName);
        }
      }
      return (String[])dsbindkeys.toArray(new String[0]);
    }

    private MethodParm[] getMethodParms()
    {
      HashMap parmMap = new HashMap();
      int rowcount = parmTable.getModel().getRowCount();
      System.out.println("parmTable rowcount=" + rowcount);
      for (int i=0; i<rowcount; i++)
      {
        if (parmTable.getValueAt(i,0) != null && parmTable.getValueAt(i,0) != "")
        {
          MethodParm parm = new MethodParm();
          parm.parmName = (String)parmTable.getValueAt(i,0);
          parm.parmType = (String)parmTable.getValueAt(i,1);
          parm.parmRequired = (String)parmTable.getValueAt(i,2);
          parm.parmPassBy = (String)parmTable.getValueAt(i,3);
          parm.parmDefaultValue = (String)parmTable.getValueAt(i,4);
          parm.parmLabel = (String)parmTable.getValueAt(i,5);
          parm.parmDomainValues = new String[0];
          parmMap.put(parm.parmName, parm);
        }
      }
      return (MethodParm[])parmMap.values().toArray(new MethodParm[0]);
    }

    private void renderCurrentProperties(MethodProperties properties)
    {

    }

}
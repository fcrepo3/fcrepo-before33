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
import javax.swing.JOptionPane;
import javax.swing.ButtonGroup;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Collection;
import java.util.Vector;
import java.util.StringTokenizer;

import fedora.client.bmech.data.*;

/**
 *
 * <p><b>Title:</b> MethodPropertiesDialog.java</p>
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
public class MethodPropertiesDialog extends JDialog
{
    private MethodsPane parent;
    private String builderClassName;
    private String methodName;
    private String baseURL;
    private JTable parmTable;
    private JRadioButton rb_http;
    private JTextField rb_http_URL;
    private JRadioButton rb_httpRelative;
    private JTextField rb_httpRelative_URL;
    private JRadioButton rb_soap;
    private JTextField returnMIMES;

    // The data structure that is populated by this dialog.
    private MethodProperties mp;

    // These are used to load JComboBox table columns
    private HashMap parmTypeTbl = new HashMap();
    private HashMap parmTypeToDisplayTbl = new HashMap();
    private void loadParmTypeTbl()
    {
      parmTypeTbl.put("USER", MethodParm.USER_INPUT);
      parmTypeTbl.put("DATASTREAM", MethodParm.DATASTREAM_INPUT);
      parmTypeTbl.put("DEFAULT", MethodParm.DEFAULT_INPUT);
    }
    private void loadParmTypeToDisplayTbl()
    {
      parmTypeToDisplayTbl.put(MethodParm.USER_INPUT, "USER");
      parmTypeToDisplayTbl.put(MethodParm.DATASTREAM_INPUT, "DATASTREAM");
      parmTypeToDisplayTbl.put(MethodParm.DEFAULT_INPUT, "DEFAULT");
    }
    private HashMap passByTbl = new HashMap();
    private HashMap passByToDisplayTbl = new HashMap();
    private void loadPassByTbl()
    {
      passByTbl.put("URL_REF", MethodParm.PASS_BY_REF);
      passByTbl.put("VALUE", MethodParm.PASS_BY_VALUE);
    }
    private void loadPassByToDisplayTbl()
    {
      passByToDisplayTbl.put(MethodParm.PASS_BY_REF, "URL_REF");
      passByToDisplayTbl.put(MethodParm.PASS_BY_VALUE, "VALUE");
    }
    private HashMap parmReqTbl = new HashMap();
    private HashMap parmReqToDisplayTbl = new HashMap();
    private void loadParmReqTbl()
    {
      parmReqTbl.put("YES", "true");
      parmReqTbl.put("NO", "false");
    }
    private void loadParmReqToDisplayTbl()
    {
      parmReqToDisplayTbl.put("true", "YES");
      parmReqToDisplayTbl.put("false", "NO");
    }
    //private String[] parmReq = new String[] {"YES", "NO"};


    public MethodPropertiesDialog(MethodsPane parent, String methodName,
      MethodProperties methodProperties)
    {
        loadParmTypeTbl();
        loadParmTypeToDisplayTbl();
        loadPassByTbl();
        loadPassByToDisplayTbl();
        loadParmReqTbl();
        loadParmReqToDisplayTbl();
        this.parent = parent;
        this.builderClassName = parent.parent.getClass().getName();
        this.methodName = methodName;

        setTitle("Method Properties for: " + methodName);
        setSize(800, 400);
        setModal(true);
        getContentPane().setLayout(new BorderLayout());

        // Service Binding Panel
        JPanel bindingPanel = null;
        if (builderClassName.equalsIgnoreCase("fedora.client.bmech.BMechBuilder"))
        {
          this.baseURL = parent.getBaseURL();
          if (parent.hasBaseURL())
          {
            this.baseURL = (parent.getBaseURL().endsWith("/"))
              ? parent.getBaseURL() : (parent.getBaseURL() + "/");
          }
          bindingPanel = setBindingPanel();
        }

        // Method Parms Panel
        JPanel parmsPanel = setParmsPanel();

        // Method Return Panel
        JPanel returnPanel = setMethodReturnPanel();

        // Dialog Buttons Panel
        JButton done = new JButton("OK");
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

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new BorderLayout());
        if (builderClassName.equalsIgnoreCase("fedora.client.bmech.BMechBuilder"))
        {
          lowerPanel.add(returnPanel, BorderLayout.NORTH);
        }
        lowerPanel.add(mainButtonPanel, BorderLayout.SOUTH);

        if (builderClassName.equalsIgnoreCase("fedora.client.bmech.BMechBuilder"))
        {
          getContentPane().add(bindingPanel, BorderLayout.NORTH);
        }
        getContentPane().add(parmsPanel, BorderLayout.CENTER);
        getContentPane().add(lowerPanel, BorderLayout.SOUTH);
        renderCurrentProperties(methodProperties);
        setVisible(true);
    }

    private JPanel setBindingPanel()
    {
        // Method Binding Panel
        // http full URL
        rb_http = new JRadioButton("HTTP URL: ", false);
        rb_http.setActionCommand("http");
        rb_http_URL = new JTextField(30);
        rb_http_URL.setToolTipText("Enter the full URL for the service method." +
          "The URL should use the replacement syntax described in Help.");
        // http relative URL
        String rLabel = "HTTP URL (relative):     " + baseURL;
        rb_httpRelative = new JRadioButton(rLabel, false);
        rb_httpRelative.setActionCommand("httprel");
        // soap checkbox
        rb_soap = new JRadioButton("SOAP Binding (auto-generated)", false);
        rb_soap.setActionCommand("soap");
        rb_soap.setEnabled(false);
        ButtonGroup rb_buttonGroup = new ButtonGroup();
        if (parent.hasBaseURL())
        {
          rb_httpRelative.setSelected(true);
          rb_buttonGroup.add(rb_httpRelative);
        }
        else
        {
          rb_http.setSelected(true);
          rb_buttonGroup.add(rb_http);
        }
        rb_buttonGroup.add(rb_soap);
        JPanel bindingPanel = new JPanel();
        bindingPanel.setBorder(new TitledBorder("Method Binding:"));
        bindingPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15,2,2,2);
        gbc.gridy = 0;
        gbc.gridx = 0;
        if (parent.hasBaseURL())
        {
          bindingPanel.add(rb_httpRelative, gbc);
        }
        else
        {
          bindingPanel.add(rb_http, gbc);
        }
        gbc.gridx = 1;
        bindingPanel.add(rb_http_URL, gbc);
        gbc.insets = new Insets(2,2,15,2);
        gbc.gridy = 1;
        gbc.gridx = 0;
        bindingPanel.add(rb_soap, gbc);
        return bindingPanel;
    }

    private JPanel setParmsPanel()
    {
        // Parms Table Panel
        parmTable = new JTable(20,7);
        parmTable.setColumnSelectionAllowed(false);
        parmTable.setRowSelectionAllowed(true);
        parmTable.setRowHeight(18);

        TableColumn tc0 = parmTable.getColumnModel().getColumn(0);
        tc0.setHeaderValue("Parm Name");
        // Set the ParmType column to be rendered and edited with JComboBox
        TableColumn tc1 = parmTable.getColumnModel().getColumn(1);
        tc1.setHeaderValue("Parm Type");
        tc1.sizeWidthToFit();
        String[] parmTypes;
        if (builderClassName.equalsIgnoreCase("fedora.client.bmech.BMechBuilder"))
        {
          parmTypes = (String[])parmTypeTbl.keySet().toArray(new String[0]);
        }
        else
        {
          parmTypes = new String[] {(String)
            parmTypeToDisplayTbl.get(MethodParm.USER_INPUT)};
        }
        tc1.setCellRenderer(new ComboBoxRenderer(parmTypes));
        tc1.setCellEditor(new ComboBoxTableCellEditor(parmTypes));
        // Set the ParmReq column to be rendered and edited with JComboBox
        TableColumn tc2 = parmTable.getColumnModel().getColumn(2);
        tc2.setHeaderValue("Required?");
        String[] parmReq = (String[])parmReqTbl.keySet().toArray(new String[0]);
        tc2.setCellRenderer(new ComboBoxRenderer(parmReq));
        tc2.setCellEditor(new ComboBoxTableCellEditor(parmReq));
        // Set the PassBy column to be rendered and edited with JComboBox
        TableColumn tc3 = parmTable.getColumnModel().getColumn(3);
        tc3.setHeaderValue("Pass By");
        String[] parmPassBy = (String[])passByTbl.keySet().toArray(new String[0]);
        //String[] parmPassBy = new String[] {"A", "B"};
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
        t_buttonPanel.setLayout(new BoxLayout(t_buttonPanel, BoxLayout.Y_AXIS));
        t_buttonPanel.add(jb1);
        t_buttonPanel.add(jb2);
        t_buttonPanel.add(jb3);

        JPanel parmsPanel = new JPanel(new BorderLayout());
        parmsPanel.setBorder(new TitledBorder("Method Parameter Definitions:"));
        parmsPanel.add(scrollpane, BorderLayout.CENTER);
        parmsPanel.add(t_buttonPanel, BorderLayout.EAST);
        return parmsPanel;
    }

    private JPanel setMethodReturnPanel()
    {
      // Method Return Types Panel
      returnMIMES = new JTextField(30);
      JPanel returnPanel = new JPanel();
      returnPanel.setBorder(new TitledBorder("Method Return Types:"));
      returnPanel.setLayout(new GridBagLayout());
      GridBagConstraints gbc2 = new GridBagConstraints();
      gbc2.anchor = GridBagConstraints.WEST;
      gbc2.insets = new Insets(15,2,15,2);
      gbc2.gridy = 0;
      gbc2.gridx = 0;
      returnPanel.add(new JLabel("MIME types (comma delimit):"), gbc2);
      gbc2.gridx = 1;
      returnPanel.add(returnMIMES, gbc2);
      return returnPanel;
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
      setMethodProperties();
      MethodProperties properties = getMethodProperties();
      if (validMethodProperties(properties))
      {
        if (builderClassName.equalsIgnoreCase("fedora.client.bmech.BMechBuilder"))
        {
          mp.dsBindingKeys = setDSBindingKeys(properties.methodParms);
        }
        parent.setMethodProperties(methodName, mp);
        setVisible(false);
        dispose();
      }
    }

    private void cancelProperties()
    {
      setVisible(false);
      dispose();
    }

    private boolean parmsInURL(String http_URL, MethodParm[] parms)
    {
      for (int i=0; i<parms.length; i++)
      {
        String pattern = "\\(" + parms[i].parmName  + "\\)";
        if (!(foundInString(http_URL, pattern)))
        {
          System.out.println("str: " + pattern + " not found in: " + http_URL);
          return false;
        }
      }
      System.out.println("All parms found within URL: " + http_URL);
      return true;
    }

    private boolean foundInString(String inputString, String patternString)
    {
      Pattern pattern = Pattern.compile(patternString);
      Matcher m = pattern.matcher(inputString);
      return m.find();
    }

    private boolean validMethodProperties(MethodProperties mp)
    {
      System.out.println("MethodPropertiesDialog: validating method properties...");
      return (validBinding(mp)
              && validMethodParms(mp.methodParms)
              && validReturnTypes(mp.returnMIMETypes))
        ? true : false;
    }

    private boolean validReturnTypes(String[] mimeTypes)
    {
      if (builderClassName.equalsIgnoreCase("fedora.client.bmech.BMechBuilder"))
      {
        if (mimeTypes.length <= 0)
        {
            assertMethodPropertiesMsg("You must enter an at least one return MIME type for method!");
            return false;
        }
      }
      return true;

    }

    private boolean validBinding(MethodProperties mp)
    {
      if (builderClassName.equalsIgnoreCase("fedora.client.bmech.BMechBuilder"))
      {
        if (mp.protocolType.equalsIgnoreCase(mp.HTTP_MESSAGE_PROTOCOL))
        {
          if (mp.methodFullURL == null || mp.methodFullURL.trim().equals(""))
          {
            assertMethodPropertiesMsg("You must enter an HTTP binding URL for method!");
            return false;
          }
          else if (mp.methodRelativeURL.startsWith("http://"))
          {
            assertMethodPropertiesMsg("A relative URL cannot begin with http://");
            return false;
          }
          else if (!(parmsInURL(mp.methodFullURL, mp.methodParms)))
          {
            assertMethodPropertiesMsg("A parm from the parm table is not "
              + "encoded in the HTTP URL. See Help for URL replacement syntax.");
            return false;
          }
        }
        else if (mp.protocolType.equalsIgnoreCase(mp.SOAP_MESSAGE_PROTOCOL))
        {
            assertMethodPropertiesMsg("Sorry, the SOAP bindings are not supported yet."
              + " Please select HTTP binding.");
            return false;
        }
      }
      return true;
    }

    private boolean validMethodParms(MethodParm[] parms)
    {
      for (int i=0; i<parms.length; i++)
      {
        if (!validMethodParm(parms[i]))
        {
          return false;
        }
      }
      System.out.println("Method parms are valid.");
      return true;
    }

    private boolean validMethodParm(MethodParm parm)
    {
      if (parm.parmType == null || parm.parmType.trim().equals(""))
      {
        assertMethodPropertiesMsg("A value for 'Parm Type' must be selected for parm "
          + parm.parmName);
        return false;
      }
      else if (parm.parmRequired == null || parm.parmRequired.trim().equals(""))
      {
        assertMethodPropertiesMsg("A value for 'Required?' must be selected for parm "
          + parm.parmName);
        return false;
      }
      else if (parm.parmPassBy == null || parm.parmPassBy.trim().equals(""))
      {
        assertMethodPropertiesMsg("A value for 'Pass By' must be selected for parm "
          + parm.parmName);
        return false;
      }
      else if ( parm.parmType.equalsIgnoreCase(parm.DATASTREAM_INPUT)
        && parm.parmPassBy.equalsIgnoreCase(parm.PASS_BY_VALUE))
      {
        assertMethodPropertiesMsg("'Pass By' must be URL_REF "
          + "when 'Parm Type' is fedora:datastreamInputType.");
        return false;
      }
      return true;
    }

    private MethodProperties getMethodProperties()
    {
      return mp;
    }

    private void setMethodProperties()
    {
      mp = new MethodProperties();
      if (builderClassName.equalsIgnoreCase("fedora.client.bmech.BMechBuilder"))
      {
        if (rb_http.isSelected())
        {
          mp.methodFullURL = rb_http_URL.getText();
          mp.methodRelativeURL = mp.methodFullURL;
          mp.protocolType = mp.HTTP_MESSAGE_PROTOCOL;
        }
        else if (rb_httpRelative.isSelected())
        {
          mp.methodRelativeURL = rb_http_URL.getText();
          // Get rid of forward slash if exists since the baseURL is
          // forced to end in a forward slash.
          if (mp.methodRelativeURL.startsWith("/"))
          {
            mp.methodRelativeURL = mp.methodRelativeURL.substring(1);
          }
          mp.methodFullURL = baseURL + mp.methodRelativeURL;
          mp.protocolType = mp.HTTP_MESSAGE_PROTOCOL;
        }
        else
        {
          mp.protocolType = mp.SOAP_MESSAGE_PROTOCOL;
        }
      }
      mp.returnMIMETypes = unloadReturnTypes();
      mp.methodParms = unloadMethodParms();
      return;
    }

    private String[] setDSBindingKeys(MethodParm[] parms)
    {
      Vector dsbindkeys = new Vector();
      for (int i=0; i<parms.length; i++)
      {
        if (parms[i].parmType.equalsIgnoreCase(MethodParm.DATASTREAM_INPUT))
        {
          dsbindkeys.add(parms[i].parmName);
        }
      }
      return (String[])dsbindkeys.toArray(new String[0]);
    }

    private MethodParm[] unloadMethodParms()
    {
      if (parmTable.isEditing())
      {
        parmTable.getCellEditor().stopCellEditing();
      }
      HashMap parmMap = new HashMap();
      int rowcount = parmTable.getModel().getRowCount();
      //System.out.println("parmTable rowcount=" + rowcount);
      for (int i=0; i<rowcount; i++)
      {
        if (parmTable.getValueAt(i,0) != null && parmTable.getValueAt(i,0) != "")
        {
          MethodParm parm = new MethodParm();
          parm.parmName = ((String)parmTable.getValueAt(i,0));
          parm.parmType = ((String)parmTypeTbl.get(parmTable.getValueAt(i,1)));
          parm.parmRequired = ((String)parmReqTbl.get(parmTable.getValueAt(i,2)));
          parm.parmPassBy = ((String)passByTbl.get(parmTable.getValueAt(i,3)));
          parm.parmDefaultValue = ((String)parmTable.getValueAt(i,4));
          parm.parmLabel = ((String)parmTable.getValueAt(i,5));

          Vector domainValues = new Vector();
          String values;
          if ((values = (String)parmTable.getValueAt(i,6)) != null)
          {
            StringTokenizer st = new StringTokenizer(values, ",");
            //System.out.println("count domain parms = " + st.countTokens());
            while (st.hasMoreElements())
            {
              domainValues.add(((String)st.nextElement()).trim());
            }
            parm.parmDomainValues = (String[])domainValues.toArray(new String[0]);
          }
          parmMap.put(parm.parmName, parm);
        }
      }
      return (MethodParm[])parmMap.values().toArray(new MethodParm[0]);
    }

    private String[] unloadReturnTypes()
    {
      Vector mimeTypes = new Vector();
      StringTokenizer st = new StringTokenizer(returnMIMES.getText(), ",");
      while (st.hasMoreElements())
      {
        mimeTypes.add(((String)st.nextElement()).trim());
      }
      return (String[])mimeTypes.toArray(new String[0]);
    }

    private void renderCurrentProperties(MethodProperties properties)
    {
      if (properties == null)
      {
        return;
      }
      if (properties.protocolType.equalsIgnoreCase(Method.HTTP_MESSAGE_PROTOCOL))
      {
        rb_http.setSelected(true);
        if (parent.hasBaseURL())
        {
          rb_http_URL.setText(properties.methodRelativeURL);
        }
        else
        {
          rb_http_URL.setText(properties.methodFullURL);
        }

        rb_soap.setSelected(false);
      }
      else if (properties.protocolType.equalsIgnoreCase(Method.SOAP_MESSAGE_PROTOCOL))
      {
        rb_http.setSelected(false);
        rb_http_URL.setText("");
        rb_soap.setSelected(true);
      }

      // render the existing return MIME types
      StringBuffer sb = new StringBuffer();
      //System.out.println("count mime: " + properties.returnMIMETypes.length);
      for (int i=0; i<properties.returnMIMETypes.length; i++)
      {
        sb.append(properties.returnMIMETypes[i]);
        int j = i+1;
        if (!(j == properties.returnMIMETypes.length))
        {
          sb.append(",");
        }
      }
      returnMIMES.setText(sb.toString());

      // render the existing method parms
      MethodParm[] parms = properties.methodParms;
      for (int i=0; i<parms.length; i++)
      {
        // make sure we have enough rows for the parms.
        int freeRows = parmTable.getRowCount();
        if (parms.length > freeRows)
        {
          int newRows = parms.length - freeRows;
          for (int j=0; j<newRows; j++)
          {
            ((DefaultTableModel)parmTable.getModel()).addRow(
              new Object[]{"", "", "", "", ""});
          }
        }

        TableCellEditor ce;
        // load existing parms into table
        parmTable.setValueAt(parms[i].parmName, i, 0);

        ce = parmTable.getCellEditor(i, 1);
        ce.getTableCellEditorComponent(
          parmTable, parmTypeToDisplayTbl.get(parms[i].parmType), true, i, 1);
        parmTable.setValueAt(ce.getCellEditorValue(), i, 1);

        ce = parmTable.getCellEditor(i, 2);
        ce.getTableCellEditorComponent(
          parmTable, parmReqToDisplayTbl.get(parms[i].parmRequired), true, i, 2);
        parmTable.setValueAt(ce.getCellEditorValue(), i, 2);

        ce = parmTable.getCellEditor(i, 3);
        ce.getTableCellEditorComponent(
          parmTable, passByToDisplayTbl.get(parms[i].parmPassBy), true, i, 3);
        parmTable.setValueAt(ce.getCellEditorValue(), i, 3);

        parmTable.setValueAt(parms[i].parmDefaultValue, i, 4);
        parmTable.setValueAt(parms[i].parmLabel, i, 5);

        // render the existing domain values
        StringBuffer sb2 = new StringBuffer();
        //System.out.println("count values: " + parms[i].parmDomainValues.length);
        for (int i2=0; i2<parms[i].parmDomainValues.length; i2++)
        {
          sb2.append(parms[i].parmDomainValues[i2]);
          int j = i+1;
          if (!(j == parms[i].parmDomainValues.length))
          {
            sb2.append(",");
          }
        }
        parmTable.setValueAt(sb2.toString(), i, 6);
      }
    }

    private void assertMethodPropertiesMsg(String msg)
    {
      JOptionPane.showMessageDialog(
        this, new String(msg), "Method Properties Message",
        JOptionPane.INFORMATION_MESSAGE);
    }
}
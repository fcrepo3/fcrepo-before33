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
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Collection;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.HashMap;

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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
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

    private JRadioButton rb_httpRelative;
    private JRadioButton rb_httpFull;
    private JRadioButton rb_httpDS;
    private JRadioButton rb_soap;

    private JTextField URL_textRelative;
    private JTextField URL_textFull;
    private JTextField URL_textDS;

    private JTextField returnMIMES;
    private JButton helpURL;

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
        setSize(850, 400);
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

        helpURL = new JButton("Help");
        helpURL.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            showURLHelp();
          }
        } );

        // http just binding key
        rb_httpDS = new JRadioButton("Fedora LOCAL HTTP Resolver: (enter datastream parm name)", false);
        rb_httpDS.setActionCommand("http");
        URL_textDS = new JTextField(30);
        URL_textDS.setToolTipText("Enter the datastream parameter name in parentheses."
          + "  See Help button for more details on syntax.");

        // http full URL
        rb_httpFull = new JRadioButton("Multi-Server Service: (enter full URL for method)", false);
        rb_httpFull.setActionCommand("httpFull");
        URL_textFull = new JTextField(30);
        URL_textFull.setToolTipText("Enter the full URL for the service method." +
          " The URL should use the replacement syntax described in the Help button.");

        // http relative URL
        String rLabel = "HTTP URL (relative):       " + baseURL;
        rb_httpRelative = new JRadioButton(rLabel, false);
        rb_httpRelative.setActionCommand("httprel");
        URL_textRelative = new JTextField(30);
        URL_textRelative.setToolTipText("Enter relative URL for the service method"
          + "  See Help button for more details on syntax.");

        // soap checkbox
        rb_soap = new JRadioButton("SOAP Binding (auto-generated)", false);
        rb_soap.setActionCommand("soap");
        rb_soap.setEnabled(false);
        ButtonGroup rb_buttonGroup = new ButtonGroup();

        // add one of the three variants for the service
        if (parent.hasBaseURL())
        {
          rb_httpRelative.setSelected(true);
          rb_buttonGroup.add(rb_httpRelative);
        }
        else if (parent.isMultiServer())
        {
          rb_httpFull.setSelected(true);
          rb_buttonGroup.add(rb_httpFull);
        }
        else if (parent.isLocalHTTP())
        {
          rb_httpDS.setSelected(true);
          rb_buttonGroup.add(rb_httpDS);
        }
        // add the soap variant
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
        else if (parent.isMultiServer())
        {
          bindingPanel.add(rb_httpFull, gbc);
        }
        else if (parent.isLocalHTTP())
        {
          bindingPanel.add(rb_httpDS, gbc);
        }
        gbc.gridx = 1;
        if (parent.hasBaseURL())
        {
          bindingPanel.add(URL_textRelative, gbc);
        }
        else if (parent.isMultiServer())
        {
          bindingPanel.add(URL_textFull, gbc);
        }
        else if (parent.isLocalHTTP())
        {
          bindingPanel.add(URL_textDS, gbc);
        }
        gbc.gridx = 2;
        bindingPanel.add(helpURL, gbc);
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
        tc6.setHeaderValue("Valid Values (a,b)");

        JScrollPane scrollpane = new JScrollPane(parmTable);
        scrollpane.setColumnHeaderView(parmTable.getTableHeader());
        scrollpane.getViewport().setBackground(Color.white);

        // Table Buttons Panel
        JButton jb1 = new JButton("AddRow");
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

    private void showURLHelp()
    {
        JTextArea helptxt = new JTextArea();
        helptxt.setLineWrap(true);
        helptxt.setWrapStyleWord(true);
        helptxt.setBounds(0,0,550,20);
        helptxt.append("The HTTP method binding URL must represent the exact"
          + " URL syntax necessary to run the method on the target service. "
          + " Fedora uses the URL replacement syntax defined in the WSDL 1.1"
          + " specification (see http://www.w3.org/TR/wsdl) to encode "
          + " placeholders in the URL for parameter values.\n\n");
        helptxt.append("A placeholder is"
          + " created by putting the Fedora-specific parameter name"
          + " (defined in table below) inside parentheses, and inserting this"
          + " within the URL at the spot where a parameter value should be. "
          + " At runtime, Fedora will insert actual values in the URL where"
          + " the placeholders exist.  The values will either be obtained from a user"
          + " or acquired by the system.  In the case of Datastream inputs to"
          + " a method at runtime, Fedora determined which datastream in an"
          + " object is tagged with the Fedora-specific parameter name,"
          + " and will insert a value (i.e., the Fedora callback URL for the"
          + " datastream) into the parameter value.\n\n");
        helptxt.append("Use the following syntax to encode your parameters"
          + " within the method binding URL:\n\n");
        helptxt.append("http://myserver.com/myservice/mymethod?myparm1=(parmname1)&myparm2=(parmname2)\n\n");
        helptxt.append("For example:\n");
        helptxt.append("http://localhost:8080/imgsizer/resize_image?image=(IMGDATASTREAM)&size=(USERSIZE)\n\n");
        helptxt.append("SPECIAL CASE:\n");
        helptxt.append("When the option to use Fedora LOCAL HTTP Resolver"
          + " is selected, you do not need to enter a URL.  This option"
          + " means that there is no independent service being called to"
          + " run the method, instead, Fedora will just resolve a URL for"
          + " a datastream that is bound to the method.  In this case, all"
          + " that is required is that a parameter representing a datastream"
          + " be entered in parentheses.  The same parameter should be entered"
          + " in the method parameters table.\n\n");
        helptxt.append("Use the following syntax to encode your parameter"
          + " within the method binding URL box:\n\n");
        helptxt.append("(parmname1)\n\n");
        helptxt.append("For example:\n");
        helptxt.append("(HIGHRESIMAGE)\n\n");

        JOptionPane.showMessageDialog(
          this, helptxt, "Help for Method Binding URL",
          JOptionPane.OK_OPTION);
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
          if (parms[i].parmName.equalsIgnoreCase("NULLBIND"))
          {
            continue;
          }
          System.out.println("The parm named " + parms[i].parmName
            + " is not found in the HTTP method binding URL: " + http_URL);
          return false;
        }
      }
      System.out.println("All parms found within the"
        + " HTTP method bindingURL: " + http_URL);
      return true;
    }

    private boolean parmsFromURLMissing(String http_URL, MethodParm[] parms)
    {
      // set up
      Vector firstPassTokens = new Vector();
      Vector parmsInURL = new Vector();
      Vector tableParms = new Vector();
      for (int i=0; i<parms.length; i++)
      {
        tableParms.add(parms[i].parmName);
        System.out.println("Parm name in table: " + parms[i].parmName);
      }
      // find the encoded parm names in the HTTP binding URL
      StringTokenizer st1 = new StringTokenizer(http_URL, "(");
      while (st1.hasMoreTokens())
      {
        String fpt = st1.nextToken();
        System.out.println("First pass token " + fpt);
        firstPassTokens.add(fpt);
      }
      Iterator it = firstPassTokens.iterator();
      while (it.hasNext())
      {
        String token = (String)it.next();
        // if the token contains a ")" do further tokenization.
        // take just what's before the ")" and disgard the rest.
        Pattern pattern = Pattern.compile("\\)");
        Matcher m = pattern.matcher(token);
        if (m.find())
        {
          StringTokenizer st2 = new StringTokenizer(token, ")");
          while (st2.hasMoreTokens())
          {
              String s = st2.nextToken();
              parmsInURL.add(s);
              System.out.println("parm parsed out of HTTP method binding URL: " + s);
              break;
          }
        }
      }
      // check if the parms in the URL are found in the parm table
      Iterator allParmsInURL = parmsInURL.iterator();
      while (allParmsInURL.hasNext())
      {
        String parmInURL = (String)allParmsInURL.next();
        if (!(tableParms.contains(parmInURL)))
        {
          return false;
        }
      }
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
          else if (!(this.parmsFromURLMissing(mp.methodFullURL, mp.methodParms)))
          {
            assertMethodPropertiesMsg("The HTTP method binding URL has a parm"
              + " encoded within it that is not listed in the method parm table."
              + " See Help for URL replacement syntax.");
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
        if (rb_httpDS.isSelected())
        {
          mp.methodFullURL = URL_textDS.getText();
          mp.methodRelativeURL = mp.methodFullURL;
          mp.protocolType = mp.HTTP_MESSAGE_PROTOCOL;
        }
        else if (rb_httpFull.isSelected())
        {
          mp.methodFullURL = URL_textFull.getText();
          mp.methodRelativeURL = " ";
          mp.protocolType = mp.HTTP_MESSAGE_PROTOCOL;
        }
        else if (rb_httpRelative.isSelected())
        {
          mp.methodRelativeURL = URL_textRelative.getText();
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
        String parmName = (String)parmTable.getValueAt(i,0);
        //System.out.println("unload parm: " + i + " >>" + parmName);
        if (parmName != null)
        {
          //System.out.println("trim since parm not null at row " + i);
          parmName = parmName.trim();
        }
        if (parmName != null && !parmName.equalsIgnoreCase("")
          && !parmName.equalsIgnoreCase(" "))
        {
          //System.out.println("parm not null or spaces at row" + i);
          MethodParm parm = new MethodParm();
          parm.parmName = ((String)parmName);
          parm.parmType = ((String)parmTypeTbl.get(parmTable.getValueAt(i,1)));
          parm.parmRequired = ((String)parmReqTbl.get(parmTable.getValueAt(i,2)));
          parm.parmPassBy = ((String)passByTbl.get(parmTable.getValueAt(i,3)));
          parm.parmDefaultValue = ((String)parmTable.getValueAt(i,4));
          parm.parmLabel = ((String)parmTable.getValueAt(i,5));

          Vector domainValues = new Vector();
          String values;
          if ((values = (String)parmTable.getValueAt(i,6)) != null)
          {
            String normalizedString = normalizeString(values);
            StringTokenizer st = new StringTokenizer(normalizedString, ",");
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
      String normalizedString = normalizeString(returnMIMES.getText());
      StringTokenizer st = new StringTokenizer(normalizedString, ",");
      while (st.hasMoreElements())
      {
        mimeTypes.add(((String)st.nextElement()).trim());
      }
      return (String[])mimeTypes.toArray(new String[0]);
    }

    private String normalizeString(String values)
    {
      // make sure values are comma delimited
      String original = values.trim();
      Pattern spaces = Pattern.compile(" ++");
      Matcher m = spaces.matcher(original);
      String interim = m.replaceAll(",");
      Pattern multcommas = Pattern.compile(",++");
      Matcher m2 = multcommas.matcher(interim);
      String normal = m2.replaceAll(",");
      return normal;
    }

    private void renderCurrentProperties(MethodProperties properties)
    {
      if (properties == null)
      {
        return;
      }
      if (builderClassName.equalsIgnoreCase("fedora.client.bmech.BMechBuilder"))
      {
        renderBindingProperties(properties);
        renderReturnProperties(properties);
        renderParmProperties(properties);
      }
      else
      {
        renderParmProperties(properties);
      }
    }

    private void renderParmProperties(MethodProperties properties)
    {
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

    private void renderReturnProperties(MethodProperties properties)
    {
      // render the existing return MIME types
      StringBuffer sb = new StringBuffer();
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
    }

    private void renderBindingProperties(MethodProperties properties)
    {
      if (properties.protocolType.equalsIgnoreCase(Method.HTTP_MESSAGE_PROTOCOL))
      {
        //rb_http.setSelected(true);
        if (parent.hasBaseURL())
        {
          rb_httpRelative.setSelected(true);
          URL_textRelative.setText(properties.methodRelativeURL);

          rb_httpDS.setSelected(false);
          rb_httpFull.setSelected(false);

          URL_textDS.setEnabled(false);
          URL_textFull.setEnabled(false);
        }
        else if (parent.isMultiServer())
        {
          rb_httpFull.setSelected(true);
          URL_textFull.setText(properties.methodFullURL);

          rb_httpDS.setSelected(false);
          rb_httpRelative.setSelected(false);

          URL_textDS.setEnabled(false);
          URL_textRelative.setEnabled(false);
        }
        else if (parent.isLocalHTTP())
        {
          rb_httpDS.setSelected(true);
          URL_textDS.setText(properties.methodFullURL);

          rb_httpFull.setSelected(false);
          rb_httpRelative.setSelected(false);

          URL_textFull.setEnabled(false);
          URL_textRelative.setEnabled(false);
        }

        rb_soap.setSelected(false);
      }
      else if (properties.protocolType.equalsIgnoreCase(Method.SOAP_MESSAGE_PROTOCOL))
      {
        rb_httpDS.setSelected(false);
        rb_httpFull.setSelected(false);
        rb_httpRelative.setSelected(false);

        URL_textDS.setText("");
        URL_textFull.setText("");
        URL_textRelative.setText("");
        URL_textDS.setEnabled(false);
        URL_textFull.setEnabled(false);
        URL_textRelative.setEnabled(false);

        rb_soap.setSelected(true);
      }
    }

    private void assertMethodPropertiesMsg(String msg)
    {
      JOptionPane.showMessageDialog(
        this, new String(msg), "Method Properties Message",
        JOptionPane.INFORMATION_MESSAGE);
    }
}
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
import javax.swing.JTextArea;
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
 * <p><b>Title:</b> DatastreamInputPane.java</p>
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
public class DatastreamInputPane extends JPanel
{
    private BMechBuilder parent;
    private JTable dsinputTable;

    // The data structure that is populated by this panel.
    private DSInputRule[] dsInputSpec;

    private HashMap ordinalityTbl = new HashMap();
    private HashMap ordinalityToDisplayTbl = new HashMap();
    private void loadOrdinalityTbl()
    {
      ordinalityTbl.put("YES", "true");
      ordinalityTbl.put("NO", "false");
    }
    private void loadOrdinalityToDisplayTbl()
    {
      ordinalityToDisplayTbl.put("true", "YES");
      ordinalityToDisplayTbl.put("false", "NO");
    }

    public DatastreamInputPane(BMechBuilder parent)
    {
        this.parent = parent;
        loadOrdinalityTbl();
        loadOrdinalityToDisplayTbl();

        setLayout(new BorderLayout());
        setSize(800,400);

        // Text Area Panel
        JPanel textPanel = new JPanel();
        textPanel.setBorder(new TitledBorder("Datastream Input Specification"));
        JTextArea ta = new JTextArea("In the table below, define the " +
          "data contract for this behavior mechanism. " +
          "The data contract specifies what kinds of Datastreams must " +
          "reside in a Fedora Data Object that uses this mechanism. \n\n" +
          "Any method parameters of type 'DatastreamInput' that you entered " +
          "in the Service Methods tab will automatically appear in the table below. " );
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        ta.setBounds(0,0,500,20);
        textPanel.add(ta);
        //textPanel.setLayout(new GridLayout(6,2));

        // DS Input Table Panel
        dsinputTable = new JTable(0,7);
        dsinputTable.setColumnSelectionAllowed(false);
        dsinputTable.setRowSelectionAllowed(true);
        dsinputTable.setRowHeight(18);

        TableColumn tc0 = dsinputTable.getColumnModel().getColumn(0);
        tc0.setHeaderValue("Name");
        TableColumn tc1 = dsinputTable.getColumnModel().getColumn(1);
        tc1.setHeaderValue("MIMEType");
        tc1.sizeWidthToFit();
        TableColumn tc2 = dsinputTable.getColumnModel().getColumn(2);
        tc2.setHeaderValue("Min Occurs");
        tc2.sizeWidthToFit();
        TableColumn tc3 = dsinputTable.getColumnModel().getColumn(3);
        tc3.setHeaderValue("Max Occurs");
        tc3.sizeWidthToFit();
        // Set the Order Matters column to be rendered and edited with JComboBox
        TableColumn tc4 = dsinputTable.getColumnModel().getColumn(4);
        tc4.setHeaderValue("Ordered?");
        String[] orderReq = (String[])ordinalityTbl.keySet().toArray(new String[0]);
        tc4.setCellRenderer(new ComboBoxRenderer(orderReq));
        tc4.setCellEditor(new ComboBoxTableCellEditor(orderReq));
        TableColumn tc5 = dsinputTable.getColumnModel().getColumn(5);
        tc5.setHeaderValue("Pretty Label");
        tc5.sizeWidthToFit();
        TableColumn tc6 = dsinputTable.getColumnModel().getColumn(6);
        tc6.setHeaderValue("Other Details");
        tc6.sizeWidthToFit();

        JScrollPane scrollpane = new JScrollPane(dsinputTable);
        scrollpane.setColumnHeaderView(dsinputTable.getTableHeader());
        scrollpane.getViewport().setBackground(Color.white);

        JPanel dsinputPanel = new JPanel(new BorderLayout());
        dsinputPanel.setBorder(new TitledBorder("Datastream Input Parms:"));
        dsinputPanel.add(scrollpane, BorderLayout.CENTER);

        add(textPanel, BorderLayout.NORTH);
        add(dsinputPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    public void setDSBindingKeys(Vector dsBindingKeys)
    {
      String[] keys = (String[])dsBindingKeys.toArray(new String[0]);
      // make sure we have enough rows in the table
      int freeRows = dsinputTable.getRowCount();
      if (keys.length > freeRows)
      {
        int newRows = keys.length - freeRows;
        for (int j=0; j<newRows; j++)
        {
          ((DefaultTableModel)dsinputTable.getModel()).addRow(
            new Object[]{null, null, null, null, null, null, null});
        }
      }
      for (int i=0; i<keys.length; i++)
      {
        dsinputTable.setValueAt(keys[i], i, 0);
      }
    }

    public DSInputRule[] getDSInputRules()
    {
      if (dsinputTable.isEditing())
      {
        dsinputTable.getCellEditor().stopCellEditing();
      }
      Vector rules = new Vector();
      int rowcount = dsinputTable.getModel().getRowCount();
      if ((rowcount <=0) && (parent.newBMech.getDSBindingKeys().size() > 0))
      {
        this.assertDSInputMsg("You must enter information about "
          + "datastream input parms in the Datastream Input Tab.");
        return null;
      }
      for (int i=0; i<rowcount; i++)
      {
        DSInputRule dsRule = new DSInputRule();
        dsRule.bindingKeyName = (String)dsinputTable.getValueAt(i,0);
        // FIXIT!! Allow multiple MIME types?
        dsRule.bindingMIMEType = (String)dsinputTable.getValueAt(i,1);
        dsRule.minNumBindings = (String)dsinputTable.getValueAt(i, 2);
        dsRule.maxNumBindings = (String)dsinputTable.getValueAt(i, 3);
        dsRule.ordinality = (String)ordinalityTbl.get(dsinputTable.getValueAt(i,4));
        dsRule.bindingLabel = (String)dsinputTable.getValueAt(i,5);
        dsRule.bindingInstruction = (String)dsinputTable.getValueAt(i,6);
        if ((dsRule.bindingKeyName != null) &&
          !(dsRule.bindingKeyName.trim().equals("")))
        {
          rules.add(dsRule);
        }
      }
      return (DSInputRule[])rules.toArray(new DSInputRule[0]);
    }

    private void assertDSInputMsg(String msg)
    {
      JOptionPane.showMessageDialog(
        this, new String(msg), "DSInput Message",
        JOptionPane.INFORMATION_MESSAGE);
    }
}
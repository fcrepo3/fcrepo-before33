package fedora.client.bmech;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import fedora.client.bmech.data.DCElement;
import fedora.client.Administrator;
import fedora.client.objecteditor.Util;

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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
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
	private JComboBox bDefPIDComboBox;
    private String bDefPID;
    private String bDefLabel;
    protected JTextField bObjectPID;
    private JRadioButton rb_sysPID;
    private JRadioButton rb_retainPID;
    private final ButtonGroup rb_buttonGroup = new ButtonGroup();
    protected String rb_chosen;
    private JTextField bObjectLabel;
    private JTextField bObjectName;
    private JTable dcTable;
    protected DefaultTableModel dcTableModel;
	protected DCElementDialog dcDialog;
	private boolean editDCMode = false;
	protected String[] bDefOptions = new String[0];

    public GeneralPane(BMechBuilder parent)
    {
        this.parent = parent;
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2,1));
        topPanel.add(setDescriptionPanel());
		topPanel.add(setContractPanel());

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
          + " with the prefixes 'test:' or 'demo:' or a prefix you configured with your Fedora server."
          + " Examples PIDs are: 'demo:1', test:50, my-behaviors:75, myprefix:200");
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

	private JPanel setContractPanel()
	{
		JPanel contractPanel = new JPanel();
		contractPanel.setLayout(new GridBagLayout());
		contractPanel.setBorder(new TitledBorder("Behavior Definition Contract"));
		GridBagConstraints gbc2 = new GridBagConstraints();
		//gbc2.anchor = GridBagConstraints.WEST;
		gbc2.gridy = 0;
		gbc2.gridx = 0;
		contractPanel.add(
		  new JLabel("Behavior: "),
		  gbc2);

		// build dropdown of possible behaviors by getting a full
		// list of bDefs from the server.
		Map allBDefLabels = null;
		try
		{
			allBDefLabels=Util.getBDefLabelMap();
		} 
		catch (Exception e) 
		{
			JOptionPane.showMessageDialog(Administrator.getDesktop(),
					e.getMessage() + "\nError getting behavior definitions from repository!", 
					"Contact system administrator.",
					JOptionPane.ERROR_MESSAGE);
		}		
		Map bDefLabels=new HashMap();
		Iterator iter=allBDefLabels.keySet().iterator();
		while (iter.hasNext()) {
			String pid=(String) iter.next();
				bDefLabels.put(pid, (String) allBDefLabels.get(pid));
		}
		// set up the combobox 
		bDefOptions=new String[bDefLabels.keySet().size() + 1];
		if (bDefOptions.length==1) {
			bDefOptions[0]="No behavior definitions in repository!";
		} else {
			bDefOptions[0]="[Select a Behavior Definition]";
		}
		iter=bDefLabels.keySet().iterator();
		int i=1;
		while (iter.hasNext()) {
			String pid=(String) iter.next();
			String label=(String) bDefLabels.get(pid);
			bDefOptions[i++]=pid + " - " + label;
		}
		
		bDefPIDComboBox=new JComboBox(bDefOptions);		
		bDefPIDComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					String[] parts=
							((String) bDefPIDComboBox.getSelectedItem()).
									split(" - ");
					if (parts.length==1) {
						bDefPID=null;
						bDefLabel=null;
					} else {
						bDefPID=parts[0];
						bDefLabel=parts[1];
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(
							Administrator.getDesktop(),
							e.getMessage(), 
							"Error getting behavior definition",
							JOptionPane.ERROR_MESSAGE);
				}
				if (bDefPID != null)
				{
				  MethodsPane mp =
					(MethodsPane)((BMechBuilder)parent).tabpane.getComponentAt(2);
				  mp.renderContractMethods(bDefPID);
				}
				else
				{
					MethodsPane mp =
					  (MethodsPane)((BMechBuilder)parent).tabpane.getComponentAt(2);
					mp.clearContractMethods();
				}
			}
		});	
		gbc2.gridx = 1;
		contractPanel.add(bDefPIDComboBox, gbc2);
		return contractPanel;
	}
	
    private JPanel setDCPanel()
    {   	
        // DC Table Panel
        dcTableModel = new DefaultTableModel();
		// Create a JTable that disallow edits (edits done via dialog box only)
		dcTable = new JTable(dcTableModel) {
		  public boolean isCellEditable(int rowIndex, int vColIndex) {
		  	if (vColIndex == 0){
		  		return false;
		  	}
		  	else{ 
		  		return true;
		  	}
		  }
		};
        
        dcTable.setColumnSelectionAllowed(false);
        dcTable.setRowSelectionAllowed(true);
        
        dcTableModel.addColumn("Element Name");
        dcTableModel.addColumn("Value");
               
        dcTableModel.addRow(new Object[]{"title", ""});
        dcTableModel.addRow(new Object[]{"creator", ""});
        dcTableModel.addRow(new Object[]{"subject", ""});
        dcTableModel.addRow(new Object[]{"publisher", ""});
        dcTableModel.addRow(new Object[]{"contributor", ""});
        dcTableModel.addRow(new Object[]{"date", ""});
        dcTableModel.addRow(new Object[]{"type", ""});
        dcTableModel.addRow(new Object[]{"format", ""});
        dcTableModel.addRow(new Object[]{"identifier", ""});
        dcTableModel.addRow(new Object[]{"source", ""});
        dcTableModel.addRow(new Object[]{"language", ""});
        dcTableModel.addRow(new Object[]{"relation", ""});
        dcTableModel.addRow(new Object[]{"coverage", ""});
        dcTableModel.addRow(new Object[]{"rights", ""});
		JScrollPane scrollpane = new JScrollPane(dcTable);
		scrollpane.getViewport().setBackground(Color.white);

        // Table Buttons Panel
        JButton jb1 = new JButton("Add");
        jb1.setMinimumSize(new Dimension(100,30));
        jb1.setMaximumSize(new Dimension(100,30));
        jb1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            addDCElement();
          }
        } );
        JButton jb2 = new JButton("Edit");
        jb2.setMinimumSize(new Dimension(100,30));
        jb2.setMaximumSize(new Dimension(100,30));
        jb2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            editDCElement();
          }
        } );
        JButton jb3 = new JButton("Delete");
        jb3.setMinimumSize(new Dimension(100,30));
        jb3.setMaximumSize(new Dimension(100,30));
        jb3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            deleteDCElement();
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
      	return bDefPID;
        //return bDefPID.getText();
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

	public void setDCElement(String dcName, String dcValue)
	{
	  if (editDCMode)
	  {
		int currentRowIndex = dcTable.getSelectedRow();
		dcTable.setValueAt(dcName, currentRowIndex,0);
		dcTable.setValueAt(dcValue, currentRowIndex,1);
	  }
	  else
	  {
		dcTableModel.addRow(new Object[]{dcName, dcValue});
	  }
	}
	
	private void addDCElement()
	{
	  dcDialog = new DCElementDialog(this, "Add DC Element", true);
	}

	private void editDCElement()
	{
	  editDCMode = true;
	  if (dcTable.isEditing())
	  {
		dcTable.getCellEditor().stopCellEditing();
	  }
	  int currentRowIndex = dcTable.getSelectedRow();
	  dcDialog = new DCElementDialog(
		this, "Edit DC Element", true,
		(String)dcTable.getValueAt(currentRowIndex,0),
		(String)dcTable.getValueAt(currentRowIndex,1));
	  editDCMode = false;
	}

    private void deleteDCElement()
    {
      dcTableModel.removeRow(dcTable.getSelectedRow());
    }
    
	protected void assertInvalidDCMsg(String msg)
	{
	  JOptionPane.showMessageDialog(
		this, new String(msg), "Invalid DC Element",
		JOptionPane.INFORMATION_MESSAGE);
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

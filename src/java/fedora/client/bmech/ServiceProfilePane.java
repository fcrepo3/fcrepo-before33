package fedora.client.bmech;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fedora.client.bmech.data.ServiceProfile;
import fedora.client.bmech.data.ServiceSoftware;

/**
 *
 * <p><b>Title:</b> ServiceProfilePane.java</p>
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
public class ServiceProfilePane
        extends JPanel {

    private JTextField serviceName;
    private JTextField serviceLabel;
    private JTextField serviceTestURL;

    private JComboBox msgProtocol;
    private JTextField inputMIMETypes;
	private JTextField outputMIMETypes;
	
	private JTable dependencyTable;
	private BMechBuilder parent;
	

    public ServiceProfilePane(BMechBuilder parent)
    {
    	this.parent = parent;
        setLayout(new GridLayout(3,1));

        // Text Panel 1
        JPanel textPane1 = new JPanel();
        textPane1.setBorder(new TitledBorder("General"));
        textPane1.setLayout(new GridLayout(4,2));
        textPane1.add(new JLabel("Service Name: "));
        textPane1.add(serviceName = new JTextField());
        textPane1.add(new JLabel("Service Description: "));
        textPane1.add(serviceLabel = new JTextField());
        textPane1.add(new JLabel("Service Test URL: "));
        textPane1.add(serviceTestURL = new JTextField());


        // Text Panel 2
        JPanel textPane2 = new JPanel();
        textPane2.setBorder(new TitledBorder("Service Binding"));
        textPane2.setLayout(new GridLayout(4,2));
        
		textPane2.add(new JLabel("Messaging Protocol:"));	
		String[] messaging = {"HTTP GET", "HTTP POST", "SOAP"};
		textPane2.add(msgProtocol = new JComboBox(messaging));
        textPane2.add(new JLabel("Input MIME Types: "));
        textPane2.add(inputMIMETypes = new JTextField());
        textPane2.add(new JLabel("Output MIME Types: "));
        textPane2.add(outputMIMETypes = new JTextField());

		// Service Dependencies Input Table Panel
		dependencyTable = new JTable(6,5);
		dependencyTable.setColumnSelectionAllowed(false);
		dependencyTable.setRowSelectionAllowed(true);
		dependencyTable.setRowHeight(18);

		TableColumn tc0 = dependencyTable.getColumnModel().getColumn(0);
		tc0.setHeaderValue("Software Name");
		tc0.sizeWidthToFit();
		TableColumn tc1 = dependencyTable.getColumnModel().getColumn(1);
		tc1.setHeaderValue("Version");
		tc1.sizeWidthToFit();
		TableColumn tc2 = dependencyTable.getColumnModel().getColumn(2);
		tc2.setHeaderValue("Software Type");
		String[] swtype = {ServiceSoftware.SW_PROGLANG, 
						   ServiceSoftware.SW_OS, 
						   ServiceSoftware.SW_UTILITY, 
						   ServiceSoftware.SW_APPLIC, 
						   ServiceSoftware.SW_OTHER};
		tc2.setCellRenderer(new ComboBoxRenderer(swtype));
		tc2.setCellEditor(new ComboBoxTableCellEditor(swtype));
		tc2.sizeWidthToFit();
		tc2.sizeWidthToFit();
		TableColumn tc3 = dependencyTable.getColumnModel().getColumn(3);
		tc3.setHeaderValue("License Type");
		String[] license = {ServiceSoftware.L_COM, 
							ServiceSoftware.L_GPL, 
							ServiceSoftware.L_LGPL,
							ServiceSoftware.L_MPL, 
							ServiceSoftware.L_BSD, 
							ServiceSoftware.L_CPL, 
							ServiceSoftware.L_OTHER};
		tc3.setCellRenderer(new ComboBoxRenderer(license));
		tc3.setCellEditor(new ComboBoxTableCellEditor(license));
		tc3.sizeWidthToFit();
		TableColumn tc4 = dependencyTable.getColumnModel().getColumn(4);
		tc4.setHeaderValue("Open Source?");
		String[] yesno = {Boolean.toString(ServiceSoftware.YES), 
						  Boolean.toString(ServiceSoftware.NO)};
		tc4.setCellRenderer(new ComboBoxRenderer(yesno));
		tc4.setCellEditor(new ComboBoxTableCellEditor(yesno));
		tc4.sizeWidthToFit();

		JScrollPane scrollpane = new JScrollPane(dependencyTable);
		scrollpane.setColumnHeaderView(dependencyTable.getTableHeader());
		scrollpane.getViewport().setBackground(Color.white);
		
		JPanel dependencyPanel = new JPanel(new BorderLayout());
		dependencyPanel.setBorder(new TitledBorder("Software Dependencies"));
		dependencyPanel.add(scrollpane, BorderLayout.CENTER);

        add(textPane1);
        add(textPane2);
        add(dependencyPanel);
        setVisible(true);
    }

    public ServiceProfile getServiceProfile()
    {
    	ServiceProfile profile = new ServiceProfile();
    	profile.serviceName = getServiceName();
    	profile.serviceLabel = getServiceLabel();
    	profile.serviceTestURL = getServiceTestURL();
    	profile.msgProtocol = getMsgProtocol();
    	profile.inputMIMETypes = getInputMIMETypes();
    	profile.outputMIMETypes = getOutputMIMETypes();
    	profile.software = getSWDependencies();
    	return profile;
    }   

	public String getServiceName()
	{
	  return serviceName.getText();
	}

	public String getServiceLabel()
	{
	  return serviceLabel.getText();
	}
	
	public String getServiceTestURL()
	{
	  return serviceTestURL.getText();
	}
	
	public String getMsgProtocol()
	{
	  return (String)msgProtocol.getSelectedItem();
	}

	public String[] getInputMIMETypes()
	{
	  Vector mimeTypes = new Vector();
	  String normalizedString = normalizeString(inputMIMETypes.getText());
	  StringTokenizer st = new StringTokenizer(normalizedString, ",");
	  while (st.hasMoreElements())
	  {
		mimeTypes.add(((String)st.nextElement()).trim());
	  }
	  return (String[])mimeTypes.toArray(new String[0]);
	}
		
	public String[] getOutputMIMETypes()
	{
	  Vector mimeTypes = new Vector();
	  String normalizedString = normalizeString(outputMIMETypes.getText());
	  StringTokenizer st = new StringTokenizer(normalizedString, ",");
	  while (st.hasMoreElements())
	  {
		mimeTypes.add(((String)st.nextElement()).trim());
	  }
	  return (String[])mimeTypes.toArray(new String[0]);
	}

	public ServiceSoftware[] getSWDependencies()
	{
	  if (dependencyTable.isEditing())
	  {
		dependencyTable.getCellEditor().stopCellEditing();
	  }
	  Vector elements = new Vector();
	  int rowcount = dependencyTable.getModel().getRowCount();
	  for (int i=0; i<rowcount; i++)
	  {
		ServiceSoftware sw = new ServiceSoftware();		
		sw.swName = (String)dependencyTable.getValueAt(i,0);		
		if (sw.swName != null)
		{
		  sw.swName = sw.swName.trim();
		}
		if (sw.swName != null && !sw.swName.equalsIgnoreCase("")
		  && !sw.swName.equalsIgnoreCase(" "))
		{
			sw.swVersion = (String)dependencyTable.getValueAt(i,1);
			sw.swType = (String)dependencyTable.getValueAt(i,2);
			sw.swLicenceType = (String)dependencyTable.getValueAt(i,3);
			sw.isOpenSource = Boolean.getBoolean((String)dependencyTable.getValueAt(i,4));
			elements.add(sw);
		}
	  }
	  return (ServiceSoftware[])elements.toArray(new ServiceSoftware[0]);
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
}
package fedora.client.bmech;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.io.File;
import java.io.InputStream;

import fedora.client.Administrator;
import fedora.client.bmech.data.*;
import fedora.client.bmech.xml.*;
import fedora.client.utility.ingest.AutoIngestor;

/**
 *
 * <p><b>Title:</b> BMechBuilder.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class BMechBuilder extends JInternalFrame
{

    protected JTabbedPane tabpane;
    protected BMechTemplate newBMech;
    private int selectedTabPane;
	private String s_protocol = null;
    private String s_host = null;
    private int s_port = 0;
    private String s_user = null;
    private String s_pass = null;
    private File s_lastDir = null;
    private String currentTabName;
    private int currentTabIndex;

    public static void main(String[] args)
    {
      try {
          if (args.length == 5) {
				      JFrame frame = new JFrame("BMechBuilder Test");
				      String protocol = args[0];
				      String host = args[1];
				      int port = new Integer(args[2]).intValue();
				      String user = args[3];
				      String pass = args[4];
				      File dir = null;
				      frame.addWindowListener(new WindowAdapter() {
				          public void windowClosing(WindowEvent e) {System.exit(0);}
				      });
				      frame.getContentPane().add(
				        new BMechBuilder(protocol, host, port, user, pass, dir),
				          BorderLayout.CENTER);
				      frame.setSize(700, 500);
				      frame.setVisible(true);
          } else {
              System.out.println("BMechBuilder main method requires 5 arguments.");
              System.out.println("Usage: BMechBuilder protocol host port user pass");
          }
      } catch (Exception e) {
          System.out.println(e.getMessage());
          e.printStackTrace();
      }     
  }

    public BMechBuilder(String protocol, String host, int port, String user, String pass, File dir)
    {
        super("Behavior Mechanism Builder");
		s_protocol = protocol;
        s_host = host;
        s_port = port;
        s_user = user;
        s_pass = pass;
        s_lastDir = dir;
        setClosable(true);
        setMaximizable(true);
        setSize(700, 500);
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        newBMech = new BMechTemplate();

        tabpane = new JTabbedPane();
        tabpane.setBackground(Color.GRAY);
        tabpane.addTab("General", createGeneralPane());
		tabpane.addTab("Service Profile", createProfilePane());
        tabpane.addTab("Service Methods", createMethodsPane());
        tabpane.addTab("Datastream Input", createDSInputPane());
        tabpane.addTab("Documentation", createDocPane());

        // General Buttons Panel
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            saveBMech();
          }
        } );
        JButton ingest = new JButton("Ingest");
        ingest.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            ingestBMech();
          }
        } );
        JButton help = new JButton("Help");
        help.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            showHelp();
          }
        } );
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            cancelBMech();
          }
        } );
        JPanel gbuttonPanel = new JPanel();
        gbuttonPanel.setBackground(Color.WHITE);
        gbuttonPanel.add(save);
        gbuttonPanel.add(ingest);
        gbuttonPanel.add(help);
        gbuttonPanel.add(cancel);

        getContentPane().add(tabpane, BorderLayout.CENTER);
        getContentPane().add(gbuttonPanel, BorderLayout.SOUTH);
        setListeners();
        setVisible(true);
    }

	private void setListeners()
	{	
		// set up listener for JTabbedPane object
		tabpane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// everytime a tab changes, update the bmech template object in memory
				updateBMechTemplate();
				currentTabIndex = tabpane.getSelectedIndex();
				currentTabName = tabpane.getTitleAt(currentTabIndex);
				// pre-populate the DatastreamInputPane with valid datastream 
				// input parms that were defined in the MethodsPane
				if (currentTabIndex == 3)
				{
				  DatastreamInputPane dsip =
					(DatastreamInputPane)tabpane.getComponentAt(3);
				  dsip.renderDSBindingKeys(newBMech.getDSBindingKeys());
				}
			}
		});		
	}
	
    public BMechTemplate getBMechTemplate()
    {
      return newBMech;
    }

    public void saveBMech()
    {
      BMechMETSSerializer mets = savePanelInfo();
      File file = null;
      if (mets != null)
      {
        JFileChooser chooser = new JFileChooser(s_lastDir);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        XMLFileChooserFilter filter = new XMLFileChooserFilter();
        chooser.setFileFilter(filter);
        if (chooser.showSaveDialog(tabpane) == JFileChooser.APPROVE_OPTION)
        {
          file = chooser.getSelectedFile();
          s_lastDir = file.getParentFile(); // remember the dir for next time
          String ext = filter.getExtension(file);
          if (ext == null || !(ext.equalsIgnoreCase("xml")))
          {
            file = new File((file.getPath() + ".xml"));
          }
          try
          {
            mets.writeMETSFile(file);
          }
          catch (Exception e)
          {
            e.printStackTrace();
            assertTabPaneMsg(("BMechBuilder: Error saving METS file for bmech: "
              + e.getMessage()), "BMechBuilder");
          }
        }
        else
        {
          assertTabPaneMsg("BMechBuilder: You did not specify a file to Save.",
            "BMechBuilder");
        }
      }
    }

    public void ingestBMech()
    {
      InputStream in = null;
      String pid = null;
      BMechMETSSerializer mets = savePanelInfo();
      if (mets != null)
      {
        try
        {
          in = mets.writeMETSStream();
        }
        catch (Exception e)
        {
          e.printStackTrace();
          assertTabPaneMsg(("BMechBuilder: Error saving METS to stream for bmech: "
            + e.getMessage()), "BMechBuilder");
        }
        try
        {
		  AutoIngestor ingestor = new AutoIngestor(Administrator.APIA, Administrator.APIM);
          pid = ingestor.ingestAndCommit(in, "ingest bmech object via BMechBuilder tool");
        }
        catch (Exception e)
        {
          e.printStackTrace();
          assertTabPaneMsg(("BMechBuilder: error ingesting bmech object: "
            + e.getMessage()), null);
        }
        assertTabPaneMsg(("New PID = " + pid), "Successful Ingest");
      }
    }

    public void showHelp()
    {
      if (currentTabIndex == 0)
      {
        showGeneralHelp();
      }
      else if (currentTabIndex == 1)
      {
		showProfileHelp();
      }
      else if (currentTabIndex == 2)
      {
		showMethodsHelp();
      }
      else if (currentTabIndex == 3)
      {
		showDatastreamsHelp();
      }
      else if (currentTabIndex == 4)
      {
		showDocumentsHelp();
      }
    }

    public void cancelBMech()
    {
      setVisible(false);
      dispose();
    }

	protected void updateBMechTemplate()
	{
		Component[] tabs = tabpane.getComponents();
		for (int i=0; i < tabs.length; i++)
		{
		  if (tabs[i].getName().equalsIgnoreCase("GeneralTab"))
		  {
		  	  //System.out.println("updateBMechTemplate: GeneralTab");
			  GeneralPane gp = (GeneralPane)tabs[i];
			  if (gp.rb_chosen.equalsIgnoreCase("retainPID"))
			  {
				newBMech.setbObjPID(gp.getBObjectPID());
			  }
			  else
			  {
				newBMech.setbObjPID(null);
			  }
			  newBMech.setbDefContractPID(gp.getBDefContractPID());
			  newBMech.setbObjLabel(gp.getBObjectLabel());
			  newBMech.setbObjName(gp.getBObjectName());
			  newBMech.setDCRecord(gp.getDCElements());
		  }
		  else if (tabs[i].getName().equalsIgnoreCase("ProfileTab"))
		  {
			  //System.out.println("updateBMechTemplate: ProfileTab");
			  // set the datastream input rules
			  ServiceProfilePane spp = (ServiceProfilePane)tabs[i];
			  newBMech.setServiceProfile(spp.getServiceProfile());
		  }
		  else if (tabs[i].getName().equalsIgnoreCase("MethodsTab"))
		  {
			  //System.out.println("updateBMechTemplate: MethodsTab");
			  MethodsPane mp = (MethodsPane)tabs[i];
			  newBMech.setHasBaseURL(mp.hasBaseURL());
			  if (mp.hasBaseURL())
			  {
				String baseURL = mp.getBaseURL();
				if (baseURL.endsWith("/"))
				{
				  newBMech.setServiceBaseURL(baseURL);
				}
				else
				{
				  newBMech.setServiceBaseURL(baseURL + "/");
				}
			  }
			  else
			  {
				newBMech.setServiceBaseURL("LOCAL");
			  }
			  HashMap mmap = mp.getMethodMap();
			  Method[] methods = mp.getMethods();
			  newBMech.setMethodsHashMap(mmap);
			  newBMech.setMethods(methods);
			  
			  // we need to update the BMechTemplate object with the latest
			  // datastream binding keys that are defined as method parms
			  Vector dsBindingKeys = new Vector();
			  for (int m=0; m<methods.length; m++)
			  {
			  	MethodProperties props = methods[m].methodProperties;
			  	if (props != null)
			  	{
					for (int j=0; j<props.dsBindingKeys.length; j++)
					{
					  if (!dsBindingKeys.contains(props.dsBindingKeys[j]))
					  {
						dsBindingKeys.add(props.dsBindingKeys[j]);
					  }
					}
			  	}
			  }
			  newBMech.setDSBindingKeys(dsBindingKeys);
		  }
		  else if (tabs[i].getName().equalsIgnoreCase("DSInputTab"))
		  {
			  //System.out.println("updateBMechTemplate: DSInputTab");
			  // set the datastream input rules
			  DatastreamInputPane dsp = (DatastreamInputPane)tabs[i];
			  newBMech.setDSInputSpec(dsp.getDSInputRules());
		  }
		  else if (tabs[i].getName().equalsIgnoreCase("DocumentsTab"))
		  {
			  //System.out.println("updateBMechTemplate: DocumentsTab");
			  DocumentsPane docp = (DocumentsPane)tabs[i];
			  newBMech.setDocDatastreams(docp.getDocDatastreams());
		  }
		}
		return;		
	}
	
	protected boolean validateBMechTemplate()
	{
		boolean validBMech = false;
		Component[] tabs = tabpane.getComponents();
		for (int i=0; i < tabs.length; i++)
		{
			if (tabs[i].getName().equalsIgnoreCase("GeneralTab"))
			{
				if (!validGeneralTab((GeneralPane)tabs[i]))
				{
					return false;
				}
			}
			else if (tabs[i].getName().equalsIgnoreCase("ProfileTab"))
			{
				if (!validProfileTab((ServiceProfilePane)tabs[i]))
				{
					return false;
				}
			}
			else if (tabs[i].getName().equalsIgnoreCase("MethodsTab"))
			{
				if (!validMethodsTab((MethodsPane)tabs[i]))
			  	{
			  		return false;
			  	}
			}
			else if (tabs[i].getName().equalsIgnoreCase("DSInputTab"))
			{
			  	if (!validDSInputTab((DatastreamInputPane)tabs[i]))
			  	{
			  		return false;
			  	}
			}
			else if (tabs[i].getName().equalsIgnoreCase("DocumentsTab"))
			{
			  	if (!validDocsTab((DocumentsPane)tabs[i]))
			  	{
			  		return false;
			  	}
			}
		}
		return true;
	}
	
    public BMechMETSSerializer savePanelInfo()
    {
	  updateBMechTemplate();
	  BMechMETSSerializer mets = null;
	  if (validateBMechTemplate())
	  {
	      //printBMech();
	      DCGenerator dcg = null;
	      DSInputSpecGenerator dsg = null;
	      MethodMapGenerator mmg = null;
	      WSDLGenerator wsdlg = null;
	      ServiceProfileSerializer spg = null;
	      try
	      {
	        dcg = new DCGenerator(newBMech);
	        //dcg.printDC();
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	        assertTabPaneMsg("BMechBuilder: error serializing dc record", null);
	      }
		  try
		  {
			spg = new ServiceProfileSerializer(newBMech);
		  }
		  catch (Exception e)
		  {
		    e.printStackTrace();
		    assertTabPaneMsg("BMechBuilder: error serializing service profile", null);
		  }
	      try
	      {
	        dsg = new DSInputSpecGenerator(newBMech);
	        //dsg.printDSInputSpec();
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	        assertTabPaneMsg("BMechBuilder: error serializing ds input spec", null);
	      }
	      try
	      {
	        mmg = new MethodMapGenerator(newBMech);
	        //mmg.printMethodMap();
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	        assertTabPaneMsg("BMechBuilder: error serializing method map", null);
	      }
	      try
	      {
	        wsdlg = new WSDLGenerator(newBMech);
	        //wsdlg.printWSDL();
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	        assertTabPaneMsg("BMechBuilder: error serializing wsdl", null);
	      }
	
	      try
	      {
	        mets = new BMechMETSSerializer(newBMech, dcg.getRootElement(), spg.getRootElement(),
	          dsg.getRootElement(), mmg.getRootElement(), wsdlg.getRootElement());
	      }
	      catch (Exception e)
	      {
	        e.printStackTrace();
	        assertTabPaneMsg("BMechBuilder: error in creating METS for bmech.", null);
	      }
	      //mets.printMETS();
	      //return mets;
	  }
	  return mets;
    }
	
    private JComponent createGeneralPane()
    {
      GeneralPane gpane = new GeneralPane(this);
      gpane.setName("GeneralTab");
      return gpane;
      //return new JLabel("Insert general stuff here.");
    }

	private JComponent createProfilePane()
	{
	  ServiceProfilePane profpane = new ServiceProfilePane(this);
	  profpane.setName("ProfileTab");
	  return profpane;
	}
	
    private JComponent createMethodsPane()
    {
      MethodsPane mpane = new MethodsPane(this);
      mpane.setName("MethodsTab");
      return mpane;
    }

    private JComponent createDSInputPane()
    {
      DatastreamInputPane dspane = new DatastreamInputPane(this);
      dspane.setName("DSInputTab");
      return dspane;
    }

    private JComponent createDocPane()
    {
      DocumentsPane docpane = new DocumentsPane();
      docpane.setName("DocumentsTab");
      return docpane;
    }

    private boolean validGeneralTab(GeneralPane gp)
    {
      if (gp.rb_chosen.equalsIgnoreCase("retainPID") &&
         (gp.getBObjectPID() == null || gp.getBObjectPID().trim().equals("")))
      {
        assertTabPaneMsg("The test PID value is missing on General Tab.", gp.getName());
        return false;
      }
      else if (gp.getBDefContractPID() == null || gp.getBDefContractPID().trim().equals(""))
      {
        assertTabPaneMsg("BDefPID is missing on General Tab.", gp.getName());
        return false;
      }
      else if (gp.getBObjectLabel() == null || gp.getBObjectLabel().trim().equals(""))
      {
        assertTabPaneMsg("Behavior Object Description is missing on General Tab.", gp.getName());
        return false;
      }
      else if (gp.getBObjectName() == null || gp.getBObjectName().trim().equals(""))
      {
        assertTabPaneMsg("Behavior Object Name (1-word) is missing on General Tab.", gp.getName());
        return false;
      }
      else if (gp.getDCElements().length <= 0)
      {
        assertTabPaneMsg("You must enter at least one DC element on General Tab.", gp.getName());
        return false;
      }
      return true;
    }
    
	private boolean validProfileTab(ServiceProfilePane spp)
	{
		if (spp.getServiceName() == null)
		{
		  assertTabPaneMsg(new String("You must enter a Service name"
			+ " in the Service Profile Tab"),
			spp.getName());
		  return false;
		}
		else if (spp.getMsgProtocol() == null)
		{
		  assertTabPaneMsg(new String("You must enter the messaging protocol for"
			+ " this service in the Service Profile Tab"), spp.getName());
		  return false;
		}
		else if (spp.getOutputMIMETypes().length == 0)
		{
		  assertTabPaneMsg(new String("You must enter at least one output MIME type"
			+ " for this service in the Service Profile Tab"), spp.getName());
		  return false;
		}
	  return true;
	}
	
    private boolean validMethodsTab(MethodsPane mp)
    {
      if (mp.hasBaseURL() && (mp.getBaseURL() == null || mp.getBaseURL().trim().equals("")))
      {
        assertTabPaneMsg("The Base URL is missing on Service Methods Tab.", mp.getName());
        return false;
      }
      else if (mp.getMethods().length <=0)
      {
        assertTabPaneMsg("You must enter at least one method definition in Service Methods Tab.", mp.getName());
        return false;
      }
      else
      {
        Method[] methods = mp.getMethods();
        for (int i=0; i<methods.length; i++)
        {
          if (methods[i].methodProperties == null)
          {
            assertTabPaneMsg(new String("You must enter properties for the method "
              + methods[i].methodName) + " in the Service Methods Tab", mp.getName());
            return false;
          }
          else if (!methods[i].methodProperties.wasValidated)
		  {
			assertTabPaneMsg(new String("You must enter valid properties for the method "
			  + methods[i].methodName + " in the Service Methods Tab"), mp.getName());
			return false;
		  }
        }
        return true;
      }
    }

    private boolean validDSInputTab(DatastreamInputPane dsp)
    {
      DSInputRule[] rules = dsp.getDSInputRules();
	  Vector bindkeys = newBMech.getDSBindingKeys();
	  if (bindkeys.size() != rules.length)
	  {
		assertTabPaneMsg(new String("You have not completed entry of the Datastream"
		  + " input binding rules"
		  + " in the Datastream Input Tab"),
		  dsp.getName());
		return false;	  
	  }
      for (int i=0; i<rules.length; i++)
      {
        if (rules[i].bindingKeyName == null)
        {
          assertTabPaneMsg(new String("A Datastream parm name is missing"
            + " from column 1 of the table in the Datastream Input Tab"),
            dsp.getName());
          return false;
        }
        else if (rules[i].bindingMIMEType == null ||
        		 rules[i].bindingMIMEType.trim().equalsIgnoreCase(""))
        {
          assertTabPaneMsg(new String("You must enter MIMEType for"
            + " datastream input parm " + rules[i].bindingKeyName
            + " in the Datastream Input Tab"), dsp.getName());
          return false;
        }
        else if (rules[i].minNumBindings == null)
        {
          assertTabPaneMsg(new String("You must enter Min Occurs for"
            + " datastream input parm " + rules[i].bindingKeyName
            + " on Datastream Input Tab."), dsp.getName());
          return false;
        }
        else if (rules[i].maxNumBindings == null)
        {
          assertTabPaneMsg(new String("You must enter Max Occurs for"
            + " datastream input parm " + rules[i].bindingKeyName
            + " on Datastream Input Tab."), dsp.getName());
          return false;
        }
        else if (rules[i].ordinality == null)
        {
          assertTabPaneMsg(new String("You must enter Order Matters for"
            + " datastream input parm " + rules[i].bindingKeyName
            + " on Datastream Input Tab."), dsp.getName());
          return false;
        }
        else if (rules[i].bindingLabel == null)
        {
          assertTabPaneMsg(new String("You must enter Pretty Label for"
            + " datastream input parm " + rules[i].bindingKeyName
            + " on Datastream Input Tab."), dsp.getName());
          return false;
        }
      }
      return true;
    }

    private boolean validDocsTab(DocumentsPane docp)
    {
      Datastream[] docs = docp.getDocDatastreams();
      if (docs.length < 1)
      {
          assertTabPaneMsg(new String("You must enter at least one document"
            + " that describes the service in the Documents Tab."),
            docp.getName());
          return false;
      }

      for (int i=0; i<docs.length; i++)
      {
        if (docs[i].dsLabel == null)
        {
          assertTabPaneMsg(new String("You must enter a Label for all documents"
            + "listed on the Documents Tab."), docp.getName());
          return false;
        }
        else if (docs[i].dsMIMEType == null)
        {
          assertTabPaneMsg(new String("You must enter a MIME type for all documents"
            + "listed on the Documents Tab."), docp.getName());
          return false;
        }
      }
      return true;
    }

    private void showGeneralHelp()
    {
        JTextArea helptxt = new JTextArea();
        helptxt.setLineWrap(true);
        helptxt.setWrapStyleWord(true);
        helptxt.setBounds(0,0,550,20);
        helptxt.append("There are three sections to the General Tab that"
          + " must be completed:\n\n"
          + " Object Description:\n"
          + " >>> Behavior Object PID: either select the button for the"
          + " repository system to generate one, or enter your own"
          + " with the prefix 'test:' or 'demo:'\n\n"
          + " >>> Behavior Object Name:  enter a single word to name the object."
          + " This name is used in various places within inline metadata that"
          + " is generated by the tool.\n\n"
          + " >>> Behavior Object Label: enter a meaningful label for theobject.\n\n"
          + " \n"
          + " Behavior Contract:\n"
          + " >>> Behavior Definition PID: enter the PID of the Behavior Definition"
          + " Object that the Behavior Mechanism is fullfilling\n\n"
          + " \n"
          + " Dublin Core Metadata:\n"
          + ">>> Enter at least one DC element to describe"
          + " the Behavior Mechanism Object.");

        JOptionPane.showMessageDialog(
          this, helptxt, "Help for General Tab",
          JOptionPane.OK_OPTION);
    }

    private void showMethodsHelp()
    {
        JTextArea helptxt = new JTextArea();
        helptxt.setLineWrap(true);
        helptxt.setWrapStyleWord(true);
        helptxt.setBounds(0,0,550,20);
        helptxt.append("Service Address:\n There are three types of service bindings that can"
          + " be set up in a Behavior Mechanism object:\n\n"
          + " 1. Base URL (Service with a Base URL): You are mapping the"
          + " Behavior Mechanism object to a service that has a"
          + " single base URL that all of the service methods are relative to."
          + " The service will be used to transform or refactor Datastream content.\n\n"
          + " 2. No Base URL (Multi-Server Service): You are mapping the"
          + " Behavior Mechanism object to a service whose methods do not have a"
          + " common base URL.  Instead, different methods may run on different"
          + " servers.  However, from the Fedora perspective these methods"
          + " may be aggregated together in a single Behavior Mechanism object" 
          + " to fulfill a behavior contract. The service methods will be used to"
          + " transform or refactor Datastream content.\n\n"
          + " 2. Fedora Built-in Datastream Resolver: You are NOT mapping the"
          + " Behavior Mechanism object to a service. Instead, this Behavior Mechanism"
          + " object will partake of default capabilities of the Fedora repository"
          + " server.  You can use this option if you simply want to make an association"
          + " between methods of a behavor contract and Datastreams in the object."
          + " So, for example, you want the behavior contract methods to just return"
          + " specific Datastreams in the object without transforming or refactoring"
          + " those datastreams via a service.  This option is really just specifying"
          + " a MethodName-to-Datastream binding relationship.\n\n\n"
          + " Service Method Definitions:\n Here are the definitions of the specific methods"
          + " that are runnable by the service.  A list of methods are automatically "
          + " listed in the table.  These were obtained by looking up the abstract methods"
          + " defined by the Behavior Definition Contract that you specified in the 'General Tab.'" 
          + " Use the 'Properties' button to the right of the table to enter specific service"
          + " binding information for each method.");

        JOptionPane.showMessageDialog(
          this, helptxt, "Help for Service Methods Tab",
          JOptionPane.OK_OPTION);
    }

    private void showDatastreamsHelp()
    {
        JTextArea helptxt = new JTextArea();
        helptxt.setLineWrap(true);
        helptxt.setWrapStyleWord(true);
        helptxt.setBounds(0,0,550,20);
        helptxt.append("insert datastream Input help\n\n");
        helptxt.append("\n\n");
        helptxt.append("\n\n");

        JOptionPane.showMessageDialog(
          this, helptxt, "Help for Datastream Input Tab",
          JOptionPane.OK_OPTION);
    }

    private void showDocumentsHelp()
    {
        JTextArea helptxt = new JTextArea();
        helptxt.setLineWrap(true);
        helptxt.setWrapStyleWord(true);
        helptxt.setBounds(0,0,550,20);
        helptxt.append("insert documents help\n\n");
        helptxt.append("\n\n");
        helptxt.append("\n\n");

        JOptionPane.showMessageDialog(
          this, helptxt, "Help for Documents Tab",
          JOptionPane.OK_OPTION);
    }

    private void showProfileHelp()
    {
        JTextArea helptxt = new JTextArea();
        helptxt.setLineWrap(true);
        helptxt.setWrapStyleWord(true);
        helptxt.setBounds(0,0,550,20);
        helptxt.append("Use the Service Profile to enter technical information about"
        + " the service being mapped to this Behavior Mechanism object.\n\n");

        JOptionPane.showMessageDialog(
          this, helptxt, "Help for Service Profile Tab",
          JOptionPane.OK_OPTION);
    }

    private void assertTabPaneMsg(String msg, String tabpane)
    {
      JOptionPane.showMessageDialog(
        this, new String(msg), new String(tabpane + " Message"),
        JOptionPane.INFORMATION_MESSAGE);
    }
    
	private void printBMech()
	{
	  System.out.println("FROM GENERAL TAB===============================");
	  System.out.println("bDefPID: " + newBMech.getbDefContractPID());
	  System.out.println("bMechLabel: " + newBMech.getbObjLabel());
	  System.out.println("DCRecord: ");
	  DCElement[] dcrecord = newBMech.getDCRecord();
	  for (int i=0; i<dcrecord.length; i++)
	  {
		System.out.println(">>> " + dcrecord[i].elementName + "="
		  + dcrecord[i].elementValue);
	  }
	  System.out.println("FROM PROFILE TAB===============================");
	  System.out.println("serviceName: " + newBMech.getServiceProfile().serviceName);
	  System.out.println("serviceLabel: " + newBMech.getServiceProfile().serviceLabel);
	  System.out.println("serviceTestURL: " + newBMech.getServiceProfile().serviceTestURL);
	  System.out.println("Input MIME: ");
	  String[] inputMIME = newBMech.getServiceProfile().inputMIMETypes;
	  for (int i=0; i<inputMIME.length; i++)
	  {
		System.out.println(">>> " + inputMIME[i]);
	  }
	  System.out.println("Input MIME: ");
	  String[] outputMIME = newBMech.getServiceProfile().outputMIMETypes;
	  for (int i=0; i<outputMIME.length; i++)
	  {
		System.out.println(">>> " + outputMIME[i]);
	  }
	  System.out.println("SW Depend: ");
	  ServiceSoftware[] sw = newBMech.getServiceProfile().software;
	  for (int i=0; i<sw.length; i++)
	  {
		System.out.println(">>> " + sw[i].swName + "," + sw[i].swType + "," + sw[i].swVersion
		+ "," + sw[i].swLicenceType + ",");
	  }
	  System.out.println("FROM METHODS TAB===============================");
	  System.out.println("hasBaseURL: "  + newBMech.getHasBaseURL());
	  System.out.println("serviceBaseURL: " + newBMech.getServiceBaseURL());
	  System.out.println("methods: ");
	  HashMap m2 = newBMech.getMethodsHashMap();
	  Collection methods = m2.values();
	  Iterator it_methods = methods.iterator();
	  while (it_methods.hasNext())
	  {
		Method method = (Method)it_methods.next();
		System.out.println("  method name: " + method.methodName + "\n"
		  + "  method desc: " + method.methodLabel + "\n"
		  + "  method URL: " + method.methodProperties.methodFullURL + "\n"
		  + "  method protocol" + method.methodProperties.protocolType + "\n");
		System.out.println("  method parms:");
		int parmcnt = method.methodProperties.methodParms.length;
		for (int i=0; i<parmcnt; i++)
		{
		  MethodParm mp = method.methodProperties.methodParms[i];
		  System.out.println(">>>parmName: " + mp.parmName + "\n"
			+ ">>>parmType: " + mp.parmType + "\n"
			+ ">>>parmLabel: " + mp.parmLabel + "\n"
			+ ">>>parmDefaultValue: " + mp.parmDefaultValue + "\n"
			+ ">>>parmPassBy: " + mp.parmPassBy + "\n"
			+ ">>>parmRequired: " + mp.parmRequired + "\n"
			+ ">>>parmDomainValues: " + mp.parmDomainValues + "\n");
		}
	  }
	  System.out.println("FROM DSINPUT TAB===============================");
	  DSInputRule[] rules = newBMech.getDSInputSpec();
	  for (int i=0; i<rules.length; i++)
	  {
		System.out.println(">>>name= " + rules[i].bindingKeyName + "\n"
		  + ">>>mime= " + rules[i].bindingMIMEType + "\n"
		  + ">>>min= " + rules[i].minNumBindings + "\n"
		  + ">>>max= " + rules[i].maxNumBindings + "\n"
		  + ">>>order= " + rules[i].ordinality + "\n"
		  + ">>>label= " + rules[i].bindingLabel + "\n"
		  + ">>>instruct= " + rules[i].bindingInstruction + "\n");
	  }
	}
  }
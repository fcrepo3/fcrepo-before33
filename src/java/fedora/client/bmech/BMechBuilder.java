package fedora.client.bmech;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.File;
import java.io.InputStream;

import fedora.client.bmech.data.*;
import fedora.client.bmech.xml.*;
import fedora.client.ingest.AutoIngestor;

public class BMechBuilder extends JInternalFrame
{

    private JTabbedPane tabpane;
    protected BMechTemplate newBMech;
    private int selectedTabPane;
    private String s_host = null;
    private int s_port = 0;
    private String s_user = null;
    private String s_pass = null;


    public static void main(String[] args)
    {
      JFrame frame = new JFrame("BMechBuilder Test");
      frame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {System.exit(0);}
      });

      frame.getContentPane().add(
        new BMechBuilder("http://localhost", 8080, "test", "test"),
          BorderLayout.CENTER);
      frame.setSize(700, 500);
      frame.setVisible(true);
  }

    public BMechBuilder(String host, int port, String user, String pass)
    {
        super("BMechBuilder");
        s_host = host;
        s_port = port;
        s_user = user;
        s_pass = pass;
        setClosable(true);
        setMaximizable(true);
        setSize(700, 500);
        getContentPane().setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        newBMech = new BMechTemplate();

        tabpane = new JTabbedPane();
        tabpane.setBackground(Color.GRAY);
        tabpane.addTab("General", createGeneralPane());
        tabpane.addTab("Service Methods", createMethodsPane());
        tabpane.addTab("Datastream Input", createDSInputPane());
        tabpane.addTab("Documentation", createDocPane());
        tabpane.addTab("Service Profile", createProfilePane());
        // set up listener for JTabbedPane object
        tabpane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int index = tabpane.getSelectedIndex();
                String title = tabpane.getTitleAt(index);
                System.out.println("index = " +
                                        index);
                System.out.println("title = " +
                                        title);

                if (index == 2)
                {
                  DatastreamInputPane dsip =
                    (DatastreamInputPane)tabpane.getComponentAt(2);
                  dsip.setDSBindingKeys(newBMech.getDSBindingKeys());
                }
            }
        });


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
        setVisible(true);
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
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        XMLFileChooserFilter filter = new XMLFileChooserFilter();
        chooser.setFileFilter(filter);
        if (chooser.showSaveDialog(tabpane) == JFileChooser.APPROVE_OPTION)
        {
          file = chooser.getSelectedFile();
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
          AutoIngestor ingestor = new AutoIngestor(s_host, s_port, s_user, s_pass);
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
      return;
    }

    public void cancelBMech()
    {
      setVisible(false);
      dispose();
    }

    public BMechMETSSerializer savePanelInfo()
    {

      Component[] tabs = tabpane.getComponents();
      System.out.println("tabs count: " + tabs.length);
      for (int i=0; i < tabs.length; i++)
      {
        System.out.println("tab name: " + tabs[i].getName());
        if (tabs[i].getName().equalsIgnoreCase("GeneralTab"))
        {
          if (validGeneralTab((GeneralPane)tabs[i]))
          {
            GeneralPane gp = (GeneralPane)tabs[i];
            newBMech.setbDefPID(gp.getBDefPID());
            newBMech.setbMechLabel(gp.getBMechLabel());
            newBMech.setbMechName(gp.getBMechName());
            newBMech.setDCRecord(gp.getDCElements());
          }
          else
          {
            return null;
          }
        }
        else if (tabs[i].getName().equalsIgnoreCase("MethodsTab"))
        {
          if (validMethodsTab((MethodsPane)tabs[i]))
          {
            MethodsPane mp = (MethodsPane)tabs[i];
            newBMech.setHasBaseURL(mp.hasBaseURL());
            String baseURL = mp.getBaseURL();
            if (baseURL.endsWith("/"))
            {
              newBMech.setServiceBaseURL(baseURL);
            }
            else
            {
              newBMech.setServiceBaseURL(baseURL + "/");
            }
            newBMech.setBMechMethodMap(mp.getBMechMethodMap());
            newBMech.setBMechMethods(mp.getBMechMethods());
          }
          else
          {
            return null;
          }
        }
        else if (tabs[i].getName().equalsIgnoreCase("DSInputTab"))
        {
          if (validDSInputTab((DatastreamInputPane)tabs[i]))
          {
            DatastreamInputPane dsp = (DatastreamInputPane)tabs[i];
            newBMech.setDSInputSpec(dsp.getDSInputRules());
          }
          else
          {
            return null;
          }
        }
        else if (tabs[i].getName().equalsIgnoreCase("DocumentsTab"))
        {
          if (validDocsTab((DocumentsPane)tabs[i]))
          {
            DocumentsPane docp = (DocumentsPane)tabs[i];
            newBMech.setDocDatastreams(docp.getDocDatastreams());
          }
          else
          {
            return null;
          }
        }
      }
      printBMech();
      DSInputSpecGenerator dsg = new DSInputSpecGenerator(newBMech);
      //dsg.printDSInputSpec();
      MethodMapGenerator mmg = new MethodMapGenerator(newBMech);
      //mmg.printMethodMap();
      WSDLGenerator wsdlg = new WSDLGenerator(newBMech);
      //wsdlg.printWSDL();
      BMechMETSSerializer mets = null;
      try
      {
        mets = new BMechMETSSerializer(newBMech, dsg.getRootElement(),
          mmg.getRootElement(), wsdlg.getRootElement());
      }
      catch (Exception e)
      {
        e.printStackTrace();
        assertTabPaneMsg("BMechBuilder: error in creating METS for bmech.", null);
      }
      mets.printMETS();
      return mets;
    }

    private JComponent createGeneralPane()
    {
      GeneralPane gpane = new GeneralPane();
      gpane.setName("GeneralTab");
      return gpane;
      //return new JLabel("Insert general stuff here.");
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

    private JComponent createProfilePane()
    {
      JLabel jl = new JLabel("Insert Service Profile stuff here.");
      jl.setName("ProfileTab");
      return jl;
    }

    private JComponent createDocPane()
    {
      DocumentsPane docpane = new DocumentsPane();
      docpane.setName("DocumentsTab");
      return docpane;
    }

    private void printBMech()
    {
      System.out.println("FROM GENERAL TAB===============================");
      System.out.println("bDefPID: " + newBMech.getbDefPID());
      System.out.println("bMechLabel: " + newBMech.getbMechLabel());
      System.out.println("DCRecord: ");
      DCElement[] dcrecord = newBMech.getDCRecord();
      for (int i=0; i<dcrecord.length; i++)
      {
        System.out.println(">>> " + dcrecord[i].elementName + "="
          + dcrecord[i].elementValue);
      }
      System.out.println("FROM METHODS TAB===============================");
      System.out.println("hasBaseURL: "  + newBMech.getHasBaseURL());
      System.out.println("serviceBaseURL: " + newBMech.getServiceBaseURL());
      System.out.println("methods: ");
      HashMap m2 = newBMech.getBMechMethodMap();
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

    private boolean validGeneralTab(GeneralPane gp)
    {
      if (gp.getBDefPID() == null || gp.getBDefPID().trim().equals(""))
      {
        assertTabPaneMsg("BDefPID is missing on General Tab.", gp.getName());
        return false;
      }
      else if (gp.getBMechLabel() == null || gp.getBMechLabel().trim().equals(""))
      {
        assertTabPaneMsg("Behavior Mechanism Label is missing on General Tab.", gp.getName());
        return false;
      }
      else if (gp.getBMechName() == null || gp.getBMechName().trim().equals(""))
      {
        assertTabPaneMsg("Behavior Mechanism Nickname is missing on General Tab.", gp.getName());
        return false;
      }
      else if (gp.getDCElements().length <= 0)
      {
        assertTabPaneMsg("You must enter at least one DC element on General Tab.", gp.getName());
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
      else if (mp.getBMechMethods().length <=0)
      {
        assertTabPaneMsg("You must enter at least one method on Service Methods Tab.", mp.getName());
        return false;
      }
      else
      {
        Method[] methods = mp.getBMechMethods();
        for (int i=0; i<methods.length; i++)
        {
          if (methods[i].methodProperties == null)
          {
            assertTabPaneMsg(new String("You must enter properties for method: "
              + methods[i].methodName), mp.getName());
            return false;
          }
        }
        return true;
      }
    }

    private boolean validDSInputTab(DatastreamInputPane dsp)
    {
      DSInputRule[] rules = dsp.getDSInputRules();
      for (int i=0; i<rules.length; i++)
      {
        if (rules[i].bindingKeyName == null)
        {
          assertTabPaneMsg(new String("A Datastream parm name is missing"
            + " from column 1 of the table on the Datastream Input Tab"),
            dsp.getName());
          return false;
        }
        else if (rules[i].bindingMIMEType == null)
        {
          assertTabPaneMsg(new String("You must enter MIMEType for"
            + " datastream input parm " + rules[i].bindingKeyName), dsp.getName());
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
        else if (rules[i].bindingInstruction == null)
        {
          assertTabPaneMsg(new String("You must enter Binding Instruction for"
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

    private void assertTabPaneMsg(String msg, String tabpane)
    {
      JOptionPane.showMessageDialog(
        this, new String(msg), new String(tabpane + " Message"),
        JOptionPane.INFORMATION_MESSAGE);
    }
  }
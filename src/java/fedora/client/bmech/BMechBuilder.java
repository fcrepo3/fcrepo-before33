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
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;

import fedora.client.bmech.data.*;
import fedora.client.bmech.xml.*;

public class BMechBuilder extends JInternalFrame
{

    private JTabbedPane tabpane;
    protected BMechTemplate newBMech;
    private int selectedTabPane;

    public static void main(String[] args)
    {
      JFrame frame = new JFrame("BMechBuilder Test");
      frame.addWindowListener(new WindowAdapter() {
          public void windowClosing(WindowEvent e) {System.exit(0);}
      });

      frame.getContentPane().add(new BMechBuilder(),
                                 BorderLayout.CENTER);
      frame.setSize(700, 500);
      frame.setVisible(true);
  }

    public BMechBuilder()
    {
        super("BMechBuilder");
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
        tabpane.addTab("Service Profile", createProfilePane());
        tabpane.addTab("Documentation", createDocPane());

        // General Buttons Panel
        JButton save = new JButton("Save");
        save.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            savePanelInfo();
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

    public void savePanelInfo()
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
            newBMech.setDCRecord(gp.getDCElements());
          }
          else
          {
            return;
          }
        }
        else if (tabs[i].getName().equalsIgnoreCase("MethodsTab"))
        {
          if (validMethodsTab((MethodsPane)tabs[i]))
          {
            MethodsPane mp = (MethodsPane)tabs[i];
            newBMech.setHasBaseURL(mp.hasBaseURL());
            newBMech.setServiceBaseURL(mp.getBaseURL());
            newBMech.setBMechMethodMap(mp.getBMechMethodMap());
            newBMech.setBMechMethods(mp.getBMechMethods());
          }
          else
          {
            return;
          }
        }
      }
      printBMech();
      MethodMapGenerator mmg = new MethodMapGenerator(newBMech);
      mmg.printMethodMap();
      return;
    }

    public void ingestBMech()
    {
      return;
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
    private JComponent createGeneralPane()
    {
      GeneralPane gp = new GeneralPane();
      gp.setName("GeneralTab");
      return gp;
      //return new JLabel("Insert general stuff here.");
    }

    private JComponent createMethodsPane()
    {
      MethodsPane mp = new MethodsPane();
      mp.setName("MethodsTab");
      return mp;
    }

    private JComponent createDSInputPane()
    {
      JLabel jl = new JLabel("Insert Datastream Input Spec stuff here.");
      jl.setName("DSInputTab");
      return jl;
    }

    private JComponent createProfilePane()
    {
      JLabel jl = new JLabel("Insert Service Profile stuff here.");
      jl.setName("ProfileTab");
      return jl;
    }

    private JComponent createDocPane()
    {
      JLabel jl = new JLabel("Insert Documentation stuff here.");
      jl.setName("DocTab");
      return jl;
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
    }

    private boolean validGeneralTab(GeneralPane gp)
    {
      if (gp.getBDefPID() == null || gp.getBDefPID().equalsIgnoreCase(""))
      {
        assertTabPaneMsg("BDefPID is missing on General Tab.", gp.getName());
        return false;
      }
      else if (gp.getBMechLabel() == null || gp.getBMechLabel().equalsIgnoreCase(""))
      {
        assertTabPaneMsg("Behavior Mechanism Label is missing on General Tab.", gp.getName());
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
      if (mp.hasBaseURL() && (mp.getBaseURL() == null || mp.getBaseURL().equals("")))
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


    private void assertTabPaneMsg(String msg, String tabpane)
    {
      JOptionPane.showMessageDialog(
        this, new String(msg), new String(tabpane + " Message"),
        JOptionPane.INFORMATION_MESSAGE);
    }
  }
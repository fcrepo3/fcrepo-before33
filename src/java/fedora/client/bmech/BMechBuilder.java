package fedora.client.bmech;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;

import fedora.client.bmech.data.*;

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
        /*
        // Register a change listener for the tab pane
        tabpane.addChangeListener(new ChangeListener() {
            // This method is called whenever the selected tab changes
            public void stateChanged(ChangeEvent evt) {
                JTabbedPane tabpane = (JTabbedPane)evt.getSource();
                // Get current tab
                selectedTabPane = tabpane.getSelectedIndex();
            }
        });
        */


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
          GeneralPane gp = (GeneralPane)tabs[i];
          newBMech.setbDefPID(gp.getBDefPID());
          newBMech.setbMechLabel(gp.getBMechLabel());
          newBMech.setDCRecord(gp.getDCElements());
        }
        else if (tabs[i].getName().equalsIgnoreCase("MethodsTab"))
        {
          MethodsPane mp = (MethodsPane)tabs[i];
          newBMech.setHasBaseURL(mp.hasBaseURL());
          newBMech.setServiceBaseURL(mp.getBaseURL());
          newBMech.setBMechMethodMap(mp.getBMechMethodMap());
        }
      }
      printBMech();
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
      HashMap m = newBMech.getDCRecord();
      Set elements = m.keySet();
      Iterator it_elements = elements.iterator();
      while (it_elements.hasNext())
      {
        String element = (String)it_elements.next();
        System.out.println(">>element: " + element + "=" + m.get(element));
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
  }
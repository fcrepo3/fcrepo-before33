package fedora.client;

import javax.help.*;
import javax.help.Map.ID;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.event.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

import fedora.swing.jhelp.SimpleHelpBroker;
import fedora.swing.jhelp.SimpleContentViewerUI;
import fedora.swing.mdi.MDIDesktopPane;
import fedora.swing.mdi.WindowMenu;

import fedora.client.actions.ExportObject;
import fedora.client.actions.PurgeObject;
import fedora.client.actions.ViewObjectXML;
import fedora.client.actions.ViewDatastreams;
import fedora.client.console.access.AccessConsole;
import fedora.client.console.management.ManagementConsole;
import fedora.client.export.AutoExporter;
import fedora.client.ingest.AutoIngestor;
import fedora.client.purge.AutoPurger;
import fedora.client.search.ResultFrame;
import fedora.client.search.Search;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.client.bmech.BDefBuilder;
import fedora.client.bmech.BMechBuilder;

// wdn >
import fedora.client.BatchBuildGUI;
import fedora.client.BatchIngestGUI;
// < wdn

/**
 *
 * <p><b>Title:</b> Administrator.java</p>
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
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class Administrator extends JFrame {

    private static MDIDesktopPane s_desktop;

    private JDialog m_aboutDialog;

    private ID m_homeID;
    private SimpleHelpBroker m_helpBroker;

    private static File s_lastDir;
    /*package*/ static File batchtoolLastDir;

    ClassLoader cl;

    private static Administrator s_instance;
    private JLabel m_aboutPic;
    private JLabel m_aboutText;
    private static String s_host;
    private static int s_port;
    private static String s_user;
    private static String s_pass;

    public Administrator(String host, int port, String user, String pass) {
        super("Fedora Administrator - " + user + " using server at " + host + ":" + port);
        if (System.getProperty("fedora.home")!=null) {
            File f=new File(System.getProperty("fedora.home"));
            if (f.exists() && f.isDirectory()) {
                s_lastDir=f;
            }
        }
        s_host=host;
        s_port=port;
        s_user=user;
        s_pass=pass;

        cl=this.getClass().getClassLoader();

        m_aboutPic=new JLabel(new ImageIcon(cl.getResource("images/fedora/aboutadmin.gif")));
        m_aboutText=new JLabel("<html>Copyright 2002, 2003, The Rector and Visitors of the<p>"
                + "University of Virginia and Cornell University<p><p>"
                + "This program is subject to the Mozilla Public License, Version 1.1<p>"
                + "(the \"License\"); you may not use this program except in compliance<p>"
                + "with the License. You may obtain a copy of the License at<p>"
                + "http://www.mozilla.org/MPL<p><p>"
                + "Software distributed under the License is distributed on an \"AS IS\"<p>"
                + "basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.<p>"
                + "See the License for the specific language governing rights and<p>"
                + "limitations under the License.<p>"
                + "<p>"
                + "Version: 1.0<p>Release Date: "
                + "May 16, 2003<p>"
                + "See http://www.fedora.info/ for "
                + "more information.");

        m_aboutText.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        JPanel splashPicAndText=new JPanel();
        splashPicAndText.setLayout(new BorderLayout());
        splashPicAndText.setBorder(BorderFactory.createLineBorder(Color.black, 5));
        splashPicAndText.add(m_aboutPic, BorderLayout.CENTER);
        splashPicAndText.add(m_aboutText, BorderLayout.SOUTH);
	    JWindow splashScreen = new JWindow();
        splashScreen.getContentPane().add(splashPicAndText);
        splashScreen.pack();
        int xSize=splashScreen.getWidth();
        int ySize=splashScreen.getHeight();
        Dimension screenSize=getToolkit().getScreenSize();
        int xLoc=(screenSize.width/2) - (xSize/2);
        int yLoc=(screenSize.height/2) - (ySize/2);
        splashScreen.setBounds(xLoc, yLoc, xSize, ySize);
        splashScreen.setVisible(true);

        setIconImage(new ImageIcon(cl.getResource("images/fedora/fedora-icon16.gif")).getImage());
        JPanel mainPanel=new JPanel();
        mainPanel.setLayout(new BorderLayout());
        s_desktop=new MDIDesktopPane();
        s_desktop.setVisible(true);
        mainPanel.add(new JScrollPane(s_desktop), BorderLayout.CENTER);

        getContentPane().add(mainPanel);
        setJMenuBar(createMenuBar());


        //Make dragging faster:
        //s_desktop.putClientProperty("JDesktopPane.dragMode", "outline");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                    dispose();
                    System.exit(0);
            }
        });

        splashScreen.setVisible(false);
        s_instance=this;
    }

    public static JDesktopPane getDesktop() {
        return s_desktop;
    }

    public static Administrator getInstance() {
        return s_instance;
    }

    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu=new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.setToolTipText("Contains commands for creating, opening, closing, and saving Digital Objects");
        JMenuItem fileIngest=new JMenuItem("Ingest...",KeyEvent.VK_I);
        fileIngest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                ActionEvent.CTRL_MASK));
        fileIngest.setToolTipText("Ingests a serialized Digitial Object.");
        fileIngest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileIngestAction();
            }
        });

        JMenuItem fileViewDatastreams=new JMenuItem(new ViewDatastreams());
        fileViewDatastreams.setMnemonic(KeyEvent.VK_D);
        fileViewDatastreams.setToolTipText("Launches a viewer/editor for the datastreams of an object.");
        fileViewDatastreams.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                ActionEvent.CTRL_MASK));

        JMenuItem fileView=new JMenuItem(new ViewObjectXML());
        fileView.setMnemonic(KeyEvent.VK_V);
        fileView.setToolTipText("Launches an XML viewer for an object.");
        fileView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                ActionEvent.CTRL_MASK));

        JMenuItem fileExport=new JMenuItem(new ExportObject());
        fileExport.setMnemonic(KeyEvent.VK_E);
        fileExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,
                ActionEvent.CTRL_MASK));
        fileExport.setToolTipText("Exports a serialized Digitial Object to disk.");
        JMenuItem filePurge=new JMenuItem(new PurgeObject());
        filePurge.setMnemonic(KeyEvent.VK_P);
        filePurge.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                ActionEvent.CTRL_MASK));
        filePurge.setToolTipText("Permanently removes a Digitial Object from the repository.");

        JMenuItem fileSave=new JMenuItem("Save",KeyEvent.VK_S);
        fileSave.setToolTipText("Saves the current Digital Object to the Repository it was opened from");
        fileSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK));
        JMenuItem fileSaveTo=new JMenuItem("Save To...",KeyEvent.VK_T);
        fileSaveTo.setToolTipText("Saves the current Digital Object to a Repository other than the one it was opened from");
        JMenuItem fileSaveAll=new JMenuItem("Save All",KeyEvent.VK_L);
        fileSaveAll.setToolTipText("Saves all opened Digital Objects to the Repository(s) they were opened from");
        JMenuItem fileExit=new JMenuItem("Exit",KeyEvent.VK_X);
        fileExit.setToolTipText("Quits the FEDORA Administrator application");

        fileExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });

        fileMenu.add(fileIngest);
        fileMenu.add(fileView);
        fileMenu.add(fileViewDatastreams);
        fileMenu.add(fileExport);
        fileMenu.add(filePurge);
        fileMenu.addSeparator();
        fileMenu.add(fileExit);

        menuBar.add(fileMenu);

        JMenu toolsMenu=new JMenu("Tools");
        toolsMenu.setMnemonic(KeyEvent.VK_T);

        JMenuItem toolsSearch=new JMenuItem("Search/Browse Repository",KeyEvent.VK_S);
        toolsSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createSearchRepository();
            }
        });
        toolsMenu.add(toolsSearch);

	//wdn >
        //JMenu toolsBatchSubMenu=new JMenu("Batch", KeyEvent.VK_B);
        JMenu toolsBatchSubMenu=new JMenu("Batch");

        JMenuItem toolsBatchBuild=new JMenuItem("Build Batch"/*, KeyEvent.VK_A*/);
        toolsBatchBuild.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createBatchBuildConsole();
            }
        });
        toolsBatchSubMenu.add(toolsBatchBuild);

        JMenuItem toolsBatchBuildIngest=new JMenuItem("Build and Ingest Batch"/*, KeyEvent.VK_A*/);
        toolsBatchBuildIngest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createBatchBuildIngestConsole();
            }
        });
        toolsBatchSubMenu.add(toolsBatchBuildIngest);

        JMenuItem toolsBatchIngest=new JMenuItem("Ingest Batch"/*, KeyEvent.VK_A*/);
        toolsBatchIngest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createBatchIngestConsole();
            }
        });
        toolsBatchSubMenu.add(toolsBatchIngest);
        toolsMenu.add(toolsBatchSubMenu);
        // < wdn

        JMenu toolsAdvanced=new JMenu("Advanced");
        JMenuItem toolsManagement=new JMenuItem("Management Console",KeyEvent.VK_M);
        toolsManagement.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createManagementConsole();
            }
        });
        toolsAdvanced.add(toolsManagement);

        JMenuItem toolsAccess=new JMenuItem("Access Console",KeyEvent.VK_A);
        toolsAccess.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAccessConsole();
            }
        });
        toolsAdvanced.add(toolsAccess);
        toolsMenu.add(toolsAdvanced);

        menuBar.add(toolsMenu);

        // [B]uilders
        JMenu buildersMenu=new JMenu("Builders");
        buildersMenu.setMnemonic(KeyEvent.VK_B);
        buildersMenu.setToolTipText("Tools to build objects");

        JMenuItem buildersBDef=new JMenuItem("BDef Builder",KeyEvent.VK_D);
        buildersBDef.setToolTipText("Create a new Behavior Definition Object");
        buildersBDef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createBDefBuilder();
            }
        });
        buildersMenu.add(buildersBDef);

        JMenuItem buildersBMech=new JMenuItem("BMech Builder",KeyEvent.VK_M);
        buildersBMech.setToolTipText("Create a new Behavior Mechanism Object");
        buildersBMech.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createBMechBuilder();
            }
        });
        buildersMenu.add(buildersBMech);
        menuBar.add(buildersMenu);


        WindowMenu windowMenu=new WindowMenu(s_desktop, "Window");
        windowMenu.setMnemonic(KeyEvent.VK_W);
        menuBar.add(windowMenu);

        // [H]elp
        JMenu helpMenu=new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        JMenuItem helpContents=new JMenuItem("Documentation",KeyEvent.VK_D);
        String portPart="";
        if (getPort()!=80) portPart=":" + getPort();
        String documentationURL="http://" + getHost() + portPart + "/userdocs/";
        helpContents.setToolTipText("See " + documentationURL);

        helpContents.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              String portPart="";
              if (getPort()!=80) portPart=":" + getPort();
              String documentationURL="http://" + getHost() + portPart + "/userdocs/";
              JOptionPane.showMessageDialog(getDesktop(),
                      "For documentation, see " + documentationURL,
                      "Fedora Documentation",
                      JOptionPane.INFORMATION_MESSAGE);
          }
        });

/**                        helpset disabled...currently pointing to doc. url.
   HelpSet hs;
   try {
      URL hsu = cl.getResource("help/jhelpset.hs");
//      URL hsURL=HelpSet.findHelpSet(null, "help/fedora.hs");
      hs = new HelpSet(this.getClass().getClassLoader(), hsu);
      m_homeID=hs.getHomeID();
      m_helpBroker=new SimpleHelpBroker(hs,new ImageIcon(cl.getResource("images/standard/general/Help16.gif")).getImage());
      m_helpBroker.setSize(new Dimension(680, 550));
      //helpContents.addActionListener(new CSH.DisplayHelpFromSource(hb));
      helpContents.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             m_helpBroker.setLocation(getCenteredPos(m_helpBroker.getSize().width, m_helpBroker.getSize().height));
		     m_helpBroker.setDisplayed(true);
             try {
             m_helpBroker.setCurrentID(m_homeID);
             m_helpBroker.ensureContentPanelDrawn(m_homeID);
             } catch (Exception ex) { }
          }
      });
   } catch (Exception ee) {
      System.out.println("Help could not be loaded:" + ee.getClass().getName() + ":" + ee.getMessage());
   }
*/

        JFrame dummy=new JFrame();
        dummy.setIconImage(new ImageIcon(cl.getResource("images/standard/general/About16.gif")).getImage());
        m_aboutDialog=new JDialog(dummy, "About Fedora Administrator", true);

        m_aboutDialog.getContentPane().add(m_aboutPic, BorderLayout.CENTER);
        JButton aboutClose=new JButton("Close");

        JPanel infoAndButton=new JPanel();
        infoAndButton.setLayout(new BorderLayout());
        infoAndButton.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        infoAndButton.add(m_aboutText);


        JPanel buttonPane=new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(aboutClose);
        infoAndButton.add(buttonPane, BorderLayout.SOUTH);

        m_aboutDialog.getContentPane().add(infoAndButton, BorderLayout.SOUTH);
        aboutClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_aboutDialog.hide();
            }
        });
        m_aboutDialog.pack();



        JMenuItem helpAbout=new JMenuItem("About Fedora Administrator",KeyEvent.VK_A);
        helpAbout.setToolTipText("Gives brief information this application");
        helpAbout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_aboutDialog.setLocation(getCenteredPos(m_aboutDialog.getWidth(), m_aboutDialog.getHeight()));
                m_aboutDialog.show();
            }
        });

        helpMenu.add(helpContents);
        helpMenu.addSeparator();
        helpMenu.add(helpAbout);

        menuBar.add(helpMenu);

        return menuBar;
    }

    public static File getLastDir() {
        return s_lastDir;
    }

    public static void setLastDir(File f) {
        s_lastDir=f;
    }

    protected void fileIngestAction() {
        try {
            JFileChooser browse;
            if (s_lastDir==null) {
                browse=new JFileChooser();
            } else {
                browse=new JFileChooser(s_lastDir);
            }
            int returnVal = browse.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = browse.getSelectedFile();
                s_lastDir=file.getParentFile(); // remember the dir for next time
                FileInputStream in=new FileInputStream(file.getAbsolutePath());
                String host;
                int port;
                String logMessage;
                host=s_host;
                port=s_port;
                logMessage="First import.";
                AutoIngestor ingestor=new AutoIngestor(host, port, s_user, s_pass);
                String pid=ingestor.ingestAndCommit(in, logMessage);
                JOptionPane.showMessageDialog(this,
                        "Ingest succeeded.  PID='" + pid + "'.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getClass().getName() + ": " + e.getMessage(),
                    "Ingest Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void fileExportAction() {
        try {
            String pid=JOptionPane.showInputDialog("Enter the PID.");
            JFileChooser browse;
            if (s_lastDir==null) {
                browse=new JFileChooser();
            } else {
                browse=new JFileChooser(s_lastDir);
            }
            browse.setApproveButtonText("Export");
            browse.setApproveButtonMnemonic('E');
            browse.setApproveButtonToolTipText("Exports to the selected file.");
            browse.setDialogTitle("Export to...");
            int returnVal = browse.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = browse.getSelectedFile();
                s_lastDir=file.getParentFile(); // remember the dir for next time
                String host;
                int port;
                host=s_host;
                port=s_port;
                AutoExporter exporter=new AutoExporter(host, port, s_user, s_pass);
                exporter.export(pid, new FileOutputStream(file));
                JOptionPane.showMessageDialog(this,
                        "Export succeeded.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getClass().getName() + ": " + e.getMessage(),
                    "Export Failure",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void createBDefBuilder() {
        BDefBuilder frame = new BDefBuilder(
          s_host, s_port, s_user, s_pass, s_lastDir);
        frame.setVisible(true);
        s_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }

    protected void createBMechBuilder() {
        BMechBuilder frame = new BMechBuilder(
          s_host, s_port, s_user, s_pass, s_lastDir);
        frame.setVisible(true);
        s_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }

    protected void createSearchRepository() {
        Search frame=new Search();
/*        FieldSearchQuery query=new FieldSearchQuery();
        query.setTerms("*");
        String[] fields=new String[4];
        fields[0]="pid";
        fields[1]="mDate";
        fields[2]="bDef";
        fields[3]="title";
        ResultFrame frame=new ResultFrame("Search Results", fields, 10, query);
        */
        frame.setVisible(true);
        s_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }

    protected void createManagementConsole() {
        ManagementConsole frame=new ManagementConsole(this);
        frame.setVisible(true);
        s_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }

    protected void createAccessConsole() {
        AccessConsole frame=new AccessConsole(this);
        frame.setVisible(true);
        s_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }

    // wdn >
    protected void createBatchBuildConsole() {
        BatchBuildGUI frame=new BatchBuildGUI(this, s_desktop);
        frame.setVisible(true);
        s_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
    protected void createBatchBuildIngestConsole() {
        BatchBuildIngestGUI frame=new BatchBuildIngestGUI(this, s_desktop, s_host, s_port, s_user, s_pass);
        frame.setVisible(true);
        s_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
    protected void createBatchIngestConsole() {
        BatchIngestGUI frame=new BatchIngestGUI(this, s_desktop, s_host, s_port, s_user, s_pass);
        frame.setVisible(true);
        s_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
    // < wdn

    public Point getCenteredPos(int xSize, int ySize) {
        Dimension screenSize=getToolkit().getScreenSize();
        int maxXPos=screenSize.width-xSize;
        int maxYPos=screenSize.height-ySize;
        int centerX=getX()+(getWidth()/2);
        int centerY=getY()+(getHeight()/2);
        int prefXPos=centerX-(xSize/2);
        int prefYPos=centerY-(ySize/2);
        if (prefXPos<0) prefXPos=0;
        if (prefXPos>maxXPos) prefXPos=maxXPos;
        if (prefYPos<0) prefYPos=0;
        if (prefYPos>maxYPos) prefYPos=maxYPos;
        return new Point(prefXPos, prefYPos);
    }

    public static String getHost() {
        return s_host;
    }

    public static int getPort() {
        return s_port;
    }

    public static String getUser() {
        return s_user;
    }

    public static String getPass() {
        return s_pass;
    }

    public static void main(String[] args) {
    /*
        try {
            UIManager.put("AuditoryCues.playList",
                    UIManager.get("AuditoryCues.allAuditoryCues"));
		    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (UnsupportedLookAndFeelException exc) {
		} catch (IllegalAccessException exc) {
		    System.out.println("IllegalAccessException Error:" + exc);
		} catch (ClassNotFoundException exc) {
		    System.out.println("ClassNotFoundException Error:" + exc);
		} catch (InstantiationException exc) {
		    System.out.println("InstantiateException Error:" + exc);
		}
        */

        // turn off obnoxious Axis stdout/err messages
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        String host="localhost";
        int port=8080;
        String user="fedoraAdmin";
        String pass="fedoraAdmin";
        if (args.length>0) {
            host=args[0];
            if (args.length>1) {
                try {
                    port=Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    System.out.println("Warning: " + args[1] + " is not a valid port number.  Using default.");
                }
                if (args.length>2) {
                    user=args[2];
                    if (args.length>3) {
                        pass=args[3];
                    }
                }
            }
        }
        System.out.println("Using Fedora server at " + host + ":" + port + " with userId=" + user + " and password=[not displayed].");
        Administrator administrator=new Administrator(host, port, user, pass);

        int xSize=820;
        int ySize=620;
        Dimension screenSize=administrator.getToolkit().getScreenSize();
        int xLoc=(screenSize.width/2) - (xSize/2);
        int yLoc=(screenSize.height/2) - (ySize/2);
        administrator.setBounds(xLoc, yLoc, xSize, ySize);
        administrator.setVisible(true);
    }
}


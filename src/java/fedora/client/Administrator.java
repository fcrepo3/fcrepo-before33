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
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import edu.cornell.dlrg.swing.jhelp.SimpleHelpBroker;
import edu.cornell.dlrg.swing.jhelp.SimpleContentViewerUI;
import edu.cornell.dlrg.swing.mdi.MDIDesktopPane;
import edu.cornell.dlrg.swing.mdi.WindowMenu;

import fedora.client.console.access.AccessConsole;
import fedora.client.console.management.ManagementConsole;
import fedora.client.ingest.AutoIngestor;

public class Administrator extends JFrame {

    private MDIDesktopPane m_desktop;
    
    private JDialog m_aboutDialog;
    
    private ID m_homeID;
    private SimpleHelpBroker m_helpBroker;
    
    private File s_lastDir;
   
    ClassLoader cl;
    
    private JLabel m_aboutPic;
    private JLabel m_aboutText;
    private String m_host;
    private int m_port;

    public Administrator(String host, int port) {
        super("Fedora Administrator - Server at " + host + ":" + port);
        m_host=host;
        m_port=port;
        
        cl=this.getClass().getClassLoader();

        m_aboutPic=new JLabel(new ImageIcon(cl.getResource("images/fedora/aboutadmin.gif")));
        m_aboutText=new JLabel("<html>Copyright 2002 University of "
                + "Virginia and Cornell University.<p>This software was "
                + "made possible by a grant from the<p>Andrew W. Mellon "
                + "Foundation.<p><p>Version: N/A<p>Release Date: "
                + "Unreleased.<p><p>Note: This is a pre-release version "
                + "of Fedora.<P>See http://www.fedora.info/ for "
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
        m_desktop=new MDIDesktopPane();
        m_desktop.setVisible(true);
        mainPanel.add(new JScrollPane(m_desktop), BorderLayout.CENTER);
        JToolBar toolBar=new JToolBar("Toolbar");
        toolBar.add(new JButton(new ImageIcon(cl.getResource("images/standard/general/New24.gif"))));
        toolBar.add(new JButton(new ImageIcon(cl.getResource("images/standard/general/Open24.gif"))));
        toolBar.add(new JButton(new ImageIcon(cl.getResource("images/standard/general/Save24.gif"))));
        toolBar.add(new JButton(new ImageIcon(cl.getResource("images/standard/general/SaveAs24.gif"))));
        toolBar.add(new JButton(new ImageIcon(cl.getResource("images/standard/general/SaveAll24.gif"))));
        toolBar.add(new JButton(new ImageIcon(cl.getResource("images/standard/general/Help24.gif"))));
        mainPanel.add(toolBar, BorderLayout.NORTH);

        getContentPane().add(mainPanel);
        setJMenuBar(createMenuBar());
        

        //Make dragging faster:
        //m_desktop.putClientProperty("JDesktopPane.dragMode", "outline");
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                    dispose();
                    System.exit(0);
            }
        });
        
        splashScreen.setVisible(false);
    }

    protected JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // [F]ile
        //   [N]ew...
        //   [O]pen...
        //   [C]lose
        //   [S]ave
        //   Save [T]o...
        //   Save A[l]l
        JMenu fileMenu=new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.setToolTipText("Contains commands for creating, opening, closing, and saving Digital Objects");
        JMenuItem fileNew=new JMenuItem("New...",KeyEvent.VK_N);
        fileNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, 
                ActionEvent.CTRL_MASK));
        fileNew.setToolTipText("Creates a new, empty Digital Object and opens it for editing");
        fileNew.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileNewAction();
            }
        });
        JMenuItem fileIngest=new JMenuItem("Ingest...",KeyEvent.VK_I);
        fileIngest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,
                ActionEvent.CTRL_MASK));
        fileIngest.setToolTipText("Ingests a serialized Digitial Object.");
        fileIngest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileIngestAction();
            }
        });
        JMenuItem fileOpen=new JMenuItem("Open...",KeyEvent.VK_O);
        fileOpen.setToolTipText("Opens an existing Digital Object");
        fileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, 
                ActionEvent.CTRL_MASK));
        JMenuItem fileClose=new JMenuItem("Close",KeyEvent.VK_C);
        fileClose.setToolTipText("Closes the current Digital Object");
        fileClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, 
                ActionEvent.CTRL_MASK));
//        ImageIcon BLANK_16x16_ICON=new ImageIcon("blank16x16.gif");
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
        
        fileMenu.add(fileNew); 
        fileMenu.add(fileIngest); 
        fileMenu.add(fileOpen); 
        fileMenu.add(fileClose); 
        fileMenu.addSeparator();
        fileMenu.add(fileSave); 
        fileMenu.add(fileSaveTo); 
        fileMenu.add(fileSaveAll); 
        fileMenu.addSeparator();
//        fileMenu.add(filePreferences);
//        fileMenu.addSeparator();
        fileMenu.add(fileExit); 

        menuBar.add(fileMenu);
        
        JMenu editMenu=new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        JMenuItem editDelete=new JMenuItem("Delete",KeyEvent.VK_D);
        editMenu.add(editDelete);
        menuBar.add(editMenu);
        
        JMenu viewMenu=new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        JMenuItem viewToolbar=new JMenuItem("Toolbar",KeyEvent.VK_T);
        viewMenu.add(viewToolbar);
        JMenuItem viewTooltips=new JMenuItem("Tooltips",KeyEvent.VK_I);
        viewMenu.add(viewTooltips);
        menuBar.add(viewMenu);
        
        JMenu toolsMenu=new JMenu("Tools");
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        
        JMenuItem toolsManagement=new JMenuItem("Management Console",KeyEvent.VK_M);
        toolsManagement.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createManagementConsole();
            }
        });
        toolsMenu.add(toolsManagement);
        
        JMenuItem toolsAccess=new JMenuItem("Access Console",KeyEvent.VK_A);
        toolsAccess.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAccessConsole();
            }
        });
        toolsMenu.add(toolsAccess);
        
        menuBar.add(toolsMenu);
        
        
        WindowMenu windowMenu=new WindowMenu(m_desktop, "Window");
        windowMenu.setMnemonic(KeyEvent.VK_W);
        menuBar.add(windowMenu);
       
        // [H]elp
        JMenu helpMenu=new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        JMenuItem helpContents=new JMenuItem("Fedora Manual",KeyEvent.VK_M);
        helpContents.setToolTipText("Shows the Fedora Manual");
        
        
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

    protected void fileNewAction() {
        // The below code opens a not-even-close-to-implemented GUI window for editing an object
        /*
        DigitalObjectEditor frame = new DigitalObjectEditor(new DigitalObject(), m_desktop);
        frame.setVisible(true);
        m_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
        */
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
                host=m_host;
                port=m_port;
                logMessage="First import.";
                AutoIngestor ingestor=new AutoIngestor(host, port);
                String pid=ingestor.ingestAndCommit(in, logMessage);
                JOptionPane.showMessageDialog(this,
                        "Ingest succeeded.  PID='" + pid + "'.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getClass().getName() + ": " + e.getMessage(),
                    "Ingest Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    protected void createManagementConsole() {
        ManagementConsole frame=new ManagementConsole(this);
        frame.setVisible(true);
        m_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
    
    protected void createAccessConsole() {
        AccessConsole frame=new AccessConsole(this);
        frame.setVisible(true);
        m_desktop.add(frame);
        try {
            frame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {}
    }
    
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
        if (args.length>0) {
            host=args[0];
            if (args.length>1) {
                try {
                    port=Integer.parseInt(args[1]);
                } catch (NumberFormatException nfe) {
                    System.out.println("Warning: " + args[1] + " is not a valid port number.  Using default.");
                }
            }
        }
        System.out.println("Using Fedora server at " + host + ":" + port);
        Administrator administrator=new Administrator(host, port);

        int xSize=710;
        int ySize=580;
        Dimension screenSize=administrator.getToolkit().getScreenSize();
        int xLoc=(screenSize.width/2) - (xSize/2);
        int yLoc=(screenSize.height/2) - (ySize/2);
        administrator.setBounds(xLoc, yLoc, xSize, ySize);
        administrator.setVisible(true);
    }
}


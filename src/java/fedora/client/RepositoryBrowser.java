package fedora.client;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.xml.rpc.ServiceException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import edu.cornell.dlrg.swing.jtable.DefaultSortTableModel;
import edu.cornell.dlrg.swing.jtable.JSortTable;
import fedora.client.Administrator;
import fedora.client.console.Console;
import fedora.client.console.ConsoleSendButtonListener;
import fedora.client.console.ConsoleCommand;
import fedora.client.console.ServiceConsoleCommandFactory;
import fedora.client.list.AutoLister;
import fedora.server.management.FedoraAPIMServiceLocator;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.ObjectInfo;

public class RepositoryBrowser
        extends JInternalFrame {
        
    private Administrator m_mainFrame;
    private static FedoraAPIMServiceLocator m_locator=new FedoraAPIMServiceLocator();
    
    public RepositoryBrowser(Administrator mainFrame) {
        super("Repository Browser",
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
        m_mainFrame=mainFrame;
       
        String[] columnNames = {"PID", 
                                "Label",
                                "Type",
                                "Content Model",
                                "State",
                                "Locked By",
                                "Created",
                                "Last Modified"};

        try {
        AutoLister a=new AutoLister(mainFrame.getHost(), mainFrame.getPort());
        Map m=a.list(null);
        Object[][] data=new Object[m.size()][8];
        Iterator pidIter=m.keySet().iterator();   
        int i=0;
        SimpleDateFormat df=new SimpleDateFormat();
        while (pidIter.hasNext()) {
            String pid=(String) pidIter.next();
            ObjectInfo inf=(ObjectInfo) m.get(pid);
            data[i][0]=pid;
            data[i][1]=inf.getLabel();
            data[i][2]=inf.getFoType();
            data[i][3]=inf.getContentModelId();
            data[i][4]=inf.getState();
            data[i][5]=inf.getLockedBy();
            data[i][6]=df.format(inf.getCreateDate().getTime());
            data[i][7]=df.format(inf.getLastModDate().getTime());
            i++;
        }

        JSortTable table=new JSortTable(new DefaultSortTableModel(data, columnNames));
        table.setPreferredScrollableViewportSize(new Dimension(400, 400));
        table.setShowVerticalLines(false);
        table.setCellSelectionEnabled(false);
        table.setRowSelectionAllowed(true);
        table.setUI(new RepositoryBrowser.BrowserTableUI());

        JScrollPane browsePanel = new JScrollPane(table);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(browsePanel, BorderLayout.CENTER);

        setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Zoom16.gif")));

        setSize(400,400);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ":" + e.getMessage());
        }
    }
    
    public class BrowserTableUI 
            extends BasicTableUI {
        protected MouseInputListener createMouseInputListener() {
            return new BasicTableUI.MouseInputHandler() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e) ) {
                        //do your stuff here...    
                        System.out.println("Right click");
                    } else {
                        // not a right click
                        super.mousePressed(e);    
                    }  
                } 
            };
        }
    }
    
}

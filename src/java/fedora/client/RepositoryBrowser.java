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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Font;
import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import edu.cornell.dlrg.swing.jtable.DefaultSortTableModel;
import edu.cornell.dlrg.swing.jtable.JSortTable;
import fedora.client.Administrator;
import fedora.client.actions.ExportObject;
import fedora.client.actions.PurgeObject;
import fedora.client.actions.ViewObjectXML;
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
        
    private static FedoraAPIMServiceLocator m_locator=new FedoraAPIMServiceLocator();

    private JSortTable m_table;
    
    public RepositoryBrowser() {
        super("Repository Browser",
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
       
        String[] columnNames = {"PID", 
                                "Label",
                                "Type",
                                "Content Model",
                                "State",
                                "Locked By",
                                "Created",
                                "Last Modified"};

        try {
        AutoLister a=new AutoLister(Administrator.getHost(), Administrator.getPort());
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

        DefaultSortTableModel model=new DefaultSortTableModel(data, columnNames);
        m_table=new JSortTable(model);
        m_table.setPreferredScrollableViewportSize(new Dimension(400, 400));
        m_table.setShowVerticalLines(false);
        m_table.setCellSelectionEnabled(false);
        m_table.setRowSelectionAllowed(true);
        m_table.setUI(new RepositoryBrowser.BrowserTableUI());

        JScrollPane browsePanel = new JScrollPane(m_table);
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
                        int rowNum=m_table.rowAtPoint(new Point(e.getX(), e.getY()));
                        if (rowNum >= 0) {
                            int[] sRows=m_table.getSelectedRows();
                            boolean clickedOnSelected=false;
                            HashSet pids=new HashSet();
                            for (int i=0; i<sRows.length; i++) {
                                if (sRows[i]==rowNum) {
                                    clickedOnSelected=true;
                                }
                                pids.add(m_table.getValueAt(sRows[i], m_table.convertColumnIndexToView(0)));
                            }
                            if (!clickedOnSelected) {
                                pids=new HashSet();
                                m_table.clearSelection();
                                m_table.addRowSelectionInterval(rowNum, rowNum);
                                pids.add(m_table.getValueAt(rowNum, m_table.convertColumnIndexToView(0)));
                            }
                            if (pids.size()==1) {
                                Iterator pidIter=pids.iterator();
                                new RepositoryBrowser.SingleSelectionPopup(
                                        (String) pidIter.next())
                                        .show(e.getComponent(), 
                                        e.getX(), e.getY());
                            } else {
                                new RepositoryBrowser.MultiSelectionPopup(pids)
                                        .show(e.getComponent(), 
                                        e.getX(), e.getY());
                            }
                        }
                    } else {
                        // not a right click
                        super.mousePressed(e);    
                    }  
                } 
            };
        }
    }
    
    public class SingleSelectionPopup
            extends JPopupMenu {
            
        private String m_pid;
        
        public SingleSelectionPopup(String pid) {
            super();
            m_pid=pid;
            JMenuItem i1=new JMenuItem(new ViewObjectXML(pid));
            i1.setMnemonic(KeyEvent.VK_V);
            i1.setToolTipText("Launches an XML viewer for the selected object.");
            JMenuItem i2=new JMenuItem(new ExportObject(pid));
            i2.setMnemonic(KeyEvent.VK_E);
            i2.setToolTipText("Exports the selected object.");
            JMenuItem i3=new JMenuItem(new PurgeObject(pid));
            i3.setMnemonic(KeyEvent.VK_P);
            i3.setToolTipText("Removes the selected object from the repository.");
            add(i1);
            add(i2);
            add(i3);
        }
    }
    
    public class MultiSelectionPopup
            extends JPopupMenu {
            
        private Set m_pids;
        
        public MultiSelectionPopup(Set pids) {
            super();
            m_pids=pids;
            JMenuItem i1=new JMenuItem(new ViewObjectXML(pids));
            i1.setMnemonic(KeyEvent.VK_V);
            i1.setToolTipText("Launches an XML viewer for the selected objects.");
            JMenuItem i2=new JMenuItem(new ExportObject(pids));
            i2.setMnemonic(KeyEvent.VK_E);
            i2.setToolTipText("Exports the selected objects.");
            JMenuItem i3=new JMenuItem(new PurgeObject(pids));
            i3.setMnemonic(KeyEvent.VK_P);
            i3.setToolTipText("Removes the selected objects from the repository.");
            add(i1);
            add(i2);
            add(i3);
        }
    }
}

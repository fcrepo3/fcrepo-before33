package fedora.client.search;

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
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fedora.swing.jtable.DefaultSortTableModel;
import fedora.swing.jtable.JSortTable;
import fedora.client.Administrator;
import fedora.client.actions.ExportObject;
import fedora.client.actions.PurgeObject;
import fedora.client.actions.ViewObjectXML;
import fedora.client.console.Console;
import fedora.client.console.ConsoleSendButtonListener;
import fedora.client.console.ConsoleCommand;
import fedora.client.console.ServiceConsoleCommandFactory;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;

public class ResultFrame
        extends JInternalFrame {
        
    private JSortTable m_table;
    
    public ResultFrame(String frameTitle, String[] displayFields, String sessionToken) {
    }
    
    public ResultFrame(String frameTitle, String[] displayFields, int maxResults, FieldSearchQuery query) {
        super(frameTitle,
              true, //resizable
              true, //closable
              true, //maximizable
              true);//iconifiable
       
        // Make sure resultFields has pid, even though they may not 
        // want to display it. Also, signal that the pid should or
        // should not be displayed.
        boolean displayPid=false;
        for (int i=0; i<displayFields.length; i++) {
            if (displayFields[i].equals("pid")) {
                displayPid=true;
            }
        }
        String[] resultFields;
        if (displayPid) {
            resultFields=displayFields;
        } else {
            resultFields=new String[displayFields.length+1];
            resultFields[0]="pid";
            for (int i=1; i<displayFields.length+1; i++) {
                resultFields[i]=displayFields[i-1];
            }
        }

        try {
            AutoFinder a=new AutoFinder(Administrator.getHost(), Administrator.getPort(), Administrator.getUser(), Administrator.getPass());
/*            FieldSearchResult fsr=a.findObjects();

            Map m=a.list(pidPattern, foType, lockedByPattern, state,
                    labelPattern, contentModelIdPattern, createDateMin,
                    createDateMax, lastModDateMin, lastModDateMax);
            Object[][] data=new Object[m.size()][displayFields.size];
            Iterator pidIter=m.keySet().iterator();   
            int i=0;
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
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
    
            DefaultSortTableModel model=new DefaultSortTableModel(data, displayFields);
            m_table=new JSortTable(model);
            m_table.setPreferredScrollableViewportSize(new Dimension(400, 400));
            m_table.setShowVerticalLines(false);
            m_table.setCellSelectionEnabled(false);
            m_table.setRowSelectionAllowed(true);
            m_table.setUI(new ResultFrame.BrowserTableUI());
    
            JScrollPane browsePanel = new JScrollPane(m_table);
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(browsePanel, BorderLayout.CENTER);
*/
    
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
                                new ResultFrame.SingleSelectionPopup(
                                        (String) pidIter.next())
                                        .show(e.getComponent(), 
                                        e.getX(), e.getY());
                            } else {
                                new ResultFrame.MultiSelectionPopup(pids)
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

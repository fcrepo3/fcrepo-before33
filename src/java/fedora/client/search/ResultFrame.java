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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import fedora.swing.jtable.DefaultSortTableModel;
import fedora.swing.jtable.JSortTable;
import fedora.client.Administrator;
import fedora.client.actions.ExportObject;
import fedora.client.actions.PurgeObject;
import fedora.client.actions.ViewObject;
import fedora.client.actions.ViewObjectXML;
import fedora.client.console.Console;
import fedora.client.console.ConsoleSendButtonListener;
import fedora.client.console.ConsoleCommand;
import fedora.client.console.ServiceConsoleCommandFactory;
import fedora.server.types.gen.ObjectFields;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;

/**
 *
 * <p><b>Title:</b> ResultFrame.java</p>
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
public class ResultFrame
        extends JInternalFrame {

    private JSortTable m_table;
    private String[] m_rowPids;
    private JButton m_moreButton;
    public static SimpleDateFormat FORMATTER=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static AutoFinder s_finder=null;

    public ResultFrame(String frameTitle, String[] displayFields, String sessionToken) {
        super(frameTitle, true, true, true, true);
        try {
            if (s_finder==null) s_finder=new AutoFinder(Administrator.getHost(),
                    Administrator.getPort(), Administrator.getUser(),
                    Administrator.getPass());
            searchAndDisplay(s_finder.resumeFindObjects(sessionToken),
                    displayFields);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: " + e.getClass().getName() + ":" + e.getMessage());
        }
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
            if (s_finder==null) s_finder=new AutoFinder(Administrator.getHost(),
                Administrator.getPort(), Administrator.getUser(),
                Administrator.getPass());
            searchAndDisplay(s_finder.findObjects(resultFields, maxResults,
                    query), displayFields);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR: " + e.getClass().getName() + ":" + e.getMessage());
        }
    }

    private void searchAndDisplay(FieldSearchResult fsr, String[] displayFields)
            throws Exception {
        // put the resulting data into a structure suitable for display
        ObjectFields[] ofs=fsr.getResultList();
        Object[][] data=new Object[ofs.length][displayFields.length];
        // while adding the pids to m_rowPids so they can be used later
        m_rowPids=new String[ofs.length];
        for (int i=0; i<ofs.length; i++) {
            ObjectFields o=ofs[i];
            m_rowPids[i]=o.getPid();
            for (int j=0; j<displayFields.length; j++) {
                data[i][j]=getValue(o, displayFields[j]);
            }
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
        if (fsr.getListSession()!=null && fsr.getListSession().getToken()!=null) {
            m_moreButton=new JButton("More Results...");
            m_moreButton.addActionListener(new MoreResultsListener(displayFields,
                    fsr.getListSession().getToken(), this));
            getContentPane().add(m_moreButton, BorderLayout.SOUTH);
        }
        setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Zoom16.gif")));
        pack();
        setSize(Administrator.getDesktop().getWidth()-40,getSize().height);
    }

    protected void removeMoreResultsButton() {
        if (m_moreButton!=null) {
            getContentPane().remove(m_moreButton);
        }
    }

    public String getValue(ObjectFields o, String name) {
        if (name.equals("pid")) return o.getPid();
        if (name.equals("label")) return o.getLabel();
        if (name.equals("fType")) return o.getFType();
        if (name.equals("cModel")) return o.getCModel();
        if (name.equals("state")) return o.getState();
        if (name.equals("ownerId")) return o.getOwnerId();
        if (name.equals("cDate")) return FORMATTER.format(o.getCDate().getTime());
        if (name.equals("mDate")) return FORMATTER.format(o.getMDate().getTime());
        if (name.equals("dcmDate")) return FORMATTER.format(o.getDcmDate());
        if (name.equals("bDef")) return getList(o.getBDef());
        if (name.equals("bMech")) return getList(o.getBMech());
        if (name.equals("title")) return getList(o.getTitle());
        if (name.equals("creator")) return getList(o.getCreator());
        if (name.equals("subject")) return getList(o.getSubject());
        if (name.equals("description")) return getList(o.getDescription());
        if (name.equals("publisher")) return getList(o.getPublisher());
        if (name.equals("contributor")) return getList(o.getContributor());
        if (name.equals("date")) return getList(o.getDate());
        if (name.equals("type")) return getList(o.getType());
        if (name.equals("format")) return getList(o.getFormat());
        if (name.equals("identifier")) return getList(o.getIdentifier());
        if (name.equals("source")) return getList(o.getSource());
        if (name.equals("language")) return getList(o.getLanguage());
        if (name.equals("relation")) return getList(o.getRelation());
        if (name.equals("coverage")) return getList(o.getCoverage());
        if (name.equals("rights")) return getList(o.getRights());
        return null;
    }

    public String getList(String[] s) {
        if (s==null) return "";
        StringBuffer out=new StringBuffer();
        for (int i=0; i<s.length; i++) {
            if (i>0) out.append(", ");
            out.append(s[i]);
        }
        return out.toString();
    }

    public class MoreResultsListener
            implements ActionListener {

        String[] m_displayFields;
        String m_sessionToken;
        ResultFrame m_parent;

        public MoreResultsListener(String[] displayFields, String sessionToken,
                ResultFrame parent) {
            m_displayFields=displayFields;
            m_sessionToken=sessionToken;
            m_parent=parent;
        }

        public void actionPerformed(ActionEvent e) {
            m_parent.removeMoreResultsButton();
            ResultFrame frame=new ResultFrame("More Search Results",
                    m_displayFields, m_sessionToken);
            frame.setVisible(true);
            Administrator.getDesktop().add(frame);
            try {
                frame.setSelected(true);
            } catch (java.beans.PropertyVetoException pve) {}
            }
    }

    public class BrowserTableUI
            extends BasicTableUI {
        protected MouseInputListener createMouseInputListener() {
            return new BasicTableUI.MouseInputHandler() {

                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount()==2) {
                        int rowNum=m_table.rowAtPoint(new Point(e.getX(), e.getY()));
                        if (rowNum>=0) {
                            // launch object viewer to view object
                            new ViewObject(m_rowPids[rowNum]).launch();
                        }
                    }
                }

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
                                pids.add(m_rowPids[sRows[i]]);
                            }
                            if (!clickedOnSelected) {
                                pids=new HashSet();
                                m_table.clearSelection();
                                m_table.addRowSelectionInterval(rowNum, rowNum);
                                pids.add(m_rowPids[rowNum]);
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
            JMenuItem i0=new JMenuItem(new ViewObject(pid));
            i0.setMnemonic(KeyEvent.VK_O);
            i0.setToolTipText("Launches a viewer for the selected object.");
            JMenuItem i1=new JMenuItem(new ViewObjectXML(pid));
            i1.setMnemonic(KeyEvent.VK_V);
            i1.setToolTipText("Launches an XML viewer for the selected object.");
            JMenuItem i2=new JMenuItem(new ExportObject(pid));
            i2.setMnemonic(KeyEvent.VK_E);
            i2.setToolTipText("Exports the selected object.");
            JMenuItem i3=new JMenuItem(new PurgeObject(pid));
            i3.setMnemonic(KeyEvent.VK_P);
            i3.setToolTipText("Removes the selected object from the repository.");
            add(i0);
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
            JMenuItem i0=new JMenuItem(new ViewObject(pids));
            i0.setMnemonic(KeyEvent.VK_O);
            i0.setToolTipText("Launches a viewer for the selected objects.");
            JMenuItem i1=new JMenuItem(new ViewObjectXML(pids));
            i1.setMnemonic(KeyEvent.VK_V);
            i1.setToolTipText("Launches an XML viewer for the selected objects.");
            JMenuItem i2=new JMenuItem(new ExportObject(pids));
            i2.setMnemonic(KeyEvent.VK_E);
            i2.setToolTipText("Exports the selected objects.");
            JMenuItem i3=new JMenuItem(new PurgeObject(pids));
            i3.setMnemonic(KeyEvent.VK_P);
            i3.setToolTipText("Removes the selected objects from the repository.");
            add(i0);
            add(i1);
            add(i2);
            add(i3);
        }
    }
}

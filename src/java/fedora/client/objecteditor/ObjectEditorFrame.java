package fedora.client.objecteditor;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import org.apache.axis.types.NonNegativeInteger;

import fedora.client.Administrator;

import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;

/**
 * An editing window that includes facilities for editing and viewing everything
 * about a digital object.
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
public class ObjectEditorFrame
        extends JInternalFrame 
        implements PotentiallyDirty {

    private ObjectPane m_objectPane;
    private DatastreamsPane m_datastreamsPane;
    private DisseminatorsPane m_disseminatorsPane;
    private JTabbedPane m_tabbedPane;
    private String m_pid;
    private String m_fType;

    static ImageIcon objIcon=new ImageIcon(Administrator.cl.getResource("images/standard/general/Information16.gif"));
    static ImageIcon dsIcon=new ImageIcon(Administrator.cl.getResource("images/standard/general/Copy16.gif"));
    static ImageIcon dissIcon=new ImageIcon(Administrator.cl.getResource("images/standard/general/Refresh16.gif"));

    /**
     * Constructor.  Queries the server for the object, builds the object
     * and component tabs, and populates them with the appropriate panels.
     */
    public ObjectEditorFrame(String pid, int startTab) 
            throws Exception {
        super(pid, true, true, true, true);
        m_pid=pid;
        // query the server for key object fields
        ObjectFields o=getObjectFields(pid);
        String state=o.getState();
        String label=o.getLabel();
        String cModel=o.getCModel();
        Calendar cDate=o.getCDate();
        Calendar mDate=o.getMDate();
        String ownerId=o.getOwnerId();
        String fType=o.getFType();
        m_fType=fType;

        doTitle(false);

        // set up dirtiness check on close
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new ObjectEditorClosingListener(pid));

        // outerPane(tabbedPane)

            // tabbedPane(ObjectPane, DatastreamsPane, DisseminatorsPane)
            m_objectPane=new ObjectPane(this, pid, state, label, cModel, cDate,
                    mDate, ownerId);
            m_datastreamsPane=new DatastreamsPane(this, pid);
            m_disseminatorsPane=new DisseminatorsPane(pid);
        
            m_tabbedPane=new JTabbedPane();
            m_tabbedPane.addTab("Properties", m_objectPane);
            m_tabbedPane.setBackgroundAt(0, Administrator.DEFAULT_COLOR);
            m_tabbedPane.setIconAt(0, objIcon);
            m_tabbedPane.addTab("Datastreams", m_datastreamsPane);
            m_tabbedPane.setBackgroundAt(1, Administrator.DEFAULT_COLOR);
            m_tabbedPane.setIconAt(1, dsIcon);
            m_tabbedPane.addTab("Disseminators", m_disseminatorsPane);
            m_tabbedPane.setBackgroundAt(2, Administrator.DEFAULT_COLOR);
            m_tabbedPane.setIconAt(2, dissIcon);
            m_tabbedPane.setSelectedIndex(startTab);

        JPanel outerPane=new JPanel();        
        outerPane.setLayout(new BorderLayout());
        outerPane.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        outerPane.add(m_tabbedPane, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(outerPane, BorderLayout.CENTER);
        setFrameIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/standard/general/Open16.gif")));
        pack();
        setSize(new Dimension(740, 545)); 
/*        Dimension size=getSize();
        Dimension aSize=Administrator.INSTANCE.getSize();
        if (size.width>aSize.width-20) {
            size.width=aSize.width-20;
        }
        if (size.height>aSize.height-20) {
            size.height=aSize.height-20;
        }
        setSize(size);
*/
        show();
    }

    private void doTitle(boolean dirty) {
        String d="";
        if (dirty) d="*";
        if (m_fType.equals("D")) {
            setTitle("Behavior Definition - " + m_pid + d);
        } else if (m_fType.equals("M")) {
            setTitle("Behavior Mechanism - " + m_pid + d);
        } else {
            setTitle("Object - " + m_pid + d);
        }
    }

    public boolean isDirty() {
        return ( m_objectPane.isDirty() || m_datastreamsPane.isDirty()
                || m_disseminatorsPane.isDirty() );
    }

    public void indicateDirtiness() {
        int dirtyCount=0;
        if (m_objectPane.isDirty()) {
            dirtyCount++;
            m_tabbedPane.setTitleAt(0, "Properties*");
        } else {
            m_tabbedPane.setTitleAt(0, "Properties");
        }
        if (m_datastreamsPane.isDirty()) {
            dirtyCount++;
            m_tabbedPane.setTitleAt(1, "Datastreams*");
        } else {
            m_tabbedPane.setTitleAt(1, "Datastreams");
        }
        if (m_disseminatorsPane.isDirty()) {
            dirtyCount++;
            m_tabbedPane.setTitleAt(2, "Disseminators*");
        } else {
            m_tabbedPane.setTitleAt(2, "Disseminators");
        }
        if (dirtyCount>0) {
            doTitle(true);
        } else {
            doTitle(false);
        }
    }

    private ObjectFields getObjectFields(String pid)
            throws Exception {
        FieldSearchQuery query=new FieldSearchQuery();
        Condition[] conditions=new Condition[1];
        conditions[0]=new Condition();
        conditions[0].setProperty("pid");
        conditions[0].setOperator(ComparisonOperator.fromValue("eq"));
        conditions[0].setValue(pid);
        query.setConditions(conditions);
        String[] fields=new String[] {"state", "label", "cModel", "cDate", 
                "mDate", "ownerId", "fType"};
        FieldSearchResult result=Administrator.APIA.findObjects(
                    fields, new NonNegativeInteger("1"), query);
        ObjectFields[] resultList=result.getResultList();
        if (resultList==null || resultList.length==0) {
            throw new IOException("Object not found in repository");
        }
        return resultList[0];
    }

    /**
     * Listens for closing events and checks for object and component
     * dirtiness.
     */
    protected class ObjectEditorClosingListener 
            extends InternalFrameAdapter {

        private String m_pid;

        public ObjectEditorClosingListener(String pid) {
            m_pid=pid;
        }

        /**
         * Check if any of the items being edited are dirty.
         * If so, give the user a chance to keep the editor open so they
         * can save their changes.
         */
        public void internalFrameClosing(InternalFrameEvent e) {
            if (isDirty()) {
                Object[] options = { "Yes", "No" };
                int selected=JOptionPane.showOptionDialog(null, 
                        "Close " + m_pid + " without saving changes?", 
                        "Unsaved changes", JOptionPane.DEFAULT_OPTION, 
                        JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                if (selected==0) {
                    e.getInternalFrame().dispose();
                }
            } else {
                e.getInternalFrame().dispose();
            }
        }

    }

}
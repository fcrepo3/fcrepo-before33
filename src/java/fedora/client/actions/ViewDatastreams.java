package fedora.client.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import fedora.client.Administrator;
import fedora.client.datastream.DatastreamViewer;

/**
 *
 * <p><b>Title:</b> ViewDatastreams.java</p>
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
 * @version 1.0
 */
public class ViewDatastreams
        extends AbstractAction {

    private Set m_pids;
    private boolean m_prompt;

    public ViewDatastreams() {
        super("View/Edit Datastreams...");
        m_prompt=true;
    }

    public ViewDatastreams(String pid) {
        super("View/Edit Datastreams");
        m_pids=new HashSet();
        m_pids.add(pid);
    }

    public ViewDatastreams(Set pids) {
        super("View/Edit Datastreams");
        m_pids=pids;
    }

    public void actionPerformed(ActionEvent ae) {
        boolean failed=false;
        if (m_prompt) {
            String pid=JOptionPane.showInputDialog("Enter the PID.");
            if (pid==null) {
                return;
            }
            m_pids=new HashSet();
            m_pids.add(pid);
        }
        Iterator pidIter=m_pids.iterator();
        while (pidIter.hasNext()) {
            String pid=(String) pidIter.next();
            try {
                DatastreamViewer viewer=new DatastreamViewer(pid);
                viewer.setVisible(true);
                Administrator.getDesktop().add(viewer);
                viewer.setSelected(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(Administrator.getDesktop(),
                        e.getClass().getName() + ": " + e.getMessage(),
                        "Datastream View Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
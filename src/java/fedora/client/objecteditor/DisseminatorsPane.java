package fedora.client.objecteditor;

import java.awt.*;
import javax.swing.*;

import fedora.client.Administrator;
import fedora.server.types.gen.Disseminator;

/**
 * Shows a tabbed pane, one for each disseminator in the object.
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
public class DisseminatorsPane
        extends JPanel
        implements PotentiallyDirty, TabDrawer {

    private String m_pid;
    private JTabbedPane m_tabbedPane;
    private ObjectEditorFrame m_owner;

    /**
     * Build the pane.
     */
    public DisseminatorsPane(ObjectEditorFrame owner, String pid)
            throws Exception {
        m_pid=pid;
        m_owner=owner;

        m_tabbedPane=new JTabbedPane(SwingConstants.LEFT);
        Disseminator[] currentVersions=Administrator.APIM.
                    getDisseminators(pid, null, null);
        for (int i=0; i<currentVersions.length; i++) {
            JLabel a=new JLabel(currentVersions[i].getLabel());
            m_tabbedPane.add(currentVersions[i].getID(), a);
        }
        m_tabbedPane.add("New...", new JLabel("not implemented"));

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        add(m_tabbedPane, BorderLayout.CENTER);
    }

    public boolean isDirty() {
        return false;
    }

    public void setDirty(String id, boolean isDirty) {
    }

    public void colorTabForState(String id, String state) {
    }

}
package fedora.client.objecteditor;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

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
        implements PotentiallyDirty {

    private String m_pid;

    /**
     * Build the pane.
     */
    public DisseminatorsPane(String pid)
            throws Exception {
        m_pid=pid;

        // this(tabbedPane)

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
        add(new javax.swing.JLabel(pid));
 //       add(tabbedPane, BorderLayout.CENTER);
 //       add(savePane, BorderLayout.SOUTH);

    }

    public boolean isDirty() {
        return true; // just kidding
    }

}
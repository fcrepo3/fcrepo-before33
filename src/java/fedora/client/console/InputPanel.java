/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.console;

import javax.swing.JPanel;

/**
 *
 * <p><b>Title:</b> InputPanel.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class InputPanel
        extends JPanel {

    public abstract Object getValue();

}
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
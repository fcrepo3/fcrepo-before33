package fedora.client.console;

import javax.swing.JLabel;

/**
 *
 * <p><b>Title:</b> NullInputPanel.java</p>
 * <p><b>Description:</b> For use when unrecognized type.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class NullInputPanel
        extends InputPanel {

    private static NullInputPanel s_instance=new NullInputPanel();

    protected NullInputPanel() {
        add(new JLabel("Unrecognized type, using null"));
    }

    public static NullInputPanel getInstance() {
        return s_instance;
    }

    public Object getValue() {
        return null;
    }

}
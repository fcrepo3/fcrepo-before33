package fedora.client.console;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.ArrayList;

/**
 *
 * <p><b>Title:</b> ArrayInputPanel.java</p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 */
public class ArrayInputPanel
        extends InputPanel {

    private ArrayList m_inputPanels;

    public ArrayInputPanel(Class cl) {
        m_inputPanels=new ArrayList();
        add(new JLabel("Array handler not implemented, will be null."));
    }

    public Object getValue() {
        Object[] out = null;
        if (m_inputPanels.size() > 0)
        {
          out=new Object[m_inputPanels.size()];
        }
        return out;
    }

}
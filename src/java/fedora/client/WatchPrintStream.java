package fedora.client;

import java.io.*;

/**
 * A PrintStream that sends its output to Administrator.WATCH_AREA,
 * the JTextArea of the Tools->Advanced->STDOUT/STDERR window.
 * This is used for redirecting System.out/err output to the UI.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 */
public class WatchPrintStream
        extends PrintStream {

    /** Output is buffered here until a call to println(String) */
    private ByteArrayOutputStream m_out;

    public WatchPrintStream(ByteArrayOutputStream out) {
        super(out);
        m_out=out;
    }

    /** 
     * Every time this is called, the buffer is cleared an output is
     * sent to the JTextArea.
     */
    public void println(String str) {
        super.println(str);
        if (Administrator.WATCH_AREA!=null) {
            String buf=m_out.toString();
            m_out.reset();
            Administrator.WATCH_AREA.append(buf);
        }
    }

}
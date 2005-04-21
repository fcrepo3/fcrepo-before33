package fedora.client;

import java.io.*;

/**
 * A PrintStream that sends its output to Administrator.WATCH_AREA,
 * the JTextArea of the Tools->Advanced->STDOUT/STDERR window.
 * This is used for redirecting System.out/err output to the UI.
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
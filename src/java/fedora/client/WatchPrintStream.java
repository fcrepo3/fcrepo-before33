package fedora.client;

import java.io.*;

// a wrapper around PrintWriter that sends all its content to WATCH_AREA
    // via WATCH_AREA.append(String)
    public class WatchPrintStream
            extends PrintStream {

        private ByteArrayOutputStream m_out;

        public WatchPrintStream(ByteArrayOutputStream out) {
            super(out);
            m_out=out;
        }

        public void println(String str) {
            super.println(str);
            if (Administrator.WATCH_AREA!=null) {
                String buf=m_out.toString();
                m_out.reset();
                Administrator.WATCH_AREA.append(buf);
            }
        }

    }
package fedora.server.utilities;

import fedora.server.Logging;
import fedora.server.StdoutLogging;

/**
 *
 * <p><b>Title:</b> ThreadMonitorImpl.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ThreadMonitorImpl
        extends StdoutLogging
        implements ThreadMonitor {

    private boolean m_stopRequested;
    private int m_pollInterval;

    public ThreadMonitorImpl(int pollInterval, Logging logTarget) {
        super(logTarget);
        if (pollInterval>=0) {
            m_pollInterval=pollInterval;
            Thread t=new Thread(this, "ThreadMonitor");
            t.start();
        }
    }

    public void run() {
        while (!m_stopRequested) {
            try {
                Thread.sleep(m_pollInterval);
            } catch (InterruptedException ie) { }
            logFiner(getThreadTree());
        }
    }

    public void requestStop() {
        m_stopRequested=true;
    }

    public static String getThreadTree() {
        ThreadGroup current, root, parent;
        current=Thread.currentThread().getThreadGroup();
        root=current;
        parent=root.getParent();
        while (parent!=null) {
            root=parent;
            parent=parent.getParent();
        }
        StringBuffer out=new StringBuffer();
        appendGroup(root, "", out);
        return out.toString();
    }

    private static void appendGroup(ThreadGroup g, String indent, StringBuffer out) {
        if (g!=null) {
            int tc=g.activeCount();
            int gc=g.activeGroupCount();
            Thread[] threads=new Thread[tc];
            ThreadGroup[] groups=new ThreadGroup[gc];
            g.enumerate(threads, false);
            g.enumerate(groups, false);
            out.append(indent + "Group: " + g.getName() + " MaxPriority: "
                    + g.getMaxPriority() + (g.isDaemon()?" DAEMON":"") + "\n");
            for (int i=0; i<tc; i++)
                appendThread(threads[i], indent + "    ", out);
            for (int i=0; i<gc; i++)
                appendGroup(groups[i], indent + "    ", out);
        }
    }

    private static void appendThread(Thread t, String indent, StringBuffer out) {
        if (t == null) return;
        out.append(indent + "Thread: " + t.getName() +
                "  Priority: " + t.getPriority() +
                (t.isDaemon()?" DAEMON":"") +
                (t.isAlive()?"":" NOT ALIVE") + "\n");
    }

    public static void main(String[] args) {
        ThreadMonitorImpl tm=new ThreadMonitorImpl(2000, null);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) {
        }
        tm.requestStop();
    }

}
package fedora.server.utilities;

import java.util.Map;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;

/**
 *
 * <p><b>Title:</b> ThreadMonitorModule.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ThreadMonitorModule
        extends Module
        implements ThreadMonitor {

    private ThreadMonitorImpl m_wrappedMonitor;
    private boolean m_active=false;

    public ThreadMonitorModule(Map params, Server server, String role)
            throws ModuleInitializationException {
        super(params, server, role);
    }

    public void initModule()
            throws ModuleInitializationException {
        String active=getParameter("active");
        String pollInterval=getParameter("pollInterval");
        String onlyMemory=getParameter("onlyMemory");
        if (active!=null && (active.toLowerCase().equals("yes") || active.toLowerCase().equals("true"))) {
            m_active=true;
            if (pollInterval==null) {
                logConfig("pollInterval unspecified, defaulting to 10,000 milliseconds.");
                pollInterval="10000";
            }
            try {
                int pi=Integer.parseInt(pollInterval);
                if (pi<0) {
                    throw new NumberFormatException();
                }
                boolean onlyMem=false;
                if (onlyMemory.equalsIgnoreCase("yes") || onlyMemory.equalsIgnoreCase("true")) {
                    onlyMem=true;
                }
                m_wrappedMonitor=new ThreadMonitorImpl(pi, onlyMem, this);
            } catch (NumberFormatException nfe) {
                throw new ModuleInitializationException("Badly formed parameter: pollInterval: must be a nonnegative integer.", getRole());
            }
        }
    }

    public void shutdownModule() {
        if (m_active) {
            m_wrappedMonitor.requestStop();
        }
    }

    public void run() {
        if (m_active) {
            m_wrappedMonitor.run();
        }
    }

    public void requestStop() {
        if (m_active) {
            m_wrappedMonitor.requestStop();
        }
    }

}
package fedora.server.management;

import java.io.*;
import java.util.*;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.storage.ConnectionPoolManager;

/**
 * A wrapper around the DBPIDGenerator class that casts it as a Module.
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
public class BasicPIDGenerator
        extends Module implements PIDGenerator {

    private ConnectionPoolManager m_mgr;
    private DBPIDGenerator m_pidGenerator;
    private File m_oldPidGenDir;

    /**
     * Constructs a BasicPIDGenerator.
     *
     * @param moduleParameters A pre-loaded Map of name-value pairs comprising
     *        the intended configuration of this Module.
     * @param server The <code>Server</code> instance.
     * @param role The role this module fulfills, a java class name.
     * @throws ModuleInitializationException If initilization values are
     *         invalid or initialization fails for some other reason.
     */
    public BasicPIDGenerator(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
    }

    public void initModule() {
        // this parameter is no longer required; but if it's specified,
        // we can automatically upgrade from a pre-1.2 version of Fedora by 
        // making sure the old "last pid generated" value is respected later.
        String dir=getParameter("pidgen_log_dir");
        if (dir!=null && !dir.equals("")) {
            if (dir.startsWith("/")
                    || dir.startsWith("\\")
                    || dir.substring(1).startsWith(":\\")) {
                m_oldPidGenDir=new File(dir);
            } else {
                m_oldPidGenDir=new File(getServer().getHomeDir(), dir);
            }
        }
    }

    /**
     * Get a reference to the ConnectionPoolManager so we can give the
     * instance constructor a ConnectionPool later in initializeIfNeeded().
     */
    public void postInitModule() 
            throws ModuleInitializationException {
        m_mgr=(ConnectionPoolManager) getServer()
                .getModule("fedora.server.storage.ConnectionPoolManager");
		if (m_mgr==null) {
            throw new ModuleInitializationException(
                    "ConnectionPoolManager module not loaded.", getRole());
		}
    }

    private void initializeIfNeeded() 
            throws IOException {
        try {
            if (m_pidGenerator==null) {
                m_pidGenerator=new DBPIDGenerator(m_mgr.getPool(), m_oldPidGenDir);
            }
        } catch (ConnectionPoolNotFoundException e) {
            throw new IOException("Can't get default connection pool!");
        }
    }

    public String generatePID(String namespaceID)
            throws IOException {
        initializeIfNeeded();
        return m_pidGenerator.generatePID(namespaceID);
    }

    public String getLastPID()
            throws IOException {
        initializeIfNeeded();
        return m_pidGenerator.getLastPID();
    }

    public void neverGeneratePID(String pid) 
            throws IOException {
        initializeIfNeeded();
        m_pidGenerator.neverGeneratePID(pid);
    }

}

package fedora.server.search;

import java.util.Map;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ServerException;
import fedora.server.storage.ConnectionPoolManager;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOReader;

/**
 *
 * <p><b>Title:</b> FieldSearchSQLModule.java</p>
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
public class FieldSearchSQLModule
        extends Module
        implements FieldSearch {

    private FieldSearchSQLImpl m_wrappedFieldSearch;

    public FieldSearchSQLModule(Map params, Server server, String role)
            throws ModuleInitializationException {
        super(params, server, role);
    }

    public void postInitModule()
            throws ModuleInitializationException {
        //
        // get and validate maxResults
        //
        if (getParameter("maxResults")==null) {
            throw new ModuleInitializationException(
                "maxResults parameter must be specified.", getRole());
        }
        int maxResults=0;
        try {
            maxResults=Integer.parseInt(getParameter("maxResults"));
            if (maxResults<1) {
                throw new NumberFormatException("");
            }
        } catch (NumberFormatException nfe) {
            throw new ModuleInitializationException(
                "maxResults must be a positive integer.", getRole());
        }
        //
        // get and validate maxSecondsPerSession
        //
        if (getParameter("maxSecondsPerSession")==null) {
            throw new ModuleInitializationException(
                "maxSecondsPerSession parameter must be specified.", getRole());
        }
        int maxSecondsPerSession=0;
        try {
            maxSecondsPerSession=Integer.parseInt(getParameter("maxSecondsPerSession"));
            if (maxSecondsPerSession<1) {
                throw new NumberFormatException("");
            }
        } catch (NumberFormatException nfe) {
            throw new ModuleInitializationException(
                "maxSecondsPerSession must be a positive integer.", getRole());
        }
        //
        // get connectionPool from ConnectionPoolManager
        //
        ConnectionPoolManager cpm=(ConnectionPoolManager) getServer().
                getModule("fedora.server.storage.ConnectionPoolManager");
        if (cpm==null) {
            throw new ModuleInitializationException(
                "ConnectionPoolManager module was required, but apparently has "
                + "not been loaded.", getRole());
        }
        String cPoolName=getParameter("connectionPool");
        ConnectionPool cPool=null;
        try {
            if (cPoolName==null) {
                logConfig("connectionPool unspecified; using default from "
                        + "ConnectionPoolManager.");
                cPool=cpm.getPool();
            } else {
                logConfig("connectionPool specified: " + cPoolName);
                cPool=cpm.getPool(cPoolName);
            }
        } catch (ConnectionPoolNotFoundException cpnfe) {
            throw new ModuleInitializationException("Could not find requested "
                    + "connectionPool.", getRole());
        }
        //
        // get the doManager
        //
        DOManager doManager=(DOManager) getServer().
                getModule("fedora.server.storage.DOManager");
        if (doManager==null) {
            throw new ModuleInitializationException(
                "DOManager module was required, but apparently has "
                + "not been loaded.", getRole());
        }
        //
        // things look ok...get the wrapped instance
        //
        m_wrappedFieldSearch=new FieldSearchSQLImpl(cPool, doManager,
                maxResults, maxSecondsPerSession, this);
    }

    public String[] getRequiredModuleRoles() {
        return new String[] {"fedora.server.storage.ConnectionPoolManager",
                "fedora.server.storage.DOManager"};
    }

    public void update(DOReader reader)
            throws ServerException {
        m_wrappedFieldSearch.update(reader);
    }

    public boolean delete(String pid)
            throws ServerException {
        return m_wrappedFieldSearch.delete(pid);
    }

    public FieldSearchResult findObjects(String[] resultFields,
            int maxResults, FieldSearchQuery query)
            throws ServerException {
        return m_wrappedFieldSearch.findObjects(resultFields,
                maxResults, query);
    }

    public FieldSearchResult resumeFindObjects(String sessionToken)
            throws ServerException {
        return m_wrappedFieldSearch.resumeFindObjects(sessionToken);
    }

}
package fedora.server.management;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;

/**
 *
 * <p><b>Title:</b> BasicPIDGenerator.java</p>
 * <p><b>Description:</b> A <code>PIDGenerator</code> that produces ordered
 * PIDs, and keeps track of where it's at using logfiles.</p>
 *
 * <p>Implementation note: This is a wrapper around the PIDGeneration class
 * that casts it as a Module.</p>
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

    private PIDGeneration m_pidGeneration;

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

    public void initModule()
            throws ModuleInitializationException {
        try {
            String pidGenLogDir=getParameter("pidgen_log_dir");
            if (pidGenLogDir==null) {
                throw new ModuleInitializationException("pidgen_log_dir must be specified", getRole());
            }
            File f=null;
            if (pidGenLogDir.startsWith("/")
                    || pidGenLogDir.startsWith("\\")
                    || pidGenLogDir.substring(1).startsWith(":\\")) {
                f=new File(pidGenLogDir);
            } else {
                f=new File(getServer().getHomeDir(), pidGenLogDir);
            }
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new ModuleInitializationException("pidgen_log_dir could not be created.", getRole());
                }
            } else {
                if (!f.isDirectory()) {
                    throw new ModuleInitializationException("pidgen_log_dir exists but is not a dir.", getRole());
                }
            }
            m_pidGeneration=new PIDGeneration(f.getAbsolutePath());
        } catch (ClassNotFoundException cnfe) {
            // won't happen
        } catch (IOException ioe) {
            // maybe...
            throw new ModuleInitializationException("IOException thrown by PIDGeneration constructor:" + ioe.getMessage(), getRole());
        }
    }

    /**
     * Creates a new PID with the specified namespace as a prefix.
     *
     * @param namespaceID The namespace id for the to-be-generated pid.
     * @return The generated pid.
     * @throws IOException If the request can't be fulfilled due to an
     *         unexpected IO condition.
     */
    public String generatePID(String namespaceID)
            throws IOException {
        return m_pidGeneration.generatePID(namespaceID);
    }

    /**
     * Gets the last generated PID.
     *
     * @return the retrieved PID string
     * @throws IOException If the request can't be fulfilled due to an
     *         unexpected IO condition.
     */
    public String getLastPID()
            throws IOException {
        return m_pidGeneration.getLastPID();
    }

}

package fedora.server;

import java.util.Map;

/**
 *
 * <p><b>Title:</b> DatastoreConfig.java</p>
 * <p><b>Description:</b> A holder of configuration name-value pairs for a
 * datastore.</p>
 *
 * <p>A datastore is a system for retrieving and storing information.  This
 * class is a convenient placeholder for the configuration values of such
 * a system.</p>
 *
 * <p>Configuration values for datastores are set in the server configuration
 * file. (see fedora-config.xsd)</p>
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
 * @version 1.0
 */
public class DatastoreConfig
        extends Parameterized {

    /**
     * Creates and initializes the <code>DatastoreConfig</code>.
     * <p></p>
     * When the server is starting up, this is invoked as part of the
     * initialization process.
     *
     * @param componentParameters A pre-loaded Map of name-value pairs
     *        comprising the intended configuration for the datastore.
     */
    public DatastoreConfig(Map parameters) {
        super(parameters);
    }

}
package fedora.server.format;

import java.io.*;
import java.util.*;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ServerException;

/**
 * Provides information about known formats to other parts of the Server.
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
public class FormatRegistryModule
        extends Module
        implements FormatRegistry {

    private CachedFormatRegistry m_wrappedRegistry;

    public FormatRegistryModule(Map params, Server server, String role)
            throws ModuleInitializationException {
        super(params, server, role);
    }

    public void initModule()
            throws ModuleInitializationException {
        File fmtRegFile=new File(new File(getServer().getHomeDir(), "config"), "fmtreg.xml");
        try {
            m_wrappedRegistry=new CachedFormatRegistry(new FileInputStream(fmtRegFile));
            Iterator iter=identifiers();
            while (iter.hasNext()) {
                String id=(String) iter.next();
                Format format=getFormat(id);
                logFinest("Format info for key: " + id + "\n"
                        + "Identifier        = " + format.getIdentifier() + "\n"
                        + "Label             = " + format.getLabel() + "\n"
                        + "XMLSchemaLocation = " + format.getXMLSchemaLocation() + "\n"
                        + "XMLNamespace      = " + format.getXMLNamespace() + "\n"
                        + "OAIPrefix         = " + format.getOAIPrefix()
                );
            }
        } catch (Exception e) {
            throw new ModuleInitializationException(
                    "Problem initializing from " + fmtRegFile.getPath()
                    + ": " + e.getMessage(),
                    getRole());
        }
    }

    public Format getFormat(String identifier) 
            throws ServerException {
        return m_wrappedRegistry.getFormat(identifier);
    }

    public Iterator identifiers() 
            throws ServerException {
        return m_wrappedRegistry.identifiers();
    }

}

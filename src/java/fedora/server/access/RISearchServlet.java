package fedora.server.access;

import java.io.*;
import javax.servlet.*;

import org.trippi.*;
import org.trippi.server.http.*;

import fedora.server.*;

/**
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 */
public class RISearchServlet extends TrippiServlet {

    public TriplestoreReader getReader() throws ServletException {
        TriplestoreReader reader = null;
        try {
            Server server = Server.getInstance(new File(System.getProperty("fedora.home")));
            reader = (TriplestoreReader) server.getModule("fedora.server.resourceIndex.ResourceIndex");
        } catch (Exception e) {
            throw new ServletException("Error initting RISearchServlet.", e);
        } 
        if (reader == null) {
            throw new ServletException("The Resource Index is not loaded.");
        } else {
            return reader;
        }
    }

    public boolean closeOnDestroy() { return false; }
    public String getIndexStylesheetLocation() { return "/ROOT/ri/index.xsl"; }
    public String getFormStylesheetLocation() { return "/ROOT/ri/form.xsl"; }
    public String getErrorStylesheetLocation() { return "/ROOT/ri/error.xsl"; }
    public String getContext(String origContext) { return "/ri"; }
}

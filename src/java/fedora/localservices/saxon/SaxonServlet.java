package fedora.localservices.saxon;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Hashtable;
import java.util.Enumeration;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import com.icl.saxon.expr.StringValue;
import java.util.Properties;

/**
 *
 * <p>Title: SaxonServlet.java</p>
 * <p>Description: Transforms a supplied input document using a supplied
 * stylesheet.</p>
 *
 * <p>Adapted from the SaxonServlet.java example file contained in the
 * source distribution of "The SAXON XSLT Processor from Michael Kay". </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The original code is Copyright &copy; 2001 by Michael Kay. All rights
 * reserved. The current project homepage for Saxon may be found at:
 * <a href="http://saxon.sourceforge.net/">http://saxon.sourceforge.net/</a>.</p>
 *
 * <p>Portions created for the Fedora Repository System are Copyright &copy; 2002, 2003
 * by The Rector and Visitors of the University of Virginia and Cornell
 * University. All rights reserved."</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author Michael Kay
 * @version Saxon 6.5.2, $Id$
 */
public class SaxonServlet extends HttpServlet {

    /**
    * service() - accept request and produce response<BR>
    * URL parameters: <UL>
    * <li>source - URL of source document</li>
    * <li>style - URL of stylesheet</li>
    * <li>clear-stylesheet-cache - if set to yes, empties the cache before running.
    * </UL>
    * @param req The HTTP request
    * @param res The HTTP response
    */
    public void service(HttpServletRequest req, HttpServletResponse res)
        throws ServletException, IOException
    {
        String source = req.getParameter("source");
        String style = req.getParameter("style");
        String clear = req.getParameter("clear-stylesheet-cache");

        if (clear!=null && clear.equals("yes")) {
            clearCache();
        }

        try {
            apply(style, source, req, res);
        } catch (TransformerException err) {
            res.getOutputStream().println("Error applying stylesheet: " + err.getMessage());
        }

    }

    /**
    * getServletInfo
    * Required by Servlet interface
    */
    public String getServletInfo() {
        return "Calls SAXON to apply a stylesheet to a source document";
    }

    /**
    * Apply stylesheet to source document
    */
    private void apply(String style, String source,
                           HttpServletRequest req, HttpServletResponse res)
                           throws TransformerException, java.io.IOException {

        ServletOutputStream out = res.getOutputStream();

        if (style==null) {
            out.println("No style parameter supplied");
            return;
        }
        if (source==null) {
            out.println("No source parameter supplied");
            return;
        }
        try {
            Templates pss = tryCache(style);
            Transformer transformer = pss.newTransformer();
            Properties details = pss.getOutputProperties();

            String mime = pss.getOutputProperties().getProperty(OutputKeys.MEDIA_TYPE);
            if (mime==null) {
               // guess
                res.setContentType("text/html");
            } else {
                res.setContentType(mime);
            }

            Enumeration p = req.getParameterNames();
            while (p.hasMoreElements()) {
                String name = (String)p.nextElement();
                if (!(name.equals("style") || name.equals("source"))) {
                    String value = req.getParameter(name);
                    transformer.setParameter(name, new StringValue(value));
                }
            }
            //
            // Begin Modification -- For use with Fedora, the manner in which a
            // StreamSource is instantiated has been altered.
            //
            //String path = getServletContext().getRealPath(source);
            //if (path==null) {
            //    throw new TransformerException("Source file " + source + " not found");
            //}
            if (source==null) {
                throw new TransformerException("Source file " + source + " not found");
            }
            //
            //File sourceFile = new File(path);
            //transformer.transform(new StreamSource(sourceFile), new StreamResult(out));
            transformer.transform(new StreamSource(source), new StreamResult(out));
            // End Modification --

        } catch (Exception err) {
            out.println(err.getMessage());
            err.printStackTrace();
        }

    }

    /**
    * Maintain prepared stylesheets in memory for reuse
    */
    private synchronized Templates tryCache(String url) throws TransformerException, java.io.IOException {
        //
        // Begin Modification -- For use with Fedora, the url is used as the
        // key in the cache hashtable.
        //String path = getServletContext().getRealPath(url);
        //if (path==null) {
        //    throw new TransformerException("Stylesheet " + url + " not found");
        //}
        if (url==null) {
                throw new TransformerException("Stylesheet " + url + " not found");
        }

        //Templates x = (Templates)cache.get(path);
        Templates x = (Templates)cache.get(url);
        if (x==null) {
            TransformerFactory factory = TransformerFactory.newInstance();
            //
            //x = factory.newTemplates(new StreamSource(new File(path)));
            x = factory.newTemplates(new StreamSource(url));
            //
            //cache.put(path, x);
            cache.put(url, x);
            // End Modification --
        }
        return x;
    }

    /**
    * Clear the cache. Useful if stylesheets have been modified, or simply if space is
    * running low. We let the garbage collector do the work.
    */
    private synchronized void clearCache() {
        cache = new Hashtable();
    }

    private Hashtable cache = new Hashtable();
}

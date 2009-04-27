/*
 * The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */
package fedora.server.rest;

import java.io.File;
import java.io.StringReader;
import java.io.Writer;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.sf.saxon.FeatureKeys;

import org.apache.log4j.Logger;

import fedora.common.Constants;

import fedora.server.Server;
import fedora.server.access.Access;
import fedora.server.errors.DatastreamNotFoundException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;
import fedora.server.errors.authorization.AuthzException;
import fedora.server.management.Management;

/**
 * A barebone RESTFUL resource implementation.
 *
 * @author cuong.tran@yourmediashelf.com
 * @version $Id$
 */
public class BaseRestResource {
    private final Logger LOG = Logger.getLogger(getClass());

    static final String[] EMPTY_STRING_ARRAY = new String[0];
    static final String DEFAULT_ENC = "UTF-8";

    public static final String XML = "text/xml";
    public static final String HTML = "text/html";
    public static final String FORM = "multipart/form-data";

    public static final MediaType TEXT_XML = new MediaType("text", "xml");
    public static final MediaType TEXT_HTML = new MediaType("text", "html");

    protected Server fedoraServer;
    protected Management apiMService;
    protected Access apiAService;
    protected String fedoraServerHost;

    public BaseRestResource() {
        try {
            this.fedoraServer = Server.getInstance(new File(Constants.FEDORA_HOME), false);
            this.apiMService = (Management) fedoraServer.getModule("fedora.server.management.Management");
            this.apiAService = (Access) fedoraServer.getModule("fedora.server.access.Access");
            this.fedoraServerHost = fedoraServer.getParameter("fedoraServerHost");
        } catch (Exception ex) {
            throw new RestException("Unable to locate Fedora server instance", ex);
        }
    }

    protected void transform(
            String xml,
            String xslt,
            Writer out) throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
        File xslFile = new File(fedoraServer.getHomeDir(), xslt);
        TransformerFactory factory = TransformerFactory.newInstance();
        if (factory.getClass().getName().equals("net.sf.saxon.TransformerFactoryImpl")) {
            factory.setAttribute(FeatureKeys.VERSION_WARNING, Boolean.FALSE);
        }
        Templates template = factory.newTemplates(new StreamSource(xslFile));
        Transformer transformer = template.newTransformer();
        transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(out));
    }

    protected Response handleException(Exception ex) {
        LOG.error(ex);

        if (ex instanceof ObjectNotInLowlevelStorageException ||
            ex instanceof DatastreamNotFoundException) {
            return Response.status(Status.NOT_FOUND).entity(ex.getMessage()).type("text/plain").build();
        } else if (ex instanceof AuthzException) {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).type("text/plain").build();
        } else {
            throw new WebApplicationException(ex);
        }
    }
}

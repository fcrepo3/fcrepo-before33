/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://fedora-commons.org/license/).
 */
package fedora.server.validation;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import fedora.common.FaultException;
import fedora.common.PID;

import fedora.server.errors.ServerException;
import fedora.server.errors.ValidationException;
import fedora.server.security.PolicyParser;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamManagedContent;

/**
 * Misc validation-related functions.
 *
 * @author Chris Wilper
 * @author Edwin Shin
 * @version $Id$
 */
public abstract class ValidationUtility {

    private static final Logger LOG = Logger.getLogger(ValidationUtility.class);

    private static PolicyParser policyParser;

    /**
     * Validates the candidate URL. File URLs (e.g. file:///bar/baz) are
     * rejected as malformed.
     *
     * @param url The URL to validate.
     * @throws ValidationException if the URL is malformed.
     */
    public static void validateURL(String url)
            throws ValidationException {
        try {
            URL candidate = new URL(url);
            if (candidate.getProtocol().equals("file")) {
                throw new ValidationException("Malformed URL: invalid protocol: " + url);
            }
        } catch (MalformedURLException e) {
            if (url.startsWith(DatastreamManagedContent.UPLOADED_SCHEME)) {
                return;
            }
            throw new ValidationException("Malformed URL: " + url, e);
        }
    }

    /**
     * Sets the policy parser to be used to validate "POLICY" datastream.
     *
     * NOTE: This must be set before attempting to validate POLICY datastreams.
     * Otherwise, a runtime exception will be thrown.
     *
     * @param parser the parser to use.
     */
    public static void setPolicyParser(PolicyParser parser) {
        policyParser = parser;
    }

    /**
     * Validates the latest version of all reserved datastreams in the given
     * object.
     */
    public static void validateReservedDatastreams(DOReader reader)
            throws ValidationException {
        try {
            for (Datastream ds: reader.GetDatastreams(null, null)) {
                if ("X".equals(ds.DSControlGrp)) {
                    validateReservedDatastream(PID.getInstance(reader.GetObjectPID()),
                                               ds.DatastreamID,
                                               ds.getContentStream());
                }
            }
        } catch (ValidationException e) {
            throw e;
        } catch (ServerException e) {
            throw new FaultException(e);
        }
    }

    /**
     * Validates the given datastream if it's a reserved datastream.
     *
     * The given stream is guaranteed to be closed when this method completes.
     */
    public static void validateReservedDatastream(PID pid,
                                                  String dsId,
                                                  InputStream content)
            throws ValidationException {
        if ("POLICY".equals(dsId)) {
            validatePOLICY(content);
        } else if ("RELS-EXT".equals(dsId)) {
            validateRELSEXT(pid, content);
        } else {
            try {
                content.close();
            } catch (IOException e) {
                LOG.warn("Error closing stream", e);
            }
        }
    }

    private static void validatePOLICY(InputStream content)
            throws ValidationException {
        LOG.debug("Validating POLICY datastream");
        policyParser.copy().parse(content, true);
        LOG.debug("POLICY datastream is valid");
    }

    private static void validateRELSEXT(PID pid, InputStream content)
            throws ValidationException {
        LOG.debug("Validating RELS-EXT datastream");
        new RelsExtValidator().validate(pid, content);
        LOG.debug("RELS-EXT datastream is valid");
    }

}
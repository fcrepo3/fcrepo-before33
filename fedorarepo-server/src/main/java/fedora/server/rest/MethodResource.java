/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://fedora-commons.org/license/).
 */
package fedora.server.rest;

import java.io.CharArrayWriter;

import java.util.Date;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fedora.server.Context;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.utilities.DateUtility;

/**
 * A rest controller to handle listing and invoking all Service Definition
 * methods in a digital object.
 *
 * @author Chris Wilper
 * @version $Id$
 */
@Path("/{pid}/methods")
public class MethodResource extends BaseRestResource {

    /**
     * Lists all Service Definitions methods that can be invoked on a digital
     * object.
     *
     * GET /objects/{pid}/methods ? format asOfDateTime
     */
    @GET
    @Produces({ HTML, XML })
    public Response getAllObjectMethods(
            @PathParam(RestParam.PID)
            String pid,
            @QueryParam(RestParam.AS_OF_DATE_TIME)
            String dTime,
            @QueryParam("format")
            @DefaultValue(HTML)
            String format) {
        return getObjectMethodsForSDefImpl(pid, null, dTime, format);
    }

    /**
     * Lists all Service Definitions methods that can be invoked on a digital
     * object, for the supplied Service Definition.
     *
     * GET /objects/{pid}/methods/{sDef} ? format asOfDateTime
     */
    @Path("/{sDef}")
    @GET
    @Produces({ HTML, XML })
    public Response getObjectMethodsForSDef(
            @PathParam(RestParam.PID)
            String pid,
            @PathParam(RestParam.SDEF)
            String sDef,
            @QueryParam(RestParam.AS_OF_DATE_TIME)
            String dTime,
            @QueryParam("format")
            @DefaultValue(HTML)
            String format) {
        return getObjectMethodsForSDefImpl(pid, sDef, dTime, format);
    }

    private Response getObjectMethodsForSDefImpl(String pid, String sDef, String dTime, String format) {
        try {
            Date asOfDateTime = DateUtility.convertStringToDate(dTime);
            Context context = getContext();
            ObjectMethodsDef[] methodDefs = apiAService.listMethods(context, pid, asOfDateTime);
            String xml = getSerializer(context).objectMethodsToXml(methodDefs, pid, sDef, asOfDateTime);

            MediaType mime = RestHelper.getContentType(format);

            if (TEXT_HTML.isCompatible(mime)) {
                CharArrayWriter writer = new CharArrayWriter();
                transform(xml, "access/listMethods.xslt", writer);
                xml = writer.toString();
            }

            return Response.ok(xml, mime).build();
        } catch (Exception ex) {
            return handleException(ex);
        }
    }

}

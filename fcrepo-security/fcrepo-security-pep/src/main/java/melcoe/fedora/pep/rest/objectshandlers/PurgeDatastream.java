/*
 * File: PurgeDatastream.java
 *
 * Copyright 2009 2DC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package melcoe.fedora.pep.rest.objectshandlers;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import melcoe.fedora.pep.PEPException;
import melcoe.fedora.pep.rest.filters.AbstractFilter;
import melcoe.fedora.util.LogUtil;

import org.apache.log4j.Logger;

import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.RequestCtx;

import fedora.common.Constants;

/**
 * Handles the PurgeDatastream operation.
 * 
 * @author nish.naidoo@gmail.com
 */
public class PurgeDatastream
        extends AbstractFilter {

    private static Logger log =
            Logger.getLogger(PurgeDatastream.class.getName());

    /**
     * Default constructor.
     * 
     * @throws PEPException
     */
    public PurgeDatastream()
            throws PEPException {
        super();
    }

    /*
     * (non-Javadoc)
     * @see
     * melcoe.fedora.pep.rest.filters.RESTFilter#handleRequest(javax.servlet
     * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public RequestCtx handleRequest(HttpServletRequest request,
                                    HttpServletResponse response)
            throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getName() + "/handleRequest!");
        }

        String path = request.getPathInfo();
        String[] parts = path.split("/");

        String pid = parts[1];
        String dsid = parts[3];
        String startDT = request.getParameter("startDT");
        String endDT = request.getParameter("endDT");

        RequestCtx req = null;
        Map<URI, AttributeValue> actions = new HashMap<URI, AttributeValue>();
        Map<URI, AttributeValue> resAttr = new HashMap<URI, AttributeValue>();
        try {
            if (pid != null && !"".equals(pid)) {
                resAttr.put(Constants.OBJECT.PID.getURI(),
                            new StringAttribute(pid));
            }
            if (pid != null && !"".equals(pid)) {
                resAttr.put(new URI(XACML_RESOURCE_ID),
                            new AnyURIAttribute(new URI(pid)));
            }
            if (dsid != null && !"".equals(dsid)) {
                resAttr.put(Constants.DATASTREAM.ID.getURI(),
                            new StringAttribute(dsid));
            }
            if (startDT != null && !"".equals(startDT)) {
                resAttr.put(Constants.DATASTREAM.CREATED_DATETIME.getURI(),
                            DateTimeAttribute.getInstance(startDT));
            }
            if (endDT != null && !"".equals(endDT)) {
                resAttr.put(Constants.DATASTREAM.AS_OF_DATETIME.getURI(),
                            DateTimeAttribute.getInstance(endDT));
            }

            actions.put(Constants.ACTION.ID.getURI(),
                        new StringAttribute(Constants.ACTION.PURGE_DATASTREAM
                                .getURI().toASCIIString()));
            actions.put(Constants.ACTION.API.getURI(),
                        new StringAttribute(Constants.ACTION.APIM.getURI()
                                .toASCIIString()));

            req =
                    getContextHandler().buildRequest(getSubjects(request),
                                                     actions,
                                                     resAttr,
                                                     getEnvironment(request));

            LogUtil.statLog(request.getRemoteUser(),
                            Constants.ACTION.PURGE_DATASTREAM.getURI()
                                    .toASCIIString(),
                            pid,
                            dsid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e.getMessage(), e);
        }

        return req;
    }

    /*
     * (non-Javadoc)
     * @see
     * melcoe.fedora.pep.rest.filters.RESTFilter#handleResponse(javax.servlet
     * .http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public RequestCtx handleResponse(HttpServletRequest request,
                                     HttpServletResponse response)
            throws IOException, ServletException {
        return null;
    }
}

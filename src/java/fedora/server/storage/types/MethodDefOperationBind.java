package fedora.server.storage.types;

/**
 *
 * <p><b>Title:</b> MethodDefHTTPBind.java</p>
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
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class MethodDefOperationBind extends MethodDef
{

    public static final String HTTP_MESSAGE_PROTOCOL = "HTTP";
    public static final String SOAP_MESSAGE_PROTOCOL = "SOAP";

    public String protocolType = null;
    public String serviceBindingAddress = null;
    public String operationLocation = null;
    public String operationURL = null;

    public String[] dsBindingKeys = new String[0];

    public MethodDefOperationBind()
    {
    }

}
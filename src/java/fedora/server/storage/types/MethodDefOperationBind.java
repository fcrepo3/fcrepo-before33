package fedora.server.storage.types;

/**
 *
 * <p><b>Title:</b> MethodDefHTTPBind.java</p>
 * <p><b>Description:</b> </p>
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
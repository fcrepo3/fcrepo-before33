package fedora.server.storage.types;

/**
 * <p>Title: MethodDefHTTPBind.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
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
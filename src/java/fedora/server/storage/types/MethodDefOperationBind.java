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

    public String protocolType;
    public String serviceBindingAddress;
    public String operationLocation;
    public String operationURL;

    public MethodDefOperationBind()
    {
    }

}
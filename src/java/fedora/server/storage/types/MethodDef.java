package fedora.server.storage.types;

/**
 * <p>Title: MethodDef.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import java.util.Hashtable;

public class MethodDef
{

    public String methodName = null;
    public String methodLabel = null;
    public MethodParmDef[] methodParms = new MethodParmDef[0];

    // For linkages to WSDL
    public String wsdlMessageName = null;
    public String wsdlOutputMessageName = null;
    public Hashtable wsdlMsgParts;

    public MethodDef()
    {
    }

}
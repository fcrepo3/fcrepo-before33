package fedora.server.storage.service;

/**
 * <p>Title: MmapMethodDef.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import java.util.Hashtable;
import fedora.server.storage.types.MethodDef;

public class MmapMethodDef extends MethodDef
{
    // For linkages to WSDL
    public String wsdlOperationName = null;
    public String wsdlMessageName = null;
    public String wsdlOutputMessageName = null;
    public MmapMethodParmDef[] wsdlMsgParts = new MmapMethodParmDef[0];
    public Hashtable wsdlMsgPartToParmDefTbl;

    public MmapMethodDef()
    {
    }

}
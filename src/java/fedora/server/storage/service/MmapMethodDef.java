package fedora.server.storage.service;

import java.util.Hashtable;
import fedora.server.storage.types.MethodDef;

/**
 *
 * <p><b>Title:</b> MmapMethodDef.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
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
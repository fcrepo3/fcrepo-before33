package fedora.server.storage.service;

/**
 * <p>Title: MmapMethodParmDef.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import fedora.server.storage.types.MethodParmDef;

public class MmapMethodParmDef extends MethodParmDef
{
    // For linkage to WSDL.
    // A parm definition in the Fedora Method Map references its related
    // WSDL message part by name.  The Fedora Method Map endows each
    // WSDL message part with a special Fedora-specific parm type
    // (fedora:datastreamInput, fedora:userInput, fedora:defaultInput).
    // When the message part is considered a fedora:datastreamInput,
    // then the
    public String wsdlMessagePartName = null;

    public MmapMethodParmDef()
    {

    }
}
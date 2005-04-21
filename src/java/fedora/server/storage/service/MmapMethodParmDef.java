package fedora.server.storage.service;

import fedora.server.storage.types.MethodParmDef;

/**
 *
 * <p><b>Title:</b> MmapMethodParmDef.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
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
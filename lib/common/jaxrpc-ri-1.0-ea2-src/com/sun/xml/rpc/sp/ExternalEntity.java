// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ExternalEntity.java

package com.sun.xml.rpc.sp;

import java.io.IOException;
import java.net.URL;
import org.xml.sax.*;

// Referenced classes of package com.sun.xml.rpc.sp:
//            EntityDecl, Resolver

class ExternalEntity extends EntityDecl {

    String systemId;
    String publicId;
    String notation;

    public ExternalEntity(Locator l) {
    }

    public InputSource getInputSource(EntityResolver r) throws SAXException, IOException {
        InputSource retval = r.resolveEntity(publicId, systemId);
        if(retval == null)
            retval = Resolver.createInputSource(new URL(systemId), false);
        return retval;
    }
}

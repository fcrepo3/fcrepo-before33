// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DataHandlerAttachmentEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import javax.activation.DataHandler;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            AttachmentEncoder

public class DataHandlerAttachmentEncoder
    implements AttachmentEncoder {

    private static final AttachmentEncoder encoder = new DataHandlerAttachmentEncoder();

    private DataHandlerAttachmentEncoder() {
    }

    public static AttachmentEncoder getInstance() {
        return encoder;
    }

    public DataHandler objectToDataHandler(Object obj) throws Exception {
        return (DataHandler)obj;
    }

    public Object dataHandlerToObject(DataHandler dataHandler) throws Exception {
        return dataHandler;
    }

}

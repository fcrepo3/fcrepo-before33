// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ImageAttachmentEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import javax.activation.DataHandler;

// Referenced classes of package com.sun.xml.rpc.encoding.simpletype:
//            AttachmentEncoder

public class ImageAttachmentEncoder
    implements AttachmentEncoder {

    private static final AttachmentEncoder encoder = new ImageAttachmentEncoder();

    private ImageAttachmentEncoder() {
    }

    public static AttachmentEncoder getInstance() {
        return encoder;
    }

    public DataHandler objectToDataHandler(Object obj) throws Exception {
        DataHandler dataHandler = new DataHandler(obj, "image/jpeg");
        return dataHandler;
    }

    public Object dataHandlerToObject(DataHandler dataHandler) throws Exception {
        return dataHandler.getContent();
    }

}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AttachmentEncoder.java

package com.sun.xml.rpc.encoding.simpletype;

import javax.activation.DataHandler;

public interface AttachmentEncoder {

    public abstract DataHandler objectToDataHandler(Object obj) throws Exception;

    public abstract Object dataHandlerToObject(DataHandler datahandler) throws Exception;
}

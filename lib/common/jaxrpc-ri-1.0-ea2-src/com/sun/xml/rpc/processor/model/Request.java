// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Request.java

package com.sun.xml.rpc.processor.model;


// Referenced classes of package com.sun.xml.rpc.processor.model:
//            Message, ModelVisitor

public class Request extends Message {

    public Request() {
    }

    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CallRequest.java

package com.sun.xml.rpc.client.dii;


// Referenced classes of package com.sun.xml.rpc.client.dii:
//            CallImpl

public class CallRequest {

    public CallImpl call;
    public Object parameters[];

    public CallRequest(CallImpl call, Object parameters[]) {
        this.call = call;
        this.parameters = parameters;
    }
}

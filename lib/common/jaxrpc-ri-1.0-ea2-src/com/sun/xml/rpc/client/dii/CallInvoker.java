// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CallInvoker.java

package com.sun.xml.rpc.client.dii;

import com.sun.xml.rpc.encoding.JAXRPCDeserializer;
import com.sun.xml.rpc.encoding.JAXRPCSerializer;
import com.sun.xml.rpc.encoding.soap.SOAPResponseStructure;
import java.rmi.RemoteException;

// Referenced classes of package com.sun.xml.rpc.client.dii:
//            CallRequest

public interface CallInvoker {

    public abstract SOAPResponseStructure doInvoke(CallRequest callrequest, JAXRPCSerializer jaxrpcserializer, JAXRPCDeserializer jaxrpcdeserializer, JAXRPCDeserializer jaxrpcdeserializer1) throws RemoteException;

    public abstract void doInvokeOneWay(CallRequest callrequest, JAXRPCSerializer jaxrpcserializer);
}

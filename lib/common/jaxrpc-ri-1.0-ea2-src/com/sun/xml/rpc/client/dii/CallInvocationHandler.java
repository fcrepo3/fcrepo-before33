// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CallInvocationHandler.java

package com.sun.xml.rpc.client.dii;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.xml.rpc.Call;

public class CallInvocationHandler
    implements InvocationHandler {

    private HashMap callMap;

    public CallInvocationHandler() {
        callMap = new HashMap();
    }

    public void addCall(Method key, Call call) {
        callMap.put(key, call);
    }

    public Call getCall(Method key) {
        return (Call)callMap.get(key);
    }

    public Object invoke(Object proxy, Method method, Object args[]) throws Throwable {
        return getCall(method).invoke(args);
    }
}

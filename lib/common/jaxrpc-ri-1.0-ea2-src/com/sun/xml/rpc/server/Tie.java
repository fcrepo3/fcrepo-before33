// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Tie.java

package com.sun.xml.rpc.server;

import java.rmi.Remote;

public interface Tie {

    public abstract void destroy();

    public abstract void setTarget(Remote remote);

    public abstract Remote getTarget();
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceLifecycle.java

package javax.xml.rpc.server;


public interface ServiceLifecycle {

    public abstract void init(Object obj);

    public abstract void destroy();
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   MessageContext.java

package javax.xml.rpc.handler;

import java.util.Iterator;

public interface MessageContext {

    public abstract void setProperty(String s, Object obj);

    public abstract Object getProperty(String s);

    public abstract void removeProperty(String s);

    public abstract boolean containsProperty(String s);

    public abstract Iterator getPropertyNames();
}

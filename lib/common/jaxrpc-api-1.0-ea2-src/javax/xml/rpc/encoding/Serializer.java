// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Serializer.java

package javax.xml.rpc.encoding;

import java.io.Serializable;

public interface Serializer
    extends Serializable {

    public abstract String getMechanismType();
}

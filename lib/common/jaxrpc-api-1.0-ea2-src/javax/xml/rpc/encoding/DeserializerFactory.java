// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DeserializerFactory.java

package javax.xml.rpc.encoding;

import java.io.Serializable;
import java.util.Iterator;

// Referenced classes of package javax.xml.rpc.encoding:
//            Deserializer

public interface DeserializerFactory
    extends Serializable {

    public abstract Deserializer getDeserializerAs(String s);

    public abstract Iterator getSupportedMechanismTypes();
}

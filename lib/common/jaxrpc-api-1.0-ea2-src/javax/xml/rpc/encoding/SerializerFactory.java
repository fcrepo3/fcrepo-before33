// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SerializerFactory.java

package javax.xml.rpc.encoding;

import java.io.Serializable;
import java.util.Iterator;

// Referenced classes of package javax.xml.rpc.encoding:
//            Serializer

public interface SerializerFactory
    extends Serializable {

    public abstract Serializer getSerializerAs(String s);

    public abstract Iterator getSupportedMechanismTypes();
}

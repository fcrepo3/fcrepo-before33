// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMappingRegistry.java

package javax.xml.rpc.encoding;

import java.io.Serializable;

// Referenced classes of package javax.xml.rpc.encoding:
//            TypeMapping

public interface TypeMappingRegistry
    extends Serializable {

    public abstract TypeMapping register(String s, TypeMapping typemapping);

    public abstract void registerDefault(TypeMapping typemapping);

    public abstract TypeMapping getDefaultTypeMapping();

    public abstract String[] getRegisteredNamespaces();

    public abstract TypeMapping getTypeMapping(String s);

    public abstract TypeMapping createTypeMapping();

    public abstract TypeMapping unregisterTypeMapping(String s);

    public abstract boolean removeTypeMapping(TypeMapping typemapping);

    public abstract void clear();
}

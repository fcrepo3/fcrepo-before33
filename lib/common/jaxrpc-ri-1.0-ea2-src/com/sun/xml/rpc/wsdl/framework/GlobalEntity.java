// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   GlobalEntity.java

package com.sun.xml.rpc.wsdl.framework;


// Referenced classes of package com.sun.xml.rpc.wsdl.framework:
//            Entity, GloballyKnown, Defining, Kind

public abstract class GlobalEntity extends Entity
    implements GloballyKnown {

    private Defining _defining;
    private String _name;

    public GlobalEntity(Defining defining) {
        _defining = defining;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public abstract Kind getKind();

    public Defining getDefining() {
        return _defining;
    }
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ObjectArraySerializer.java

package com.sun.xml.rpc.encoding;


// Referenced classes of package com.sun.xml.rpc.encoding:
//            SOAPInstanceBuilder, ArraySerializerBase, ObjectArraySerializer

class ObjectArraySerializer$ObjectArrayInstanceBuilder
    implements SOAPInstanceBuilder {

    Object instance[];
    int dimOffsets[];
    private final ObjectArraySerializer this$0; /* synthetic field */

    ObjectArraySerializer$ObjectArrayInstanceBuilder(ObjectArraySerializer this$0, int dimOffsets[]) {
        this.this$0 = this$0;
        instance = null;
        this.dimOffsets = null;
        this.dimOffsets = dimOffsets;
    }

    public int memberGateType(int memberIndex) {
        return 6;
    }

    public void construct() {
        throw new IllegalStateException();
    }

    public void setMember(int index, Object memberValue) {
        int position[] = ArraySerializerBase.positionFromIndex(index, dimOffsets);
        ObjectArraySerializer.setElement(instance, position, memberValue);
    }

    public void initialize() {
    }

    public void setInstance(Object instance) {
        this.instance = (Object[])instance;
    }

    public Object getInstance() {
        return ((Object) (instance));
    }
}

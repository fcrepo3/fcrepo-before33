// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Block.java

package com.sun.xml.rpc.processor.model;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            ModelObject, ModelVisitor, AbstractType

public class Block extends ModelObject {

    public static final int BODY = 1;
    public static final int HEADER = 2;
    private QName name;
    private AbstractType type;
    private int location;

    public Block(QName name) {
        this.name = name;
    }

    public Block(QName name, AbstractType type) {
        this.name = name;
        this.type = type;
    }

    public QName getName() {
        return name;
    }

    public AbstractType getType() {
        return type;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int i) {
        location = i;
    }

    public void setType(AbstractType type) {
        this.type = type;
    }

    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Fault.java

package com.sun.xml.rpc.processor.model;

import com.sun.xml.rpc.processor.model.java.JavaException;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            ModelObject, ModelVisitor, Block

public class Fault extends ModelObject {

    private String name;
    private Block block;
    private JavaException javaException;

    public Fault(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block b) {
        block = b;
    }

    public JavaException getJavaException() {
        return javaException;
    }

    public void setJavaException(JavaException e) {
        javaException = e;
    }

    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

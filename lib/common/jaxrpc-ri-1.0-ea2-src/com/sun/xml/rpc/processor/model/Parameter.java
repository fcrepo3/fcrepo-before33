// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parameter.java

package com.sun.xml.rpc.processor.model;

import com.sun.xml.rpc.processor.model.java.JavaParameter;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            ModelObject, ModelVisitor, AbstractType, Block

public class Parameter extends ModelObject {

    private String name;
    private JavaParameter javaParameter;
    private AbstractType type;
    private Block block;
    private Parameter link;
    private boolean embedded;

    public Parameter(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public JavaParameter getJavaParameter() {
        return javaParameter;
    }

    public void setJavaParameter(JavaParameter p) {
        javaParameter = p;
    }

    public AbstractType getType() {
        return type;
    }

    public void setType(AbstractType t) {
        type = t;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block d) {
        block = d;
    }

    public Parameter getLinkedParameter() {
        return link;
    }

    public void setLinkedParameter(Parameter p) {
        link = p;
    }

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean b) {
        embedded = b;
    }

    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

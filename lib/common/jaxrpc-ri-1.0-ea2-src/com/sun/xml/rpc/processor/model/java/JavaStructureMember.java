// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaStructureMember.java

package com.sun.xml.rpc.processor.model.java;


// Referenced classes of package com.sun.xml.rpc.processor.model.java:
//            JavaType

public class JavaStructureMember {

    private String name;
    private JavaType type;
    private boolean isPublic;
    private String readMethod;
    private String writeMethod;
    private Object owner;

    public JavaStructureMember(String name, JavaType type, Object owner) {
        this(name, type, owner, false);
    }

    public JavaStructureMember(String name, JavaType type, Object owner, boolean isPublic) {
        this.isPublic = false;
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.isPublic = isPublic;
    }

    public String getName() {
        return name;
    }

    public JavaType getType() {
        return type;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public String getReadMethod() {
        return readMethod;
    }

    public void setReadMethod(String readMethod) {
        this.readMethod = readMethod;
    }

    public String getWriteMethod() {
        return writeMethod;
    }

    public void setWriteMethod(String writeMethod) {
        this.writeMethod = writeMethod;
    }

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }
}

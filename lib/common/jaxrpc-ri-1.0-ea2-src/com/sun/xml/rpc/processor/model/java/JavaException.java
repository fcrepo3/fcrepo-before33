// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaException.java

package com.sun.xml.rpc.processor.model.java;


// Referenced classes of package com.sun.xml.rpc.processor.model.java:
//            JavaType

public class JavaException {

    private String name;
    private String propertyName;
    private JavaType propertyType;
    private boolean present;

    public JavaException(String name, String propertyName, JavaType propertyType, boolean present) {
        this.name = name;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
        this.present = present;
    }

    public String getName() {
        return name;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public JavaType getPropertyType() {
        return propertyType;
    }

    public boolean isPresent() {
        return present;
    }
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaParameter.java

package com.sun.xml.rpc.processor.model.java;

import com.sun.xml.rpc.processor.model.Parameter;

// Referenced classes of package com.sun.xml.rpc.processor.model.java:
//            JavaType

public class JavaParameter {

    private String name;
    private JavaType type;
    private Parameter parameter;
    private boolean holder;

    public JavaParameter(String name, JavaType type, Parameter parameter) {
        this(name, type, parameter, false);
    }

    public JavaParameter(String name, JavaType type, Parameter parameter, boolean holder) {
        this.name = name;
        this.type = type;
        this.parameter = parameter;
        this.holder = holder;
    }

    public String getName() {
        return name;
    }

    public JavaType getType() {
        return type;
    }

    public Parameter getParameter() {
        return parameter;
    }

    public boolean isHolder() {
        return holder;
    }
}

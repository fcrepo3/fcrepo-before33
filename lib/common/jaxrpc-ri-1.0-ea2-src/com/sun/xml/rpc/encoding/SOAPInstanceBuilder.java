// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPInstanceBuilder.java

package com.sun.xml.rpc.encoding;


public interface SOAPInstanceBuilder {

    public static final int GATES_CONSTRUCTION = 1;
    public static final int GATES_INITIALIZATION = 2;
    public static final int REQUIRES_CREATION = 4;
    public static final int REQUIRES_INITIALIZATION = 8;
    public static final int REQUIRES_COMPLETION = 16;

    public abstract int memberGateType(int i);

    public abstract void construct();

    public abstract void setMember(int i, Object obj);

    public abstract void initialize();

    public abstract void setInstance(Object obj);

    public abstract Object getInstance();
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPArrayType.java

package com.sun.xml.rpc.processor.model.soap;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.soap:
//            SOAPType, SOAPTypeVisitor

public class SOAPArrayType extends SOAPType {

    private QName elementName;
    private SOAPType elementType;
    private int rank;
    private int size[];

    public SOAPArrayType(QName name) {
        super(name);
    }

    public QName getElementName() {
        return elementName;
    }

    public void setElementName(QName name) {
        elementName = name;
    }

    public SOAPType getElementType() {
        return elementType;
    }

    public void setElementType(SOAPType type) {
        elementType = type;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int i) {
        rank = i;
    }

    public int[] getSize() {
        return size;
    }

    public void setSize(int a[]) {
        size = a;
    }

    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

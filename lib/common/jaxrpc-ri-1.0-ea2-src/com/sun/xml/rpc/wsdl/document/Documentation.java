// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Documentation.java

package com.sun.xml.rpc.wsdl.document;


// Referenced classes of package com.sun.xml.rpc.wsdl.document:
//            WSDLDocumentVisitor

public class Documentation {

    private String content;

    public Documentation(String s) {
        content = s;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String s) {
        content = s;
    }

    public void accept(WSDLDocumentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Main.java

package com.sun.xml.rpc.tools.wsdlp;

import com.sun.xml.rpc.util.localization.Resources;
import com.sun.xml.rpc.wsdl.framework.Entity;
import com.sun.xml.rpc.wsdl.framework.ParserListener;
import java.io.PrintStream;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.tools.wsdlp:
//            Main

class Main$1
    implements ParserListener {

    private final Main this$0; /* synthetic field */

    Main$1(Main this$0) {
        this.this$0 = this$0;
    }

    public void ignoringExtension(QName name, QName parent) {
        System.err.println(Main.access$000(this$0).getString("message.ignoring", new String[] {
            name.getLocalPart(), name.getNamespaceURI()
        }));
    }

    public void doneParsingEntity(QName element, Entity entity) {
        System.err.println(Main.access$000(this$0).getString("message.processed", new String[] {
            element.getLocalPart(), element.getNamespaceURI()
        }));
    }
}

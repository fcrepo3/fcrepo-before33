// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser2.java

package com.sun.xml.rpc.sp;

import org.xml.sax.Locator;

// Referenced classes of package com.sun.xml.rpc.sp:
//            Parser2, InputEntity

class Parser2$DocLocator
    implements Locator {

    private final Parser2 this$0; /* synthetic field */

    Parser2$DocLocator(Parser2 this$0) {
        this.this$0 = this$0;
    }

    public String getPublicId() {
        return Parser2.access$000(this$0) != null ? Parser2.access$000(this$0).getPublicId() : null;
    }

    public String getSystemId() {
        return Parser2.access$000(this$0) != null ? Parser2.access$000(this$0).getSystemId() : null;
    }

    public int getLineNumber() {
        return Parser2.access$000(this$0) != null ? Parser2.access$000(this$0).getLineNumber() : -1;
    }

    public int getColumnNumber() {
        return Parser2.access$000(this$0) != null ? Parser2.access$000(this$0).getColumnNumber() : -1;
    }
}

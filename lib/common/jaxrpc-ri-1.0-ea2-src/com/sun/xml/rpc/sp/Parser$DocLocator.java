// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser.java

package com.sun.xml.rpc.sp;

import org.xml.sax.Locator;

// Referenced classes of package com.sun.xml.rpc.sp:
//            Parser, InputEntity

class Parser$DocLocator
    implements Locator {

    private final Parser this$0; /* synthetic field */

    Parser$DocLocator(Parser this$0) {
        this.this$0 = this$0;
    }

    public String getPublicId() {
        return Parser.access$000(this$0) != null ? Parser.access$000(this$0).getPublicId() : null;
    }

    public String getSystemId() {
        return Parser.access$000(this$0) != null ? Parser.access$000(this$0).getSystemId() : null;
    }

    public int getLineNumber() {
        return Parser.access$000(this$0) != null ? Parser.access$000(this$0).getLineNumber() : -1;
    }

    public int getColumnNumber() {
        return Parser.access$000(this$0) != null ? Parser.access$000(this$0).getColumnNumber() : -1;
    }
}

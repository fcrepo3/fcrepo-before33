// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   QNameHolder.java

package javax.xml.rpc.holders;

import javax.xml.rpc.namespace.QName;

// Referenced classes of package javax.xml.rpc.holders:
//            Holder

public final class QNameHolder
    implements Holder {

    public QName value;

    public QNameHolder() {
    }

    public QNameHolder(QName myQName) {
        value = myQName;
    }
}

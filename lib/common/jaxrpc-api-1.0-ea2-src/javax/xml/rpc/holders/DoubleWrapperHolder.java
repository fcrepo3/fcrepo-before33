// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DoubleWrapperHolder.java

package javax.xml.rpc.holders;


// Referenced classes of package javax.xml.rpc.holders:
//            Holder

public final class DoubleWrapperHolder
    implements Holder {

    public Double value;

    public DoubleWrapperHolder() {
    }

    public DoubleWrapperHolder(Double mydouble) {
        value = mydouble;
    }
}

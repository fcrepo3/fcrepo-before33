// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   BooleanWrapperHolder.java

package javax.xml.rpc.holders;


// Referenced classes of package javax.xml.rpc.holders:
//            Holder

public final class BooleanWrapperHolder
    implements Holder {

    public Boolean value;

    public BooleanWrapperHolder() {
    }

    public BooleanWrapperHolder(Boolean myboolean) {
        value = myboolean;
    }
}

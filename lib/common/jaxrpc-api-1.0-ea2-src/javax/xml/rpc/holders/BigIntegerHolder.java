// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   BigIntegerHolder.java

package javax.xml.rpc.holders;

import java.math.BigInteger;

// Referenced classes of package javax.xml.rpc.holders:
//            Holder

public final class BigIntegerHolder
    implements Holder {

    public BigInteger value;

    public BigIntegerHolder() {
    }

    public BigIntegerHolder(BigInteger myBigInteger) {
        value = myBigInteger;
    }
}

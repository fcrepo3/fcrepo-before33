// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ByteArrayHolder.java

package javax.xml.rpc.holders;


// Referenced classes of package javax.xml.rpc.holders:
//            Holder

public final class ByteArrayHolder
    implements Holder {

    public byte value[];

    public ByteArrayHolder() {
    }

    public ByteArrayHolder(byte mybyteArray[]) {
        value = mybyteArray;
    }
}

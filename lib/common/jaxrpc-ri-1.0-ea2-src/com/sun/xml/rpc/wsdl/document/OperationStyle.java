// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   OperationStyle.java

package com.sun.xml.rpc.wsdl.document;


public final class OperationStyle {

    public static final OperationStyle ONE_WAY = new OperationStyle();
    public static final OperationStyle REQUEST_RESPONSE = new OperationStyle();
    public static final OperationStyle SOLICIT_RESPONSE = new OperationStyle();
    public static final OperationStyle NOTIFICATION = new OperationStyle();

    private OperationStyle() {
    }

}

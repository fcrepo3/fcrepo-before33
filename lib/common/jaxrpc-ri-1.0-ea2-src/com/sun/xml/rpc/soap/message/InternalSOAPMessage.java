// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   InternalSOAPMessage.java

package com.sun.xml.rpc.soap.message;

import com.sun.xml.rpc.util.NullIterator;
import java.util.*;
import javax.xml.soap.SOAPMessage;

// Referenced classes of package com.sun.xml.rpc.soap.message:
//            SOAPBlockInfo, SOAPHeaderBlockInfo

public class InternalSOAPMessage {

    public static final int NO_OPERATION = -1;
    private SOAPMessage _message;
    private List _headers;
    private SOAPBlockInfo _body;
    private int _operationCode;
    private boolean _failure;
    private boolean _headerNotUnderstood;

    public InternalSOAPMessage(SOAPMessage message) {
        _message = message;
        _operationCode = -1;
    }

    public SOAPMessage getMessage() {
        return _message;
    }

    public void add(SOAPHeaderBlockInfo headerInfo) {
        if(headerInfo != null) {
            if(_headers == null)
                _headers = new ArrayList();
            _headers.add(headerInfo);
        }
    }

    public Iterator headers() {
        if(_headers == null)
            return new NullIterator();
        else
            return _headers.iterator();
    }

    public SOAPBlockInfo getBody() {
        return _body;
    }

    public void setBody(SOAPBlockInfo body) {
        _body = body;
    }

    public int getOperationCode() {
        return _operationCode;
    }

    public void setOperationCode(int i) {
        _operationCode = i;
    }

    public boolean isHeaderNotUnderstood() {
        return _headerNotUnderstood;
    }

    public void setHeaderNotUnderstood(boolean b) {
        _headerNotUnderstood = b;
    }

    public boolean isFailure() {
        return _failure;
    }

    public void setFailure(boolean b) {
        _failure = b;
    }
}

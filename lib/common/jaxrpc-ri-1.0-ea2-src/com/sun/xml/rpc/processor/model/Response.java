// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Response.java

package com.sun.xml.rpc.processor.model;

import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            Message, ModelException, Block, ModelVisitor

public class Response extends Message {

    private Map _faultBlocks;

    public Response() {
        _faultBlocks = new HashMap();
    }

    public void addFaultBlock(Block b) {
        if(_faultBlocks.containsKey(b.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            _faultBlocks.put(b.getName(), b);
            return;
        }
    }

    public Iterator getFaultBlocks() {
        return _faultBlocks.values().iterator();
    }

    public int getFaultBlockCount() {
        return _faultBlocks.size();
    }

    public void accept(ModelVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

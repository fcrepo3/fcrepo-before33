// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Message.java

package com.sun.xml.rpc.processor.model;

import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            ModelObject, ModelException, Block, Parameter

public abstract class Message extends ModelObject {

    private Map _bodyBlocks;
    private Map _headerBlocks;
    private List _parameters;
    private Map _parametersByName;

    public Message() {
        _bodyBlocks = new HashMap();
        _headerBlocks = new HashMap();
        _parameters = new ArrayList();
        _parametersByName = new HashMap();
    }

    public void addBodyBlock(Block b) {
        if(_bodyBlocks.containsKey(b.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            _bodyBlocks.put(b.getName(), b);
            b.setLocation(1);
            return;
        }
    }

    public Iterator getBodyBlocks() {
        return _bodyBlocks.values().iterator();
    }

    public int getBodyBlockCount() {
        return _bodyBlocks.size();
    }

    public void addHeaderBlock(Block b) {
        if(_headerBlocks.containsKey(b.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            _headerBlocks.put(b.getName(), b);
            b.setLocation(2);
            return;
        }
    }

    public Iterator getHeaderBlocks() {
        return _headerBlocks.values().iterator();
    }

    public int getHeaderBlockCount() {
        return _headerBlocks.size();
    }

    public void addParameter(Parameter p) {
        if(_parametersByName.containsKey(p.getName())) {
            throw new ModelException("model.uniqueness");
        } else {
            _parameters.add(p);
            _parametersByName.put(p.getName(), p);
            return;
        }
    }

    public Iterator getParameters() {
        return _parameters.iterator();
    }
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ModelVisitor.java

package com.sun.xml.rpc.processor.model;


// Referenced classes of package com.sun.xml.rpc.processor.model:
//            Model, Service, Port, Operation, 
//            Request, Response, Fault, Block, 
//            Parameter

public interface ModelVisitor {

    public abstract void visit(Model model) throws Exception;

    public abstract void visit(Service service) throws Exception;

    public abstract void visit(Port port) throws Exception;

    public abstract void visit(Operation operation) throws Exception;

    public abstract void visit(Request request) throws Exception;

    public abstract void visit(Response response) throws Exception;

    public abstract void visit(Fault fault) throws Exception;

    public abstract void visit(Block block) throws Exception;

    public abstract void visit(Parameter parameter) throws Exception;
}

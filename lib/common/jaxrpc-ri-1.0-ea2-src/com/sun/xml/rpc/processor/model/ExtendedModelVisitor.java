// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   ExtendedModelVisitor.java

package com.sun.xml.rpc.processor.model;

import java.util.Iterator;

// Referenced classes of package com.sun.xml.rpc.processor.model:
//            Service, Port, Operation, Block,
//            Parameter, Fault, Model, Message,
//            Request, Response

public class ExtendedModelVisitor {

    public ExtendedModelVisitor() {
    }

    public void visit(Model model) throws Exception {
        preVisit(model);
        Service service;
        for(Iterator iter = model.getServices(); iter.hasNext(); postVisit(service)) {
            service = (Service)iter.next();
            preVisit(service);
            Port port;
            for(Iterator iter2 = service.getPorts(); iter2.hasNext(); postVisit(port)) {
                port = (Port)iter2.next();
                preVisit(port);
                Operation operation;
                for(Iterator iter3 = port.getOperations(); iter3.hasNext(); postVisit(operation)) {
                    operation = (Operation)iter3.next();
                    preVisit(operation);
                    Request request = operation.getRequest();
                    if(request != null) {
                        preVisit(request);
                        Block block;
                        for(Iterator iter4 = request.getHeaderBlocks(); iter4.hasNext(); visitHeaderBlock(block))
                            block = (Block)iter4.next();

                        Block block2;
                        for(Iterator iter4 = request.getBodyBlocks(); iter4.hasNext(); visitBodyBlock(block2))
                            block2 = (Block)iter4.next();

                        Parameter parameter;
                        for(Iterator iter4 = request.getParameters(); iter4.hasNext(); visit(parameter))
                            parameter = (Parameter)iter4.next();

                        postVisit(request);
                    }
                    Response response = operation.getResponse();
                    if(request != null) {
                        preVisit(response);
                        Block block;
                        for(Iterator iter4 = response.getHeaderBlocks(); iter4.hasNext(); visitHeaderBlock(block))
                            block = (Block)iter4.next();

                        Block block2;
                        for(Iterator iter4 = response.getBodyBlocks(); iter4.hasNext(); visitBodyBlock(block2))
                            block2 = (Block)iter4.next();

                        Parameter parameter;
                        for(Iterator iter4 = response.getParameters(); iter4.hasNext(); visit(parameter))
                            parameter = (Parameter)iter4.next();

                        postVisit(response);
                    }
                    Fault fault;
                    for(Iterator iter4 = operation.getFaults(); iter4.hasNext(); postVisit(fault)) {
                        fault = (Fault)iter4.next();
                        preVisit(fault);
                        visitFaultBlock(fault.getBlock());
                    }

                }

            }

        }

        postVisit(model);
    }

    protected void preVisit(Model model1) throws Exception {
    }

    protected void postVisit(Model model1) throws Exception {
    }

    protected void preVisit(Service service1) throws Exception {
    }

    protected void postVisit(Service service1) throws Exception {
    }

    protected void preVisit(Port port1) throws Exception {
    }

    protected void postVisit(Port port1) throws Exception {
    }

    protected void preVisit(Operation operation1) throws Exception {
    }

    protected void postVisit(Operation operation1) throws Exception {
    }

    protected void preVisit(Request request1) throws Exception {
    }

    protected void postVisit(Request request1) throws Exception {
    }

    protected void preVisit(Response response1) throws Exception {
    }

    protected void postVisit(Response response1) throws Exception {
    }

    protected void preVisit(Fault fault1) throws Exception {
    }

    protected void postVisit(Fault fault1) throws Exception {
    }

    protected void visitBodyBlock(Block block1) throws Exception {
    }

    protected void visitHeaderBlock(Block block1) throws Exception {
    }

    protected void visitFaultBlock(Block block1) throws Exception {
    }

    protected void visit(Parameter parameter1) throws Exception {
    }
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPTypeVisitor.java

package com.sun.xml.rpc.processor.model.soap;


// Referenced classes of package com.sun.xml.rpc.processor.model.soap:
//            SOAPArrayType, SOAPCustomType, SOAPEnumerationType, SOAPSimpleType, 
//            SOAPAnyType, SOAPOrderedStructureType, SOAPUnorderedStructureType, RPCRequestOrderedStructureType, 
//            RPCRequestUnorderedStructureType, RPCResponseStructureType

public interface SOAPTypeVisitor {

    public abstract void visit(SOAPArrayType soaparraytype) throws Exception;

    public abstract void visit(SOAPCustomType soapcustomtype) throws Exception;

    public abstract void visit(SOAPEnumerationType soapenumerationtype) throws Exception;

    public abstract void visit(SOAPSimpleType soapsimpletype) throws Exception;

    public abstract void visit(SOAPAnyType soapanytype) throws Exception;

    public abstract void visit(SOAPOrderedStructureType soaporderedstructuretype) throws Exception;

    public abstract void visit(SOAPUnorderedStructureType soapunorderedstructuretype) throws Exception;

    public abstract void visit(RPCRequestOrderedStructureType rpcrequestorderedstructuretype) throws Exception;

    public abstract void visit(RPCRequestUnorderedStructureType rpcrequestunorderedstructuretype) throws Exception;

    public abstract void visit(RPCResponseStructureType rpcresponsestructuretype) throws Exception;
}

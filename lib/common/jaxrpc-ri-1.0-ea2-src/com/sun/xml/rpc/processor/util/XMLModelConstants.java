// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   XMLModelConstants.java

package com.sun.xml.rpc.processor.util;

import javax.xml.rpc.namespace.QName;

public interface XMLModelConstants {

    public static final String NS_MODEL = "http://java.sun.com/jax-rpc-ri/xrpcc-model";
    public static final QName QNAME_MODEL = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "model");
    public static final QName QNAME_SERVICE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "service");
    public static final QName QNAME_PORT = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "port");
    public static final QName QNAME_OPERATION = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "operation");
    public static final QName QNAME_REQUEST = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "request");
    public static final QName QNAME_RESPONSE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "response");
    public static final QName QNAME_FAULT = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "fault");
    public static final QName QNAME_BODY_BLOCK = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "bodyBlock");
    public static final QName QNAME_HEADER_BLOCK = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "headerBlock");
    public static final QName QNAME_FAULT_BLOCK = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "faultBlock");
    public static final QName QNAME_PARAMETER = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "parameter");
    public static final QName QNAME_LITERAL_SIMPLE_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "literalSimpleType");
    public static final QName QNAME_LITERAL_SEQUENCE_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "literalSequenceType");
    public static final QName QNAME_LITERAL_ALL_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "literalAllType");
    public static final QName QNAME_LITERAL_FRAGMENT_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "literalFragmentType");
    public static final QName QNAME_LITERAL_ARRAY_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "literalArrayType");
    public static final QName QNAME_SOAP_ARRAY_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "soapArrayType");
    public static final QName QNAME_SOAP_CUSTOM_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "soapCustomType");
    public static final QName QNAME_SOAP_ENUMERATION_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "soapEnumerationType");
    public static final QName QNAME_SOAP_SIMPLE_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "soapSimpleType");
    public static final QName QNAME_SOAP_ANY_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "soapAnyType");
    public static final QName QNAME_VOID_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "voidType");
    public static final QName QNAME_MEMBER = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "member");
    public static final QName QNAME_ATTRIBUTE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "attribute");
    public static final QName QNAME_ELEMENT = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "element");
    public static final QName QNAME_SOAP_ORDERED_STRUCTURE_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "soapOrderedStructureType");
    public static final QName QNAME_SOAP_UNORDERED_STRUCTURE_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "soapUnorderedStructureType");
    public static final QName QNAME_RPC_REQUEST_ORDERED_STRUCTURE_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "rpcRequestOrderedStructureType");
    public static final QName QNAME_RPC_REQUEST_UNORDERED_STRUCTURE_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "rpcRequestUnorderedStructureType");
    public static final QName QNAME_RPC_RESPONSE_STRUCTURE_TYPE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "rpcResponseStructureType");
    public static final QName ATTR_NAME = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "name");
    public static final QName ATTR_REF = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "ref");
    public static final QName ATTR_UNIQUE_NAME = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "uniqueName");
    public static final QName ATTR_TARGET_NAMESPACE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "targetNamespace");
    public static final QName ATTR_JAVA_INTERFACE_NAME = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "javaInterfaceName");
    public static final QName ATTR_STYLE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "style");
    public static final QName ATTR_OVERLOADED = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "overloaded");
    public static final QName ATTR_JAVA_TYPE_NAME = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "javaTypeName");
    public static final QName ATTR_RANK = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "rank");
    public static final QName ATTR_NILLABLE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "nillable");
    public static final QName ATTR_REQUIRED = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "required");
    public static final QName ATTR_REPEATED = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "repeated");
    public static final QName ATTR_ADDRESS = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "address");
    public static final QName ATTR_SOAP_ACTION = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "soapAction");
    public static final QName ATTR_JAVA_EXCEPTION_NAME = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "javaExceptionName");
    public static final QName ATTR_JAVA_NAME = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "javaName");
    public static final QName ATTR_EMBEDDED = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "embedded");
    public static final QName ATTR_BLOCK_NAME = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-model", "blockName");

}

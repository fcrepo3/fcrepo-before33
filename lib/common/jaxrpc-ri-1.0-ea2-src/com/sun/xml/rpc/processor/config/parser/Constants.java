// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Constants.java

package com.sun.xml.rpc.processor.config.parser;

import javax.xml.rpc.namespace.QName;

public interface Constants {

    public static final String NS_XRPCC = "http://java.sun.com/jax-rpc-ri/xrpcc-config";
    public static final QName QNAME_CONFIGURATION = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "configuration");
    public static final QName QNAME_INTERFACE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "interface");
    public static final QName QNAME_MODEL = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "model");
    public static final QName QNAME_RMI = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "rmi");
    public static final QName QNAME_SERVICE = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "service");
    public static final QName QNAME_WSDL = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "wsdl");
    public static final QName QNAME_TYPE_MAPPING_REGISTRY = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "typeMappingRegistry");
    public static final QName QNAME_TYPE_MAPPING = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "typeMapping");
    public static final QName QNAME_ENTRY = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "entry");
    public static final QName QNAME_HANDLER_CHAINS = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "handlerChains");
    public static final QName QNAME_CHAIN = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "chain");
    public static final QName QNAME_HANDLER = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "handler");
    public static final QName QNAME_PROPERTY = new QName("http://java.sun.com/jax-rpc-ri/xrpcc-config", "property");
    public static final String ATTR_NAME = "name";
    public static final String ATTR_VALUE = "value";
    public static final String ATTR_CLASS_NAME = "className";
    public static final String ATTR_DESERIALIZER_FACTORY = "deserializerFactory";
    public static final String ATTR_ENCODING = "encoding";
    public static final String ATTR_ENCODING_STYLE = "encodingStyle";
    public static final String ATTR_JAVA_TYPE = "javaType";
    public static final String ATTR_LOCATION = "location";
    public static final String ATTR_PACKAGE_NAME = "packageName";
    public static final String ATTR_SCHEMA_TYPE = "schemaType";
    public static final String ATTR_SERIALIZER_FACTORY = "serializerFactory";
    public static final String ATTR_SERVANT_NAME = "servantName";
    public static final String ATTR_SOAP_ACTION = "soapAction";
    public static final String ATTR_SOAP_ACTION_BASE = "soapActionBase";
    public static final String ATTR_TARGET_NAMESPACE = "targetNamespace";
    public static final String ATTR_TYPE_NAMESPACE = "typeNamespace";
    public static final String ATTR_RUN_AT = "runAt";
    public static final String ATTR_VALUE_CLIENT = "client";
    public static final String ATTR_VALUE_SERVER = "server";

}

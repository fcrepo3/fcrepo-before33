// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPConstants.java

package com.sun.xml.rpc.wsdl.document.soap;

import javax.xml.rpc.namespace.QName;

public interface SOAPConstants {

    public static final String NS_WSDL_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
    public static final String NS_SOAP_ENCODING = "http://schemas.xmlsoap.org/soap/encoding/";
    public static final String URI_SOAP_TRANSPORT_HTTP = "http://schemas.xmlsoap.org/soap/http";
    public static final QName QNAME_ADDRESS = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "address");
    public static final QName QNAME_BINDING = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "binding");
    public static final QName QNAME_BODY = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "body");
    public static final QName QNAME_FAULT = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "fault");
    public static final QName QNAME_HEADER = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "header");
    public static final QName QNAME_HEADERFAULT = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "headerfault");
    public static final QName QNAME_OPERATION = new QName("http://schemas.xmlsoap.org/wsdl/soap/", "operation");
    public static final QName QNAME_TYPE_ARRAY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "Array");
    public static final QName QNAME_ATTR_GROUP_COMMON_ATTRIBUTES = new QName("http://schemas.xmlsoap.org/soap/encoding/", "commonAttributes");
    public static final QName QNAME_ATTR_ARRAY_TYPE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
    public static final QName QNAME_ATTR_OFFSET = new QName("http://schemas.xmlsoap.org/soap/encoding/", "offset");
    public static final QName QNAME_ATTR_POSITION = new QName("http://schemas.xmlsoap.org/soap/encoding/", "position");
    public static final QName QNAME_TYPE_BASE64 = new QName("http://schemas.xmlsoap.org/soap/encoding/", "base64");
    public static final QName QNAME_ELEMENT_STRING = new QName("http://schemas.xmlsoap.org/soap/encoding/", "string");
    public static final QName QNAME_ELEMENT_NORMALIZED_STRING = new QName("http://schemas.xmlsoap.org/soap/encoding/", "normalizedString");
    public static final QName QNAME_ELEMENT_TOKEN = new QName("http://schemas.xmlsoap.org/soap/encoding/", "token");
    public static final QName QNAME_ELEMENT_BYTE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "byte");
    public static final QName QNAME_ELEMENT_UNSIGNED_BYTE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "unsignedByte");
    public static final QName QNAME_ELEMENT_BASE64_BINARY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "base64Binary");
    public static final QName QNAME_ELEMENT_HEX_BINARY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "hexBinary");
    public static final QName QNAME_ELEMENT_INTEGER = new QName("http://schemas.xmlsoap.org/soap/encoding/", "integer");
    public static final QName QNAME_ELEMENT_POSITIVE_INTEGER = new QName("http://schemas.xmlsoap.org/soap/encoding/", "positiveInteger");
    public static final QName QNAME_ELEMENT_NEGATIVE_INTEGER = new QName("http://schemas.xmlsoap.org/soap/encoding/", "negativeInteger");
    public static final QName QNAME_ELEMENT_NON_NEGATIVE_INTEGER = new QName("http://schemas.xmlsoap.org/soap/encoding/", "nonNegativeInteger");
    public static final QName QNAME_ELEMENT_NON_POSITIVE_INTEGER = new QName("http://schemas.xmlsoap.org/soap/encoding/", "nonPositiveInteger");
    public static final QName QNAME_ELEMENT_INT = new QName("http://schemas.xmlsoap.org/soap/encoding/", "int");
    public static final QName QNAME_ELEMENT_UNSIGNED_INT = new QName("http://schemas.xmlsoap.org/soap/encoding/", "unsignedInt");
    public static final QName QNAME_ELEMENT_LONG = new QName("http://schemas.xmlsoap.org/soap/encoding/", "long");
    public static final QName QNAME_ELEMENT_UNSIGNED_LONG = new QName("http://schemas.xmlsoap.org/soap/encoding/", "unsignedLong");
    public static final QName QNAME_ELEMENT_SHORT = new QName("http://schemas.xmlsoap.org/soap/encoding/", "short");
    public static final QName QNAME_ELEMENT_UNSIGNED_SHORT = new QName("http://schemas.xmlsoap.org/soap/encoding/", "unsignedShort");
    public static final QName QNAME_ELEMENT_DECIMAL = new QName("http://schemas.xmlsoap.org/soap/encoding/", "decimal");
    public static final QName QNAME_ELEMENT_FLOAT = new QName("http://schemas.xmlsoap.org/soap/encoding/", "float");
    public static final QName QNAME_ELEMENT_DOUBLE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "double");
    public static final QName QNAME_ELEMENT_BOOLEAN = new QName("http://schemas.xmlsoap.org/soap/encoding/", "boolean");
    public static final QName QNAME_ELEMENT_TIME = new QName("http://schemas.xmlsoap.org/soap/encoding/", "time");
    public static final QName QNAME_ELEMENT_DATE_TIME = new QName("http://schemas.xmlsoap.org/soap/encoding/", "dateTime");
    public static final QName QNAME_ELEMENT_DURATION = new QName("http://schemas.xmlsoap.org/soap/encoding/", "duration");
    public static final QName QNAME_ELEMENT_DATE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "date");
    public static final QName QNAME_ELEMENT_G_MONTH = new QName("http://schemas.xmlsoap.org/soap/encoding/", "gMonth");
    public static final QName QNAME_ELEMENT_G_YEAR = new QName("http://schemas.xmlsoap.org/soap/encoding/", "gYear");
    public static final QName QNAME_ELEMENT_G_YEAR_MONTH = new QName("http://schemas.xmlsoap.org/soap/encoding/", "gYearMonth");
    public static final QName QNAME_ELEMENT_G_DAY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "gDay");
    public static final QName QNAME_ELEMENT_G_MONTH_DAY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "gMonthDay");
    public static final QName QNAME_ELEMENT_NAME = new QName("http://schemas.xmlsoap.org/soap/encoding/", "Name");
    public static final QName QNAME_ELEMENT_QNAME = new QName("http://schemas.xmlsoap.org/soap/encoding/", "QName");
    public static final QName QNAME_ELEMENT_NCNAME = new QName("http://schemas.xmlsoap.org/soap/encoding/", "NCName");
    public static final QName QNAME_ELEMENT_ANY_URI = new QName("http://schemas.xmlsoap.org/soap/encoding/", "anyURI");
    public static final QName QNAME_ELEMENT_ID = new QName("http://schemas.xmlsoap.org/soap/encoding/", "ID");
    public static final QName QNAME_ELEMENT_IDREF = new QName("http://schemas.xmlsoap.org/soap/encoding/", "IDREF");
    public static final QName QNAME_ELEMENT_IDREFS = new QName("http://schemas.xmlsoap.org/soap/encoding/", "IDREFS");
    public static final QName QNAME_ELEMENT_ENTITY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "ENTITY");
    public static final QName QNAME_ELEMENT_ENTITIES = new QName("http://schemas.xmlsoap.org/soap/encoding/", "ENTITIES");
    public static final QName QNAME_ELEMENT_NOTATION = new QName("http://schemas.xmlsoap.org/soap/encoding/", "NOTATION");
    public static final QName QNAME_ELEMENT_NMTOKEN = new QName("http://schemas.xmlsoap.org/soap/encoding/", "NMTOKEN");
    public static final QName QNAME_ELEMENT_NMTOKENS = new QName("http://schemas.xmlsoap.org/soap/encoding/", "NMTOKENS");
    public static final QName QNAME_TYPE_STRING = new QName("http://schemas.xmlsoap.org/soap/encoding/", "string");
    public static final QName QNAME_TYPE_NORMALIZED_STRING = new QName("http://schemas.xmlsoap.org/soap/encoding/", "normalizedString");
    public static final QName QNAME_TYPE_TOKEN = new QName("http://schemas.xmlsoap.org/soap/encoding/", "token");
    public static final QName QNAME_TYPE_BYTE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "byte");
    public static final QName QNAME_TYPE_UNSIGNED_BYTE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "unsignedByte");
    public static final QName QNAME_TYPE_BASE64_BINARY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "base64Binary");
    public static final QName QNAME_TYPE_HEX_BINARY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "hexBinary");
    public static final QName QNAME_TYPE_INTEGER = new QName("http://schemas.xmlsoap.org/soap/encoding/", "integer");
    public static final QName QNAME_TYPE_POSITIVE_INTEGER = new QName("http://schemas.xmlsoap.org/soap/encoding/", "positiveInteger");
    public static final QName QNAME_TYPE_NEGATIVE_INTEGER = new QName("http://schemas.xmlsoap.org/soap/encoding/", "negativeInteger");
    public static final QName QNAME_TYPE_NON_NEGATIVE_INTEGER = new QName("http://schemas.xmlsoap.org/soap/encoding/", "nonNegativeInteger");
    public static final QName QNAME_TYPE_NON_POSITIVE_INTEGER = new QName("http://schemas.xmlsoap.org/soap/encoding/", "nonPositiveInteger");
    public static final QName QNAME_TYPE_INT = new QName("http://schemas.xmlsoap.org/soap/encoding/", "int");
    public static final QName QNAME_TYPE_UNSIGNED_INT = new QName("http://schemas.xmlsoap.org/soap/encoding/", "unsignedInt");
    public static final QName QNAME_TYPE_LONG = new QName("http://schemas.xmlsoap.org/soap/encoding/", "long");
    public static final QName QNAME_TYPE_UNSIGNED_LONG = new QName("http://schemas.xmlsoap.org/soap/encoding/", "unsignedLong");
    public static final QName QNAME_TYPE_SHORT = new QName("http://schemas.xmlsoap.org/soap/encoding/", "short");
    public static final QName QNAME_TYPE_UNSIGNED_SHORT = new QName("http://schemas.xmlsoap.org/soap/encoding/", "unsignedShort");
    public static final QName QNAME_TYPE_DECIMAL = new QName("http://schemas.xmlsoap.org/soap/encoding/", "decimal");
    public static final QName QNAME_TYPE_FLOAT = new QName("http://schemas.xmlsoap.org/soap/encoding/", "float");
    public static final QName QNAME_TYPE_DOUBLE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "double");
    public static final QName QNAME_TYPE_BOOLEAN = new QName("http://schemas.xmlsoap.org/soap/encoding/", "boolean");
    public static final QName QNAME_TYPE_TIME = new QName("http://schemas.xmlsoap.org/soap/encoding/", "time");
    public static final QName QNAME_TYPE_DATE_TIME = new QName("http://schemas.xmlsoap.org/soap/encoding/", "dateTime");
    public static final QName QNAME_TYPE_DURATION = new QName("http://schemas.xmlsoap.org/soap/encoding/", "duration");
    public static final QName QNAME_TYPE_DATE = new QName("http://schemas.xmlsoap.org/soap/encoding/", "date");
    public static final QName QNAME_TYPE_G_MONTH = new QName("http://schemas.xmlsoap.org/soap/encoding/", "gMonth");
    public static final QName QNAME_TYPE_G_YEAR = new QName("http://schemas.xmlsoap.org/soap/encoding/", "gYear");
    public static final QName QNAME_TYPE_G_YEAR_MONTH = new QName("http://schemas.xmlsoap.org/soap/encoding/", "gYearMonth");
    public static final QName QNAME_TYPE_G_DAY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "gDay");
    public static final QName QNAME_TYPE_G_MONTH_DAY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "gMonthDay");
    public static final QName QNAME_TYPE_NAME = new QName("http://schemas.xmlsoap.org/soap/encoding/", "Name");
    public static final QName QNAME_TYPE_QNAME = new QName("http://schemas.xmlsoap.org/soap/encoding/", "QName");
    public static final QName QNAME_TYPE_NCNAME = new QName("http://schemas.xmlsoap.org/soap/encoding/", "NCName");
    public static final QName QNAME_TYPE_ANY_URI = new QName("http://schemas.xmlsoap.org/soap/encoding/", "anyURI");
    public static final QName QNAME_TYPE_ID = new QName("http://schemas.xmlsoap.org/soap/encoding/", "ID");
    public static final QName QNAME_TYPE_IDREF = new QName("http://schemas.xmlsoap.org/soap/encoding/", "IDREF");
    public static final QName QNAME_TYPE_IDREFS = new QName("http://schemas.xmlsoap.org/soap/encoding/", "IDREFS");
    public static final QName QNAME_TYPE_ENTITY = new QName("http://schemas.xmlsoap.org/soap/encoding/", "ENTITY");
    public static final QName QNAME_TYPE_ENTITIES = new QName("http://schemas.xmlsoap.org/soap/encoding/", "ENTITIES");
    public static final QName QNAME_TYPE_NOTATION = new QName("http://schemas.xmlsoap.org/soap/encoding/", "NOTATION");
    public static final QName QNAME_TYPE_NMTOKEN = new QName("http://schemas.xmlsoap.org/soap/encoding/", "NMTOKEN");
    public static final QName QNAME_TYPE_NMTOKENS = new QName("http://schemas.xmlsoap.org/soap/encoding/", "NMTOKENS");
    public static final QName QNAME_ATTR_ID = new QName("", "id");
    public static final QName QNAME_ATTR_HREF = new QName("", "href");

}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SchemaConstants.java

package com.sun.xml.rpc.wsdl.document.schema;

import javax.xml.rpc.namespace.QName;

public interface SchemaConstants {

    public static final String NS_XMLNS = "http://www.w3.org/2000/xmlns/";
    public static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
    public static final String NS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
    public static final QName QNAME_ALL = new QName("http://www.w3.org/2001/XMLSchema", "all");
    public static final QName QNAME_ANNOTATION = new QName("http://www.w3.org/2001/XMLSchema", "annotation");
    public static final QName QNAME_ANY = new QName("http://www.w3.org/2001/XMLSchema", "any");
    public static final QName QNAME_ANY_ATTRIBUTE = new QName("http://www.w3.org/2001/XMLSchema", "anyAttribute");
    public static final QName QNAME_ATTRIBUTE = new QName("http://www.w3.org/2001/XMLSchema", "attribute");
    public static final QName QNAME_ATTRIBUTE_GROUP = new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup");
    public static final QName QNAME_CHOICE = new QName("http://www.w3.org/2001/XMLSchema", "choice");
    public static final QName QNAME_COMPLEX_CONTENT = new QName("http://www.w3.org/2001/XMLSchema", "complexContent");
    public static final QName QNAME_COMPLEX_TYPE = new QName("http://www.w3.org/2001/XMLSchema", "complexType");
    public static final QName QNAME_ELEMENT = new QName("http://www.w3.org/2001/XMLSchema", "element");
    public static final QName QNAME_ENUMERATION = new QName("http://www.w3.org/2001/XMLSchema", "enumeration");
    public static final QName QNAME_EXTENSION = new QName("http://www.w3.org/2001/XMLSchema", "extension");
    public static final QName QNAME_FIELD = new QName("http://www.w3.org/2001/XMLSchema", "field");
    public static final QName QNAME_FRACTION_DIGITS = new QName("http://www.w3.org/2001/XMLSchema", "fractionDigits");
    public static final QName QNAME_GROUP = new QName("http://www.w3.org/2001/XMLSchema", "group");
    public static final QName QNAME_IMPORT = new QName("http://www.w3.org/2001/XMLSchema", "import");
    public static final QName QNAME_INCLUDE = new QName("http://www.w3.org/2001/XMLSchema", "include");
    public static final QName QNAME_KEY = new QName("http://www.w3.org/2001/XMLSchema", "key");
    public static final QName QNAME_KEYREF = new QName("http://www.w3.org/2001/XMLSchema", "keyref");
    public static final QName QNAME_LENGTH = new QName("http://www.w3.org/2001/XMLSchema", "length");
    public static final QName QNAME_LIST = new QName("http://www.w3.org/2001/XMLSchema", "list");
    public static final QName QNAME_MAX_EXCLUSIVE = new QName("http://www.w3.org/2001/XMLSchema", "maxExclusive");
    public static final QName QNAME_MAX_INCLUSIVE = new QName("http://www.w3.org/2001/XMLSchema", "maxInclusive");
    public static final QName QNAME_MAX_LENGTH = new QName("http://www.w3.org/2001/XMLSchema", "maxLength");
    public static final QName QNAME_MIN_EXCLUSIVE = new QName("http://www.w3.org/2001/XMLSchema", "minExclusive");
    public static final QName QNAME_MIN_INCLUSIVE = new QName("http://www.w3.org/2001/XMLSchema", "minInclusive");
    public static final QName QNAME_MIN_LENGTH = new QName("http://www.w3.org/2001/XMLSchema", "minLength");
    public static final QName QNAME_NOTATION = new QName("http://www.w3.org/2001/XMLSchema", "notation");
    public static final QName QNAME_RESTRICTION = new QName("http://www.w3.org/2001/XMLSchema", "restriction");
    public static final QName QNAME_PATTERN = new QName("http://www.w3.org/2001/XMLSchema", "pattern");
    public static final QName QNAME_PRECISION = new QName("http://www.w3.org/2001/XMLSchema", "precision");
    public static final QName QNAME_REDEFINE = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
    public static final QName QNAME_SCALE = new QName("http://www.w3.org/2001/XMLSchema", "scale");
    public static final QName QNAME_SCHEMA = new QName("http://www.w3.org/2001/XMLSchema", "schema");
    public static final QName QNAME_SELECTOR = new QName("http://www.w3.org/2001/XMLSchema", "selector");
    public static final QName QNAME_SEQUENCE = new QName("http://www.w3.org/2001/XMLSchema", "sequence");
    public static final QName QNAME_SIMPLE_CONTENT = new QName("http://www.w3.org/2001/XMLSchema", "simpleContent");
    public static final QName QNAME_SIMPLE_TYPE = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
    public static final QName QNAME_TOTAL_DIGITS = new QName("http://www.w3.org/2001/XMLSchema", "totalDigits");
    public static final QName QNAME_UNIQUE = new QName("http://www.w3.org/2001/XMLSchema", "unique");
    public static final QName QNAME_UNION = new QName("http://www.w3.org/2001/XMLSchema", "union");
    public static final QName QNAME_WHITE_SPACE = new QName("http://www.w3.org/2001/XMLSchema", "whiteSpace");
    public static final QName QNAME_TYPE_STRING = new QName("http://www.w3.org/2001/XMLSchema", "string");
    public static final QName QNAME_TYPE_NORMALIZED_STRING = new QName("http://www.w3.org/2001/XMLSchema", "normalizedString");
    public static final QName QNAME_TYPE_TOKEN = new QName("http://www.w3.org/2001/XMLSchema", "token");
    public static final QName QNAME_TYPE_BYTE = new QName("http://www.w3.org/2001/XMLSchema", "byte");
    public static final QName QNAME_TYPE_UNSIGNED_BYTE = new QName("http://www.w3.org/2001/XMLSchema", "unsignedByte");
    public static final QName QNAME_TYPE_BASE64_BINARY = new QName("http://www.w3.org/2001/XMLSchema", "base64Binary");
    public static final QName QNAME_TYPE_HEX_BINARY = new QName("http://www.w3.org/2001/XMLSchema", "hexBinary");
    public static final QName QNAME_TYPE_INTEGER = new QName("http://www.w3.org/2001/XMLSchema", "integer");
    public static final QName QNAME_TYPE_POSITIVE_INTEGER = new QName("http://www.w3.org/2001/XMLSchema", "positiveInteger");
    public static final QName QNAME_TYPE_NEGATIVE_INTEGER = new QName("http://www.w3.org/2001/XMLSchema", "negativeInteger");
    public static final QName QNAME_TYPE_NON_NEGATIVE_INTEGER = new QName("http://www.w3.org/2001/XMLSchema", "nonNegativeInteger");
    public static final QName QNAME_TYPE_NON_POSITIVE_INTEGER = new QName("http://www.w3.org/2001/XMLSchema", "nonPositiveInteger");
    public static final QName QNAME_TYPE_INT = new QName("http://www.w3.org/2001/XMLSchema", "int");
    public static final QName QNAME_TYPE_UNSIGNED_INT = new QName("http://www.w3.org/2001/XMLSchema", "unsignedInt");
    public static final QName QNAME_TYPE_LONG = new QName("http://www.w3.org/2001/XMLSchema", "long");
    public static final QName QNAME_TYPE_UNSIGNED_LONG = new QName("http://www.w3.org/2001/XMLSchema", "unsignedLong");
    public static final QName QNAME_TYPE_SHORT = new QName("http://www.w3.org/2001/XMLSchema", "short");
    public static final QName QNAME_TYPE_UNSIGNED_SHORT = new QName("http://www.w3.org/2001/XMLSchema", "unsignedShort");
    public static final QName QNAME_TYPE_DECIMAL = new QName("http://www.w3.org/2001/XMLSchema", "decimal");
    public static final QName QNAME_TYPE_FLOAT = new QName("http://www.w3.org/2001/XMLSchema", "float");
    public static final QName QNAME_TYPE_DOUBLE = new QName("http://www.w3.org/2001/XMLSchema", "double");
    public static final QName QNAME_TYPE_BOOLEAN = new QName("http://www.w3.org/2001/XMLSchema", "boolean");
    public static final QName QNAME_TYPE_TIME = new QName("http://www.w3.org/2001/XMLSchema", "time");
    public static final QName QNAME_TYPE_DATE_TIME = new QName("http://www.w3.org/2001/XMLSchema", "dateTime");
    public static final QName QNAME_TYPE_DURATION = new QName("http://www.w3.org/2001/XMLSchema", "duration");
    public static final QName QNAME_TYPE_DATE = new QName("http://www.w3.org/2001/XMLSchema", "date");
    public static final QName QNAME_TYPE_G_MONTH = new QName("http://www.w3.org/2001/XMLSchema", "gMonth");
    public static final QName QNAME_TYPE_G_YEAR = new QName("http://www.w3.org/2001/XMLSchema", "gYear");
    public static final QName QNAME_TYPE_G_YEAR_MONTH = new QName("http://www.w3.org/2001/XMLSchema", "gYearMonth");
    public static final QName QNAME_TYPE_G_DAY = new QName("http://www.w3.org/2001/XMLSchema", "gDay");
    public static final QName QNAME_TYPE_G_MONTH_DAY = new QName("http://www.w3.org/2001/XMLSchema", "gMonthDay");
    public static final QName QNAME_TYPE_NAME = new QName("http://www.w3.org/2001/XMLSchema", "Name");
    public static final QName QNAME_TYPE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "QName");
    public static final QName QNAME_TYPE_NCNAME = new QName("http://www.w3.org/2001/XMLSchema", "NCName");
    public static final QName QNAME_TYPE_ANY_URI = new QName("http://www.w3.org/2001/XMLSchema", "anyURI");
    public static final QName QNAME_TYPE_ID = new QName("http://www.w3.org/2001/XMLSchema", "ID");
    public static final QName QNAME_TYPE_IDREF = new QName("http://www.w3.org/2001/XMLSchema", "IDREF");
    public static final QName QNAME_TYPE_IDREFS = new QName("http://www.w3.org/2001/XMLSchema", "IDREFS");
    public static final QName QNAME_TYPE_ENTITY = new QName("http://www.w3.org/2001/XMLSchema", "ENTITY");
    public static final QName QNAME_TYPE_ENTITIES = new QName("http://www.w3.org/2001/XMLSchema", "ENTITIES");
    public static final QName QNAME_TYPE_NOTATION = new QName("http://www.w3.org/2001/XMLSchema", "NOTATION");
    public static final QName QNAME_TYPE_NMTOKEN = new QName("http://www.w3.org/2001/XMLSchema", "NMTOKEN");
    public static final QName QNAME_TYPE_NMTOKENS = new QName("http://www.w3.org/2001/XMLSchema", "NMTOKENS");
    public static final QName QNAME_TYPE_URTYPE = new QName("http://www.w3.org/2001/XMLSchema", "anyType");
    public static final QName QNAME_TYPE_SIMPLE_URTYPE = new QName("http://www.w3.org/2001/XMLSchema", "anySimpleType");

}

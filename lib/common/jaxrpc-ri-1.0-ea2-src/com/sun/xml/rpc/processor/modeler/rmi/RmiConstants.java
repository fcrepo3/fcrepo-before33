// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RmiConstants.java

package com.sun.xml.rpc.processor.modeler.rmi;

import com.sun.xml.rpc.encoding.AttachmentConstants;
import com.sun.xml.rpc.processor.model.soap.SOAPAnyType;
import com.sun.xml.rpc.processor.model.soap.SOAPSimpleType;
import com.sun.xml.rpc.processor.modeler.ModelerConstants;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import sun.tools.java.Identifier;

public interface RmiConstants
    extends ModelerConstants {

    public static final Identifier idRemote = Identifier.lookup("java.rmi.Remote");
    public static final Identifier idRemoteException = Identifier.lookup("java.rmi.RemoteException");
    public static final Identifier idSerializable = Identifier.lookup("java.io.Serializable");
    public static final SOAPSimpleType XSD_BOOLEAN_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.BOOLEAN, ModelerConstants.BOOLEAN_JAVATYPE, false);
    public static final SOAPSimpleType XSD_BOXED_BOOLEAN_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.BOOLEAN, ModelerConstants.BOXED_BOOLEAN_JAVATYPE, false);
    public static final SOAPSimpleType XSD_BYTE_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.BYTE, ModelerConstants.BYTE_JAVATYPE, false);
    public static final SOAPSimpleType XSD_BYTE_ARRAY_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.BASE64_BINARY, ModelerConstants.BYTE_ARRAY_JAVATYPE, false);
    public static final SOAPSimpleType XSD_BOXED_BYTE_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.BYTE, ModelerConstants.BOXED_BYTE_JAVATYPE, false);
    public static final SOAPSimpleType XSD_BOXED_BYTE_ARRAY_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.BASE64_BINARY, ModelerConstants.BOXED_BYTE_ARRAY_JAVATYPE, false);
    public static final SOAPSimpleType XSD_DOUBLE_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.DOUBLE, ModelerConstants.DOUBLE_JAVATYPE, false);
    public static final SOAPSimpleType XSD_BOXED_DOUBLE_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.DOUBLE, ModelerConstants.BOXED_DOUBLE_JAVATYPE, false);
    public static final SOAPSimpleType XSD_FLOAT_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.FLOAT, ModelerConstants.FLOAT_JAVATYPE, false);
    public static final SOAPSimpleType XSD_BOXED_FLOAT_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.FLOAT, ModelerConstants.BOXED_FLOAT_JAVATYPE, false);
    public static final SOAPSimpleType XSD_INT_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.INT, ModelerConstants.INT_JAVATYPE, false);
    public static final SOAPSimpleType XSD_BOXED_INTEGER_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.INT, ModelerConstants.BOXED_INTEGER_JAVATYPE, false);
    public static final SOAPSimpleType XSD_INTEGER_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.INTEGER, ModelerConstants.BIG_INTEGER_JAVATYPE, false);
    public static final SOAPSimpleType XSD_LONG_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.LONG, ModelerConstants.LONG_JAVATYPE, false);
    public static final SOAPSimpleType XSD_BOXED_LONG_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.LONG, ModelerConstants.BOXED_LONG_JAVATYPE, false);
    public static final SOAPSimpleType XSD_SHORT_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.SHORT, ModelerConstants.SHORT_JAVATYPE, false);
    public static final SOAPSimpleType XSD_BOXED_SHORT_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.SHORT, ModelerConstants.BOXED_SHORT_JAVATYPE, false);
    public static final SOAPSimpleType XSD_DECIMAL_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.DECIMAL, ModelerConstants.DECIMAL_JAVATYPE, false);
    public static final SOAPSimpleType XSD_DATE_TIME_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.DATE_TIME, ModelerConstants.DATE_JAVATYPE, false);
    public static final SOAPSimpleType XSD_DATE_TIME_CALENDAR_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.DATE_TIME, ModelerConstants.CALENDAR_JAVATYPE, false);
    public static final SOAPSimpleType XSD_STRING_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.STRING, ModelerConstants.STRING_JAVATYPE, false);
    public static final SOAPSimpleType XSD_QNAME_SOAPTYPE = new SOAPSimpleType(BuiltInTypes.QNAME, ModelerConstants.QNAME_JAVATYPE, false);
    public static final SOAPSimpleType XSD_VOID_SOAPTYPE = new SOAPSimpleType(null, ModelerConstants.VOID_JAVATYPE, false);
    public static final SOAPAnyType XSD_ANYTYPE_SOAPTYPE = new SOAPAnyType(SchemaConstants.QNAME_TYPE_URTYPE, ModelerConstants.OBJECT_JAVATYPE);
    public static final SOAPSimpleType SOAP_BOXED_BOOLEAN_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_BOOLEAN, ModelerConstants.BOXED_BOOLEAN_JAVATYPE);
    public static final SOAPSimpleType SOAP_BOXED_BYTE_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_BYTE, ModelerConstants.BOXED_BYTE_JAVATYPE);
    public static final SOAPSimpleType SOAP_BYTE_ARRAY_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_BASE64, ModelerConstants.BYTE_ARRAY_JAVATYPE);
    public static final SOAPSimpleType SOAP_BOXED_BYTE_ARRAY_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_BASE64, ModelerConstants.BOXED_BYTE_ARRAY_JAVATYPE);
    public static final SOAPSimpleType SOAP_BOXED_DOUBLE_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_DOUBLE, ModelerConstants.BOXED_DOUBLE_JAVATYPE);
    public static final SOAPSimpleType SOAP_BOXED_FLOAT_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_FLOAT, ModelerConstants.BOXED_FLOAT_JAVATYPE);
    public static final SOAPSimpleType SOAP_BOXED_INTEGER_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_INT, ModelerConstants.BOXED_INTEGER_JAVATYPE);
    public static final SOAPSimpleType SOAP_BOXED_LONG_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_LONG, ModelerConstants.BOXED_LONG_JAVATYPE);
    public static final SOAPSimpleType SOAP_BOXED_SHORT_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_SHORT, ModelerConstants.BOXED_SHORT_JAVATYPE);
    public static final SOAPSimpleType SOAP_DECIMAL_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_DECIMAL, ModelerConstants.DECIMAL_JAVATYPE);
    public static final SOAPSimpleType SOAP_DATE_TIME_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_DATE_TIME, ModelerConstants.DATE_JAVATYPE);
    public static final SOAPSimpleType SOAP_STRING_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_STRING, ModelerConstants.STRING_JAVATYPE);
    public static final SOAPSimpleType SOAP_QNAME_SOAPTYPE = new SOAPSimpleType(SOAPConstants.QNAME_TYPE_QNAME, ModelerConstants.QNAME_JAVATYPE);
    public static final SOAPSimpleType IMAGE_SOAPTYPE = new SOAPSimpleType(AttachmentConstants.QNAME_TYPE_IMAGE, ModelerConstants.IMAGE_JAVATYPE);
    public static final SOAPSimpleType MIME_MULTIPART_SOAPTYPE = new SOAPSimpleType(AttachmentConstants.QNAME_TYPE_MIME_MULTIPART, ModelerConstants.MIME_MULTIPART_JAVATYPE);
    public static final SOAPSimpleType SOURCE_SOAPTYPE = new SOAPSimpleType(AttachmentConstants.QNAME_TYPE_SOURCE, ModelerConstants.SOURCE_JAVATYPE);
    public static final SOAPSimpleType DATA_HANDLER_SOAPTYPE = new SOAPSimpleType(AttachmentConstants.QNAME_TYPE_DATA_HANDLER, ModelerConstants.DATA_HANDLER_JAVATYPE);

}

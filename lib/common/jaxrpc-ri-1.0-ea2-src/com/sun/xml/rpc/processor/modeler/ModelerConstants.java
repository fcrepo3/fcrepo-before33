// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ModelerConstants.java

package com.sun.xml.rpc.processor.modeler;

import com.sun.xml.rpc.encoding.AttachmentConstants;
import com.sun.xml.rpc.processor.model.java.JavaSimpleType;

public interface ModelerConstants
    extends AttachmentConstants {

    public static final String BRACKETS = "[]";
    public static final String FALSE_STR = "false";
    public static final String ZERO_STR = "0";
    public static final String NULL_STR = "null";
    public static final String BOOLEAN_CLASSNAME = Boolean.TYPE.getName();
    public static final String BOOLEAN_ARRAY_CLASSNAME = BOOLEAN_CLASSNAME + "[]";
    public static final String BOXED_BOOLEAN_CLASSNAME = (ModelerConstants$1.class$java$lang$Boolean != null ? ModelerConstants$1.class$java$lang$Boolean : (ModelerConstants$1.class$java$lang$Boolean = ModelerConstants$1._mthclass$("java.lang.Boolean"))).getName();
    public static final String BOXED_BOOLEAN_ARRAY_CLASSNAME = BOXED_BOOLEAN_CLASSNAME + "[]";
    public static final String BYTE_CLASSNAME = Byte.TYPE.getName();
    public static final String BYTE_ARRAY_CLASSNAME = BYTE_CLASSNAME + "[]";
    public static final String BYTE_ARRAY_ARRAY_CLASSNAME = BYTE_ARRAY_CLASSNAME + "[]";
    public static final String BOXED_BYTE_CLASSNAME = (ModelerConstants$1.class$java$lang$Byte != null ? ModelerConstants$1.class$java$lang$Byte : (ModelerConstants$1.class$java$lang$Byte = ModelerConstants$1._mthclass$("java.lang.Byte"))).getName();
    public static final String BOXED_BYTE_ARRAY_CLASSNAME = BOXED_BYTE_CLASSNAME + "[]";
    public static final String BOXED_BYTE_ARRAY_ARRAY_CLASSNAME = BOXED_BYTE_ARRAY_CLASSNAME + "[]";
    public static final String DOUBLE_CLASSNAME = Double.TYPE.getName();
    public static final String DOUBLE_ARRAY_CLASSNAME = DOUBLE_CLASSNAME + "[]";
    public static final String BOXED_DOUBLE_CLASSNAME = (ModelerConstants$1.class$java$lang$Double != null ? ModelerConstants$1.class$java$lang$Double : (ModelerConstants$1.class$java$lang$Double = ModelerConstants$1._mthclass$("java.lang.Double"))).getName();
    public static final String BOXED_DOUBLE_ARRAY_CLASSNAME = BOXED_DOUBLE_CLASSNAME + "[]";
    public static final String FLOAT_CLASSNAME = Float.TYPE.getName();
    public static final String FLOAT_ARRAY_CLASSNAME = FLOAT_CLASSNAME + "[]";
    public static final String BOXED_FLOAT_CLASSNAME = (ModelerConstants$1.class$java$lang$Float != null ? ModelerConstants$1.class$java$lang$Float : (ModelerConstants$1.class$java$lang$Float = ModelerConstants$1._mthclass$("java.lang.Float"))).getName();
    public static final String BOXED_FLOAT_ARRAY_CLASSNAME = BOXED_FLOAT_CLASSNAME + "[]";
    public static final String INT_CLASSNAME = Integer.TYPE.getName();
    public static final String INT_ARRAY_CLASSNAME = INT_CLASSNAME + "[]";
    public static final String BOXED_INTEGER_CLASSNAME = (ModelerConstants$1.class$java$lang$Integer != null ? ModelerConstants$1.class$java$lang$Integer : (ModelerConstants$1.class$java$lang$Integer = ModelerConstants$1._mthclass$("java.lang.Integer"))).getName();
    public static final String BOXED_INTEGER_ARRAY_CLASSNAME = BOXED_INTEGER_CLASSNAME + "[]";
    public static final String LONG_CLASSNAME = Long.TYPE.getName();
    public static final String LONG_ARRAY_CLASSNAME = LONG_CLASSNAME + "[]";
    public static final String BOXED_LONG_CLASSNAME = (ModelerConstants$1.class$java$lang$Long != null ? ModelerConstants$1.class$java$lang$Long : (ModelerConstants$1.class$java$lang$Long = ModelerConstants$1._mthclass$("java.lang.Long"))).getName();
    public static final String BOXED_LONG_ARRAY_CLASSNAME = BOXED_LONG_CLASSNAME + "[]";
    public static final String SHORT_CLASSNAME = Short.TYPE.getName();
    public static final String SHORT_ARRAY_CLASSNAME = SHORT_CLASSNAME + "[]";
    public static final String BOXED_SHORT_CLASSNAME = (ModelerConstants$1.class$java$lang$Short != null ? ModelerConstants$1.class$java$lang$Short : (ModelerConstants$1.class$java$lang$Short = ModelerConstants$1._mthclass$("java.lang.Short"))).getName();
    public static final String BOXED_SHORT_ARRAY_CLASSNAME = BOXED_SHORT_CLASSNAME + "[]";
    public static final String BIGDECIMAL_CLASSNAME = (ModelerConstants$1.class$java$math$BigDecimal != null ? ModelerConstants$1.class$java$math$BigDecimal : (ModelerConstants$1.class$java$math$BigDecimal = ModelerConstants$1._mthclass$("java.math.BigDecimal"))).getName();
    public static final String BIGDECIMAL_ARRAY_CLASSNAME = BIGDECIMAL_CLASSNAME + "[]";
    public static final String BIGINTEGER_CLASSNAME = (ModelerConstants$1.class$java$math$BigInteger != null ? ModelerConstants$1.class$java$math$BigInteger : (ModelerConstants$1.class$java$math$BigInteger = ModelerConstants$1._mthclass$("java.math.BigInteger"))).getName();
    public static final String BIGINTEGER_ARRAY_CLASSNAME = BIGINTEGER_CLASSNAME + "[]";
    public static final String CALENDAR_CLASSNAME = (ModelerConstants$1.class$java$util$Calendar != null ? ModelerConstants$1.class$java$util$Calendar : (ModelerConstants$1.class$java$util$Calendar = ModelerConstants$1._mthclass$("java.util.Calendar"))).getName();
    public static final String CALENDAR_ARRAY_CLASSNAME = CALENDAR_CLASSNAME + "[]";
    public static final String DATE_CLASSNAME = (ModelerConstants$1.class$java$util$Date != null ? ModelerConstants$1.class$java$util$Date : (ModelerConstants$1.class$java$util$Date = ModelerConstants$1._mthclass$("java.util.Date"))).getName();
    public static final String DATE_ARRAY_CLASSNAME = DATE_CLASSNAME + "[]";
    public static final String STRING_CLASSNAME = (ModelerConstants$1.class$java$lang$String != null ? ModelerConstants$1.class$java$lang$String : (ModelerConstants$1.class$java$lang$String = ModelerConstants$1._mthclass$("java.lang.String"))).getName();
    public static final String STRING_ARRAY_CLASSNAME = STRING_CLASSNAME + "[]";
    public static final String QNAME_CLASSNAME = (ModelerConstants$1.class$javax$xml$rpc$namespace$QName != null ? ModelerConstants$1.class$javax$xml$rpc$namespace$QName : (ModelerConstants$1.class$javax$xml$rpc$namespace$QName = ModelerConstants$1._mthclass$("javax.xml.rpc.namespace.QName"))).getName();
    public static final String QNAME_ARRAY_CLASSNAME = QNAME_CLASSNAME + "[]";
    public static final String VOID_CLASSNAME = Void.TYPE.getName();
    public static final String OBJECT_CLASSNAME = (ModelerConstants$1.class$java$lang$Object != null ? ModelerConstants$1.class$java$lang$Object : (ModelerConstants$1.class$java$lang$Object = ModelerConstants$1._mthclass$("java.lang.Object"))).getName();
    public static final String OBJECT_ARRAY_CLASSNAME = OBJECT_CLASSNAME + "[]";
    public static final String SOAPELEMENT_CLASSNAME = (ModelerConstants$1.class$javax$xml$soap$SOAPElement != null ? ModelerConstants$1.class$javax$xml$soap$SOAPElement : (ModelerConstants$1.class$javax$xml$soap$SOAPElement = ModelerConstants$1._mthclass$("javax.xml.soap.SOAPElement"))).getName();
    public static final String IMAGE_CLASSNAME = (ModelerConstants$1.class$java$awt$Image != null ? ModelerConstants$1.class$java$awt$Image : (ModelerConstants$1.class$java$awt$Image = ModelerConstants$1._mthclass$("java.awt.Image"))).getName();
    public static final String MIME_MULTIPART_CLASSNAME = (ModelerConstants$1.class$javax$mail$internet$MimeMultipart != null ? ModelerConstants$1.class$javax$mail$internet$MimeMultipart : (ModelerConstants$1.class$javax$mail$internet$MimeMultipart = ModelerConstants$1._mthclass$("javax.mail.internet.MimeMultipart"))).getName();
    public static final String SOURCE_CLASSNAME = (ModelerConstants$1.class$javax$xml$transform$Source != null ? ModelerConstants$1.class$javax$xml$transform$Source : (ModelerConstants$1.class$javax$xml$transform$Source = ModelerConstants$1._mthclass$("javax.xml.transform.Source"))).getName();
    public static final String DATA_HANDLER_CLASSNAME = (ModelerConstants$1.class$javax$activation$DataHandler != null ? ModelerConstants$1.class$javax$activation$DataHandler : (ModelerConstants$1.class$javax$activation$DataHandler = ModelerConstants$1._mthclass$("javax.activation.DataHandler"))).getName();
    public static final JavaSimpleType BOOLEAN_JAVATYPE = new JavaSimpleType(BOOLEAN_CLASSNAME, "false");
    public static final JavaSimpleType BOXED_BOOLEAN_JAVATYPE = new JavaSimpleType(BOXED_BOOLEAN_CLASSNAME, "null");
    public static final JavaSimpleType BYTE_JAVATYPE = new JavaSimpleType(BYTE_CLASSNAME, "0");
    public static final JavaSimpleType BYTE_ARRAY_JAVATYPE = new JavaSimpleType(BYTE_ARRAY_CLASSNAME, "null");
    public static final JavaSimpleType BOXED_BYTE_JAVATYPE = new JavaSimpleType(BOXED_BYTE_CLASSNAME, "null");
    public static final JavaSimpleType BOXED_BYTE_ARRAY_JAVATYPE = new JavaSimpleType(BOXED_BYTE_ARRAY_CLASSNAME, "null");
    public static final JavaSimpleType DOUBLE_JAVATYPE = new JavaSimpleType(DOUBLE_CLASSNAME, "0");
    public static final JavaSimpleType BOXED_DOUBLE_JAVATYPE = new JavaSimpleType(BOXED_DOUBLE_CLASSNAME, "null");
    public static final JavaSimpleType FLOAT_JAVATYPE = new JavaSimpleType(FLOAT_CLASSNAME, "0");
    public static final JavaSimpleType BOXED_FLOAT_JAVATYPE = new JavaSimpleType(BOXED_FLOAT_CLASSNAME, "null");
    public static final JavaSimpleType INT_JAVATYPE = new JavaSimpleType(INT_CLASSNAME, "0");
    public static final JavaSimpleType BOXED_INTEGER_JAVATYPE = new JavaSimpleType(BOXED_INTEGER_CLASSNAME, "null");
    public static final JavaSimpleType LONG_JAVATYPE = new JavaSimpleType(LONG_CLASSNAME, "0");
    public static final JavaSimpleType BOXED_LONG_JAVATYPE = new JavaSimpleType(BOXED_LONG_CLASSNAME, "null");
    public static final JavaSimpleType SHORT_JAVATYPE = new JavaSimpleType(SHORT_CLASSNAME, "0");
    public static final JavaSimpleType BOXED_SHORT_JAVATYPE = new JavaSimpleType(BOXED_SHORT_CLASSNAME, "null");
    public static final JavaSimpleType DECIMAL_JAVATYPE = new JavaSimpleType(BIGDECIMAL_CLASSNAME, "null");
    public static final JavaSimpleType BIG_INTEGER_JAVATYPE = new JavaSimpleType(BIGINTEGER_CLASSNAME, "null");
    public static final JavaSimpleType CALENDAR_JAVATYPE = new JavaSimpleType(CALENDAR_CLASSNAME, "null");
    public static final JavaSimpleType DATE_JAVATYPE = new JavaSimpleType(DATE_CLASSNAME, "null");
    public static final JavaSimpleType STRING_JAVATYPE = new JavaSimpleType(STRING_CLASSNAME, "null");
    public static final JavaSimpleType QNAME_JAVATYPE = new JavaSimpleType(QNAME_CLASSNAME, "null");
    public static final JavaSimpleType VOID_JAVATYPE = new JavaSimpleType(VOID_CLASSNAME, null);
    public static final JavaSimpleType OBJECT_JAVATYPE = new JavaSimpleType(OBJECT_CLASSNAME, null);
    public static final JavaSimpleType SOAPELEMENT_JAVATYPE = new JavaSimpleType(SOAPELEMENT_CLASSNAME, null);
    public static final JavaSimpleType IMAGE_JAVATYPE = new JavaSimpleType(IMAGE_CLASSNAME, null);
    public static final JavaSimpleType MIME_MULTIPART_JAVATYPE = new JavaSimpleType(MIME_MULTIPART_CLASSNAME, null);
    public static final JavaSimpleType SOURCE_JAVATYPE = new JavaSimpleType(SOURCE_CLASSNAME, null);
    public static final JavaSimpleType DATA_HANDLER_JAVATYPE = new JavaSimpleType(DATA_HANDLER_CLASSNAME, null);

}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   GeneratorConstants.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.modeler.ModelerConstants;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            Names

public interface GeneratorConstants
    extends ModelerConstants {

    public static final String ID_REMOTE_EXCEPTION = "java.rmi.RemoteException";
    public static final String ID_STUB_BASE = "com.sun.xml.rpc.client.StubBase";
    public static final String ID_TIE_BASE = "com.sun.xml.rpc.server.TieBase";
    public static final String BASE_SERIALIZER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$CombinedSerializer != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$CombinedSerializer : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$CombinedSerializer = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.CombinedSerializer")));
    public static final String REFERENCEABLE_SERIALIZER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$ReferenceableSerializerImpl != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$ReferenceableSerializerImpl : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$ReferenceableSerializerImpl = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.ReferenceableSerializerImpl")));
    public static final String DYNAMIC_SERIALIZER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$DynamicSerializer != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$DynamicSerializer : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$DynamicSerializer = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.DynamicSerializer")));
    public static final String SIMPLE_TYPE_SERIALIZER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$SimpleTypeSerializer != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$SimpleTypeSerializer : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$SimpleTypeSerializer = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.SimpleTypeSerializer")));
    public static final String SIMPLE_MULTI_TYPE_SERIALIZER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$SimpleMultiTypeSerializer != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$SimpleMultiTypeSerializer : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$SimpleMultiTypeSerializer = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.SimpleMultiTypeSerializer")));
    public static final String LITERAL_FRAGMENT_SERIALIZER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$literal$LiteralFragmentSerializer != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$literal$LiteralFragmentSerializer : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$literal$LiteralFragmentSerializer = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.literal.LiteralFragmentSerializer")));
    public static final String LITERAL_SIMPLE_TYPE_SERIALIZER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$literal$LiteralSimpleTypeSerializer != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$literal$LiteralSimpleTypeSerializer : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$literal$LiteralSimpleTypeSerializer = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.literal.LiteralSimpleTypeSerializer")));
    public static final String ATTACHMENT_SERIALIZER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$AttachmentSerializer != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$AttachmentSerializer : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$AttachmentSerializer = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.AttachmentSerializer")));
    public static final String XSD_BASE64_BINARY_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBase64BinaryEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBase64BinaryEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBase64BinaryEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDBase64BinaryEncoder")));
    public static final String XSD_BOOLEAN_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBooleanEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBooleanEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBooleanEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDBooleanEncoder")));
    public static final String XSD_BOXED_BASE64_BINARY_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBoxedBase64BinaryEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBoxedBase64BinaryEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBoxedBase64BinaryEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDBoxedBase64BinaryEncoder")));
    public static final String XSD_BOXED_HEX_BINARY_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBoxedHexBinaryEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBoxedHexBinaryEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDBoxedHexBinaryEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDBoxedHexBinaryEncoder")));
    public static final String XSD_BYTE_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDByteEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDByteEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDByteEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDByteEncoder")));
    public static final String XSD_DATE_TIME_CALENDAR_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDateTimeCalendarEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDateTimeCalendarEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDateTimeCalendarEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDDateTimeCalendarEncoder")));
    public static final String XSD_DATE_TIME_DATE_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDateTimeDateEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDateTimeDateEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDateTimeDateEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDDateTimeDateEncoder")));
    public static final String XSD_DECIMAL_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDecimalEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDecimalEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDecimalEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDDecimalEncoder")));
    public static final String XSD_DOUBLE_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDoubleEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDoubleEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDDoubleEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDDoubleEncoder")));
    public static final String XSD_FLOAT_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDFloatEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDFloatEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDFloatEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDFloatEncoder")));
    public static final String XSD_HEX_BINARY_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDHexBinaryEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDHexBinaryEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDHexBinaryEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDHexBinaryEncoder")));
    public static final String XSD_INT_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDIntEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDIntEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDIntEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDIntEncoder")));
    public static final String XSD_INTEGER_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDIntegerEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDIntegerEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDIntegerEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDIntegerEncoder")));
    public static final String XSD_LONG_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDLongEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDLongEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDLongEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDLongEncoder")));
    public static final String XSD_QNAME_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDQNameEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDQNameEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDQNameEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDQNameEncoder")));
    public static final String XSD_SHORT_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDShortEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDShortEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDShortEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDShortEncoder")));
    public static final String XSD_STRING_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDStringEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDStringEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$XSDStringEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.XSDStringEncoder")));
    public static final String IMAGE_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$ImageAttachmentEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$ImageAttachmentEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$ImageAttachmentEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.ImageAttachmentEncoder")));
    public static final String MIME_MULTIPART_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$MimeMultipartAttachmentEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$MimeMultipartAttachmentEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$MimeMultipartAttachmentEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.MimeMultipartAttachmentEncoder")));
    public static final String SOURCE_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$SourceAttachmentEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$SourceAttachmentEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$SourceAttachmentEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.SourceAttachmentEncoder")));
    public static final String DATA_HANDLER_ENCODER_NAME = Names.stripQualifier(GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$DataHandlerAttachmentEncoder != null ? GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$DataHandlerAttachmentEncoder : (GeneratorConstants$1.class$com$sun$xml$rpc$encoding$simpletype$DataHandlerAttachmentEncoder = GeneratorConstants$1._mthclass$("com.sun.xml.rpc.encoding.simpletype.DataHandlerAttachmentEncoder")));
    public static final String NULLABLE_STR = "NULLABLE";
    public static final String NOT_NULLABLE_STR = "NOT_NULLABLE";
    public static final String REFERENCEABLE_STR = "REFERENCEABLE";
    public static final String NOT_REFERENCEABLE_STR = "NOT_REFERENCEABLE";
    public static final String SERIALIZE_AS_REF_STR = "SERIALIZE_AS_REF";
    public static final String DONT_SERIALIZE_AS_REF_STR = "DONT_SERIALIZE_AS_REF";
    public static final String ENCODE_TYPE_STR = "ENCODE_TYPE";
    public static final String DONT_ENCODE_TYPE_STR = "DONT_ENCODE_TYPE";
    public static final String UNDERSCORE = "_";
    public static final String STUB_SUFFIX = "_Stub";
    public static final String TIE_SUFFIX = "_Tie";
    public static final String SKELETON_SUFFIX = "_Skeleton";
    public static final String SERVANT_SUFFIX = "_Impl";
    public static final String HOLDER_SUFFIX = "_Holder";
    public static final String JAVA_SRC_SUFFIX = ".java";
    public static final String SOAP_SERIALIZER_SUFFIX = "_SOAPSerializer";
    public static final String ARRAY_SOAP_SERIALIZER_SUFFIX = "Array_SOAPSerializer";
    public static final String LITERAL_SERIALIZER_SUFFIX = "_LiteralSerializer";
    public static final String ARRAY_LITERAL_SERIALIZER_SUFFIX = "Array_LiteralSerializer";
    public static final String SOAP_BUILDER_SUFFIX = "_SOAPBuilder";
    public static final String IMPL_SUFFIX = "_Impl";
    public static final String SERIALIZER_REGISTRY_SUFFIX = "_SerializerRegistry";
    public static final String ARRAY = "Array";
    public static final String MEMBER_PREFIX = "my";
    public static final String SERIALIZER_SUFFIX = "_Serializer";
    public static final String DESERIALIZER_SUFFIX = "_Deserializer";
    public static final String FAULT_SOAPSERIALIZER_SUFFIX = "_Fault_SOAPSerializer";
    public static final String OPCODE_SUFFIX = "_OPCODE";
    public static final String QNAME_SUFFIX = "_QNAME";
    public static final String TYPE_QNAME_SUFFIX = "_TYPE_QNAME";
    public static final String GET = "get";
    public static final String SET = "set";
    public static final String RESPONSE = "Response";
    public static final String NS_PREFIX = "ns";
    public static final String SERVICE_SUFFIX = "_Service";
    public static final String SERVICE_IMPL_SUFFIX = "_Service_Impl";

}

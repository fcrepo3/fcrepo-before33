// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ArraySerializerBase.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.soap.SOAPConstants;
import com.sun.xml.rpc.encoding.xsd.XSDConstants;
import com.sun.xml.rpc.streaming.*;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.lang.reflect.Array;
import java.util.Arrays;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            SerializerBase, SerializationException, DeserializationException, SerializerCallback, 
//            SOAPSerializationContext, SOAPDeserializationContext

public abstract class ArraySerializerBase extends SerializerBase {

    protected QName elemName;
    protected QName elemType;
    protected Class elemClass;
    protected int rank;
    protected int dims[];
    protected int null_dims[];

    protected ArraySerializerBase(QName type, boolean encodeType, boolean isNullable, String encodingStyle, QName elemName, QName elemType, Class elemClass, 
            int rank, int dims[]) {
        super(type, encodeType, isNullable, encodingStyle);
        this.rank = -1;
        if(elemType == null)
            throw new IllegalArgumentException();
        this.elemName = elemName;
        this.elemType = elemType;
        this.elemClass = elemClass;
        this.rank = rank;
        this.dims = dims;
        if(dims != null)
            null_dims = dims;
        else
        if(rank >= 0)
            null_dims = new int[rank];
        else
            null_dims = new int[0];
    }

    public void serialize(Object obj, QName name, SerializerCallback callback, XMLWriter writer, SOAPSerializationContext context) {
        boolean pushedEncodingStyle = false;
        try {
            if(obj == null) {
                if(!super.isNullable)
                    throw new SerializationException("soap.unexpectedNull");
                serializeNull(name, writer, context);
            } else {
                writer.startElement(name == null ? super.type : name);
                if(callback != null)
                    callback.onStartTag(obj, name, writer, context);
                pushedEncodingStyle = context.pushEncodingStyle(super.encodingStyle, writer);
                String attrVal;
                if(super.encodeType) {
                    attrVal = XMLWriterUtil.encodeQName(writer, SOAPConstants.QNAME_ENCODING_ARRAY);
                    writer.writeAttribute(XSDConstants.QNAME_XSI_TYPE, attrVal);
                }
                int dims[] = this.dims == null ? getArrayDimensions(obj) : this.dims;
                String encodedDims = encodeArrayDimensions(dims);
                attrVal = XMLWriterUtil.encodeQName(writer, elemType) + encodedDims;
                writer.writeAttribute(SOAPConstants.QNAME_ENCODING_ARRAYTYPE, attrVal);
                serializeArrayInstance(obj, dims, writer, context);
                writer.endElement();
            }
        }
        catch(JAXRPCExceptionBase e) {
            throw new SerializationException(e);
        }
        catch(Exception e) {
            throw new SerializationException(new LocalizableExceptionAdapter(e));
        }
        finally {
            if(pushedEncodingStyle)
                context.popEncodingStyle();
        }
    }

    protected void serializeNull(QName name, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        boolean pushedEncodingStyle = false;
        writer.startElement(name == null ? super.type : name);
        pushedEncodingStyle = context.pushEncodingStyle(super.encodingStyle, writer);
        String attrVal;
        if(super.encodeType) {
            attrVal = XMLWriterUtil.encodeQName(writer, super.type);
            writer.writeAttribute(XSDConstants.QNAME_XSI_TYPE, attrVal);
        }
        String encodedDims = encodeArrayDimensions(null_dims);
        attrVal = XMLWriterUtil.encodeQName(writer, elemType) + encodedDims;
        writer.writeAttribute(SOAPConstants.QNAME_ENCODING_ARRAYTYPE, attrVal);
        writer.writeAttribute(XSDConstants.QNAME_XSI_NIL, "1");
        writer.endElement();
        if(pushedEncodingStyle)
            context.popEncodingStyle();
    }

    protected abstract void serializeArrayInstance(Object obj, int ai[], XMLWriter xmlwriter, SOAPSerializationContext soapserializationcontext) throws Exception;

    public Object deserialize(QName name, XMLReader reader, SOAPDeserializationContext context) {
        boolean pushedEncodingStyle = false;
        try {
            pushedEncodingStyle = context.processEncodingStyle(reader);
            context.verifyEncodingStyle(super.encodingStyle);
            if(name != null)
                verifyName(reader, name);
            boolean isNull = SerializerBase.getNullStatus(reader);
            if(!isNull) {
                QName actualType = SerializerBase.getType(reader);
                if(actualType != null && !actualType.equals(super.type) && !actualType.equals(SOAPConstants.QNAME_ENCODING_ARRAY))
                    throw new DeserializationException("soap.unexpectedElementType", new Object[] {
                        super.type.toString(), actualType.toString()
                    });
                int dims[] = verifyArrayType(reader);
                Object rslt = deserializeArrayInstance(reader, context, dims);
                XMLReaderUtil.verifyReaderState(reader, 2);
                Object obj1 = rslt;
                return obj1;
            }
            if(!super.isNullable)
                throw new DeserializationException("soap.unexpectedNull");
            skipEmptyContent(reader);
            Object obj = null;
            return obj;
        }
        catch(JAXRPCExceptionBase e) {
            throw new DeserializationException(e);
        }
        catch(Exception e) {
            throw new DeserializationException(new LocalizableExceptionAdapter(e));
        }
        finally {
            if(pushedEncodingStyle)
                context.popEncodingStyle();
        }
    }

    protected abstract Object deserializeArrayInstance(XMLReader xmlreader, SOAPDeserializationContext soapdeserializationcontext, int ai[]) throws Exception;

    public static boolean isEmptyDimensions(int dims[]) {
        return dims.length == 0;
    }

    public static int[] getArrayElementPosition(XMLReader reader, int dims[]) throws Exception {
        int elemPos[] = null;
        Attributes attrs = reader.getAttributes();
        String attrVal = attrs.getValue("http://schemas.xmlsoap.org/soap/encoding/", "position");
        if(attrVal != null) {
            elemPos = decodeArrayDimensions(attrVal);
            if(isEmptyDimensions(dims) && elemPos.length != 1 || !isEmptyDimensions(dims) && elemPos.length != dims.length)
                throw new DeserializationException("soap.illegalArrayElementPosition", attrVal);
        }
        return elemPos;
    }

    public static int[] getArrayOffset(XMLReader reader, int dims[]) throws Exception {
        int offset[] = null;
        Attributes attrs = reader.getAttributes();
        String attrVal = attrs.getValue("http://schemas.xmlsoap.org/soap/encoding/", "offset");
        if(attrVal != null) {
            offset = decodeArrayDimensions(attrVal);
            if(isEmptyDimensions(dims) && offset.length != 1 || !isEmptyDimensions(dims) && offset.length != dims.length)
                throw new DeserializationException("soap.illegalArrayOffset", attrVal);
        }
        return offset;
    }

    protected int[] verifyArrayType(XMLReader reader) throws Exception {
        String arrayType = null;
        Attributes attrs = reader.getAttributes();
        arrayType = attrs.getValue("http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
        if(arrayType == null) {
            throw new DeserializationException("soap.malformedArrayType", "<arrayType attribute missing>");
        } else {
            verifyArrayElementType(arrayType, reader);
            return verifyArrayDimensions(arrayType, reader);
        }
    }

    protected void verifyArrayElementType(String arrayType, XMLReader reader) throws Exception {
        QName actualElemType = getArrayElementType(arrayType, reader);
        if(!actualElemType.equals(elemType))
            throw new DeserializationException("soap.unexpectedArrayElementType", new Object[] {
                elemType.toString(), actualElemType.toString()
            });
        else
            return;
    }

    public static QName getArrayElementType(String arrayType, XMLReader reader) throws Exception {
        QName elemType = null;
        boolean malformed = true;
        int idx = arrayType.indexOf('[');
        if(idx >= 0) {
            String elemTypeStr = arrayType.substring(0, idx).trim();
            if(elemTypeStr.length() > 0) {
                elemType = XMLReaderUtil.decodeQName(reader, elemTypeStr);
                malformed = false;
            }
        }
        if(malformed)
            throw new DeserializationException("soap.malformedArrayType", arrayType);
        else
            return elemType;
    }

    protected int[] verifyArrayDimensions(String arrayType, XMLReader reader) throws Exception {
        int actualDims[] = getArrayDimensions(arrayType, reader);
        if(rank >= 0 && (isEmptyDimensions(actualDims) && rank != 1 || !isEmptyDimensions(actualDims) && actualDims.length != rank))
            throw new DeserializationException("soap.unexpectedArrayRank", new Object[] {
                Integer.toString(rank), Integer.toString(actualDims.length), arrayType
            });
        if(dims != null && actualDims.length > 0 && !Arrays.equals(dims, actualDims))
            throw new DeserializationException("soap.unexpectedArrayDimensions", new Object[] {
                encodeArrayDimensions(dims), encodeArrayDimensions(actualDims), arrayType
            });
        else
            return actualDims;
    }

    public static int[] getArrayDimensions(String arrayType, XMLReader reader) throws Exception {
        int startIdx = arrayType.lastIndexOf('[');
        int endIdx = arrayType.lastIndexOf(']');
        if(startIdx < 0 || endIdx < 0 || startIdx > endIdx) {
            throw new DeserializationException("soap.malformedArrayType", arrayType);
        } else {
            String dimStr = arrayType.substring(startIdx, endIdx + 1);
            return decodeArrayDimensions(dimStr);
        }
    }

    protected int getArrayRank(Object obj) {
        int rank = 0;
        for(Class type = obj.getClass(); type != elemClass; type = type.getComponentType())
            rank++;

        return rank;
    }

    protected int[] getArrayDimensions(Object obj) {
        int rank = this.rank < 0 ? getArrayRank(obj) : this.rank;
        return getArrayDimensions(obj, rank);
    }

    public static int[] getArrayDimensions(Object obj, int rank) {
        int dims[] = new int[rank];
        Object arr = obj;
        for(int i = 0; i < rank; i++) {
            dims[i] = Array.getLength(arr);
            if(dims[i] == 0)
                break;
            arr = Array.get(arr, 0);
        }

        return dims;
    }

    public static int[] decodeArrayDimensions(String dimStr) throws Exception {
        String str = dimStr.trim();
        if(str.charAt(0) != '[' || str.charAt(str.length() - 1) != ']')
            throw new DeserializationException("soap.malformedArrayDimensions", dimStr);
        str = str.substring(1, str.length() - 1).trim();
        int strLen = str.length();
        int dimCount = 0;
        if(strLen > 0) {
            dimCount++;
            for(int commaIdx = -1; (commaIdx = str.indexOf(',', commaIdx + 1)) >= 0;)
                dimCount++;

        }
        int dims[] = new int[dimCount];
        int idx = 0;
        for(int i = 0; i < dimCount; i++) {
            while(idx < strLen && Character.isWhitespace(str.charAt(idx))) 
                idx++;
            int startIdx = idx;
            int dim = 0;
            char c1;
            for(; idx < strLen && Character.isDigit(c1 = str.charAt(idx)); idx++)
                dim = dim * 10 + (c1 - 48);

            if(idx > startIdx)
                dims[i] = dim;
            else
                throw new DeserializationException("soap.malformedArrayDimensions", dimStr);
            for(; idx < strLen && Character.isWhitespace(str.charAt(idx)); idx++);
            if(i < dimCount - 1) {
                if(idx >= strLen || str.charAt(idx) != ',')
                    throw new DeserializationException("soap.malformedArrayDimensions", dimStr);
                idx++;
            } else
            if(idx != strLen)
                throw new DeserializationException("soap.malformedArrayDimensions", dimStr);
        }

        return dims;
    }

    public static String encodeArrayDimensions(int dims[]) throws Exception {
        StringBuffer buf = new StringBuffer("[");
        for(int i = 0; i < dims.length; i++) {
            if(i > 0)
                buf.append(',');
            buf.append(dims[i]);
        }

        buf.append(']');
        return buf.toString();
    }

    public static boolean isPositionWithinBounds(int position[], int dims[]) {
        for(int i = 0; i < position.length; i++)
            if(position[i] >= dims[i])
                return false;

        return true;
    }

    public static void incrementPosition(int position[], int dims[]) throws Exception {
        for(int i = position.length - 1; i >= 0; i--) {
            if(++position[i] < dims[i])
                break;
            if(i == 0)
                throw new DeserializationException("soap.outOfBoundsArrayElementPosition", encodeArrayDimensions(position));
            position[i] = 0;
        }

    }

    public static int[] getDimensionOffsets(int dims[]) {
        int dimOffsets[] = null;
        if(isEmptyDimensions(dims)) {
            dimOffsets = (new int[] {
                1
            });
        } else {
            dimOffsets = new int[dims.length];
            dimOffsets[dimOffsets.length - 1] = 1;
            for(int i = dimOffsets.length - 2; i >= 0; i--)
                dimOffsets[i] = dims[i + 1] * dimOffsets[i + 1];

        }
        return dimOffsets;
    }

    public static int indexFromPosition(int position[], int dimOffsets[]) {
        int index = 0;
        for(int i = 0; i < position.length; i++)
            index += position[i] * dimOffsets[i];

        return index;
    }

    public static int[] positionFromIndex(int index, int dimOffsets[]) {
        int position[] = new int[dimOffsets.length];
        int tmp = index;
        for(int i = 0; i < position.length; i++) {
            position[i] = tmp / dimOffsets[i];
            tmp %= dimOffsets[i];
        }

        return position;
    }
}

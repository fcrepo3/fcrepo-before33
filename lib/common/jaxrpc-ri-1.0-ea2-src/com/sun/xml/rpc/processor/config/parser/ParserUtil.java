// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ParserUtil.java

package com.sun.xml.rpc.processor.config.parser;

import com.sun.xml.rpc.processor.config.ConfigurationException;
import com.sun.xml.rpc.streaming.Attributes;
import com.sun.xml.rpc.streaming.XMLReader;

public class ParserUtil {

    public ParserUtil() {
    }

    public static String getAttribute(XMLReader reader, String name) {
        Attributes attributes = reader.getAttributes();
        return attributes.getValue(name);
    }

    public static String getMandatoryAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        if(value == null)
            failWithLocalName("configuration.missing.attribute", reader, name);
        return value;
    }

    public static String getMandatoryNonEmptyAttribute(XMLReader reader, String name) {
        String value = getAttribute(reader, name);
        if(value == null)
            failWithLocalName("configuration.missing.attribute", reader, name);
        else
        if(value.equals(""))
            failWithLocalName("configuration.invalidAttributeValue", reader, name);
        return value;
    }

    public static void ensureNoContent(XMLReader reader) {
        if(reader.nextElementContent() != 2)
            fail("configuration.unexpectedContent", reader);
    }

    public static void fail(String key, XMLReader reader) {
        throw new ConfigurationException(key, Integer.toString(reader.getLineNumber()));
    }

    public static void failWithLocalName(String key, XMLReader reader) {
        throw new ConfigurationException(key, new Object[] {
            Integer.toString(reader.getLineNumber()), reader.getLocalName()
        });
    }

    public static void failWithLocalName(String key, XMLReader reader, String arg) {
        throw new ConfigurationException(key, new Object[] {
            Integer.toString(reader.getLineNumber()), reader.getLocalName(), arg
        });
    }
}

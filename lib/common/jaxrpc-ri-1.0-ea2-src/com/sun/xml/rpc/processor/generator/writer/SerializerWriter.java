// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SerializerWriter.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.util.IndentingWriter;
import java.io.IOException;

public interface SerializerWriter {

    public abstract void createSerializer(IndentingWriter indentingwriter, StringBuffer stringbuffer, String s, boolean flag, boolean flag1, String s1) throws IOException;

    public abstract void registerSerializer(IndentingWriter indentingwriter, boolean flag, boolean flag1, String s) throws IOException;

    public abstract void declareSerializer(IndentingWriter indentingwriter, boolean flag, boolean flag1) throws IOException;

    public abstract void initializeSerializer(IndentingWriter indentingwriter, String s, String s1) throws IOException;

    public abstract String serializerName();

    public abstract String serializerMemberName();

    public abstract String deserializerName();

    public abstract String deserializerMemberName();
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralSerializerWriterBase.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.model.AbstractType;

// Referenced classes of package com.sun.xml.rpc.processor.generator.writer:
//            SerializerWriterBase

public abstract class LiteralSerializerWriterBase extends SerializerWriterBase {

    public LiteralSerializerWriterBase(AbstractType type) {
        super(type);
    }

    protected String getEncodingStyleString() {
        return "\"\"";
    }
}

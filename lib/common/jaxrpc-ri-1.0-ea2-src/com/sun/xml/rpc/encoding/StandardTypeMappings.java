// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StandardTypeMappings.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.encoding.soap.StandardSOAPTypeMappings;
import com.sun.xml.rpc.util.exception.JAXRPCExceptionBase;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import javax.xml.rpc.JAXRPCException;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            TypeMappingImpl, TypeMappingException, ExtendedTypeMapping

public class StandardTypeMappings {

    private static final ExtendedTypeMapping SOAP;
    private static final ExtendedTypeMapping LITERAL;
    private static final JAXRPCException staticInitializationException;

    public StandardTypeMappings() {
    }

    public static ExtendedTypeMapping getSoap() {
        if(staticInitializationException != null)
            throw staticInitializationException;
        else
            return SOAP;
    }

    public static ExtendedTypeMapping getLiteral() {
        if(staticInitializationException != null)
            throw staticInitializationException;
        else
            return LITERAL;
    }

    static  {
        JAXRPCException staticInitializationExceptionTmp = null;
        ExtendedTypeMapping SOAPTmp = null;
        ExtendedTypeMapping LITERALTmp = null;
        try {
            SOAPTmp = new StandardSOAPTypeMappings();
            LITERALTmp = new TypeMappingImpl();
        }
        catch(JAXRPCExceptionBase e) {
            staticInitializationExceptionTmp = new TypeMappingException("typemapping.nested.exception.static.initialization", e);
        }
        catch(Exception e) {
            staticInitializationExceptionTmp = new TypeMappingException("typemapping.nested.exception.static.initialization", new LocalizableExceptionAdapter(e));
        }
        staticInitializationException = staticInitializationExceptionTmp;
        SOAP = SOAPTmp;
        LITERAL = LITERALTmp;
    }
}

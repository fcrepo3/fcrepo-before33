// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JAXRPCExceptionBase.java

package com.sun.xml.rpc.util.exception;

import com.sun.xml.rpc.util.localization.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.rpc.JAXRPCException;

// Referenced classes of package com.sun.xml.rpc.util.exception:
//            NestableExceptionSupport

public abstract class JAXRPCExceptionBase extends JAXRPCException
    implements Localizable {

    protected LocalizableSupport localizablePart;
    protected NestableExceptionSupport nestablePart;

    public JAXRPCExceptionBase() {
        nestablePart = new NestableExceptionSupport();
    }

    public JAXRPCExceptionBase(String key) {
        this();
        localizablePart = new LocalizableSupport(key);
    }

    public JAXRPCExceptionBase(String key, String arg) {
        this();
        localizablePart = new LocalizableSupport(key, arg);
    }

    public JAXRPCExceptionBase(String key, Localizable localizable) {
        this(key, new Object[] {
            localizable
        });
    }

    protected JAXRPCExceptionBase(String key, Object args[]) {
        this();
        localizablePart = new LocalizableSupport(key, args);
        if(args != null && nestablePart.getCause() == null) {
            for(int i = 0; i < args.length; i++) {
                if(!(args[i] instanceof Throwable))
                    continue;
                nestablePart.setCause((Throwable)args[i]);
                break;
            }

        }
    }

    public String getKey() {
        return localizablePart.getKey();
    }

    public Object[] getArguments() {
        return localizablePart.getArguments();
    }

    public abstract String getResourceBundleName();

    public String toString() {
        return getMessage();
    }

    public String getMessage() {
        Localizer localizer = new Localizer();
        return localizer.localize(this);
    }

    public Throwable getLinkedException() {
        return nestablePart.getCause();
    }

    public void printStackTrace() {
        super.printStackTrace();
        nestablePart.printStackTrace();
    }

    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
        nestablePart.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter s) {
        super.printStackTrace(s);
        nestablePart.printStackTrace(s);
    }
}

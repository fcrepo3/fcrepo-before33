// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServiceExceptionImpl.java

package com.sun.xml.rpc.client;

import com.sun.xml.rpc.util.exception.NestableExceptionSupport;
import com.sun.xml.rpc.util.localization.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.rpc.ServiceException;

public class ServiceExceptionImpl extends ServiceException
    implements Localizable {

    protected LocalizableSupport localizablePart;
    protected NestableExceptionSupport nestablePart;

    public ServiceExceptionImpl() {
        nestablePart = new NestableExceptionSupport();
    }

    public ServiceExceptionImpl(String key) {
        this();
        localizablePart = new LocalizableSupport(key);
    }

    public ServiceExceptionImpl(String key, String arg) {
        this();
        localizablePart = new LocalizableSupport(key, arg);
    }

    public ServiceExceptionImpl(String key, Localizable localizable) {
        this(key, new Object[] {
            localizable
        });
    }

    protected ServiceExceptionImpl(String key, Object args[]) {
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

    public ServiceExceptionImpl(Localizable arg) {
        this("service.exception.nested", arg);
    }

    public String getResourceBundleName() {
        return "com.sun.xml.rpc.resources.dii";
    }

    public String getKey() {
        return localizablePart.getKey();
    }

    public Object[] getArguments() {
        return localizablePart.getArguments();
    }

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

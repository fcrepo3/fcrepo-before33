// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LocalizableExceptionAdapter.java

package com.sun.xml.rpc.util.exception;

import com.sun.xml.rpc.util.localization.*;
import java.io.PrintStream;
import java.io.PrintWriter;

public class LocalizableExceptionAdapter extends Exception
    implements Localizable {

    protected Localizable localizablePart;
    protected Throwable nestedException;

    public LocalizableExceptionAdapter(Throwable nestedException) {
        this.nestedException = nestedException;
        if(nestedException instanceof Localizable)
            localizablePart = (Localizable)nestedException;
        else
            localizablePart = new NullLocalizable(nestedException.toString());
    }

    public String getKey() {
        return localizablePart.getKey();
    }

    public Object[] getArguments() {
        return localizablePart.getArguments();
    }

    public String getResourceBundleName() {
        return localizablePart.getResourceBundleName();
    }

    public String toString() {
        return nestedException.toString();
    }

    public String getLocalizedMessage() {
        if(nestedException == localizablePart) {
            Localizer localizer = new Localizer();
            return localizer.localize(localizablePart);
        } else {
            return nestedException.getLocalizedMessage();
        }
    }

    public String getMessage() {
        return getLocalizedMessage();
    }

    public void printStackTrace() {
        nestedException.printStackTrace();
    }

    public void printStackTrace(PrintStream s) {
        nestedException.printStackTrace(s);
    }

    public void printStackTrace(PrintWriter s) {
        nestedException.printStackTrace(s);
    }
}

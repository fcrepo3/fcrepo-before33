// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NestableExceptionSupport.java

package com.sun.xml.rpc.util.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class NestableExceptionSupport {

    protected Throwable cause;

    public NestableExceptionSupport() {
        cause = null;
    }

    public NestableExceptionSupport(Throwable cause) {
        this.cause = null;
        this.cause = cause;
    }

    public void printStackTrace() {
        if(cause != null) {
            System.err.println("\nCAUSE:\n");
            cause.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream s) {
        if(cause != null) {
            s.println("\nCAUSE:\n");
            cause.printStackTrace(s);
        }
    }

    public void printStackTrace(PrintWriter s) {
        if(cause != null) {
            s.println("\nCAUSE:\n");
            cause.printStackTrace(s);
        }
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }
}

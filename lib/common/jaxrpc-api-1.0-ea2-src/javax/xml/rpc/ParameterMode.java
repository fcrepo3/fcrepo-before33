// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ParameterMode.java

package javax.xml.rpc;


public class ParameterMode {

    private final String mode;
    public static final ParameterMode PARAM_MODE_IN = new ParameterMode("PARAM_MODE_IN");
    public static final ParameterMode PARAM_MODE_OUT = new ParameterMode("PARAM_MODE_OUT");
    public static final ParameterMode PARAM_MODE_INOUT = new ParameterMode("PARAM_MODE_INOUT");

    private ParameterMode(String mode) {
        this.mode = mode;
    }

    public String toString() {
        return mode;
    }

}

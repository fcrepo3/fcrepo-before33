// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   WSDLModelInfo.java

package com.sun.xml.rpc.processor.config;

import com.sun.xml.rpc.processor.modeler.Modeler;
import com.sun.xml.rpc.processor.modeler.wsdl.WSDLModeler;
import java.util.Properties;

// Referenced classes of package com.sun.xml.rpc.processor.config:
//            ModelInfo

public class WSDLModelInfo extends ModelInfo {

    private String _location;
    private String _javaPackageName;

    public WSDLModelInfo() {
    }

    protected Modeler getModeler(Properties options) {
        return new WSDLModeler(this, options);
    }

    public String getLocation() {
        return _location;
    }

    public void setLocation(String s) {
        _location = s;
    }

    public String getJavaPackageName() {
        return _javaPackageName;
    }

    public void setJavaPackageName(String s) {
        _javaPackageName = s;
    }
}

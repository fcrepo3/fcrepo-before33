// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Configuration.java

package com.sun.xml.rpc.processor.config;

import com.sun.xml.rpc.processor.util.BatchEnvironment;

// Referenced classes of package com.sun.xml.rpc.processor.config:
//            ModelInfo

public class Configuration {

    private BatchEnvironment _env;
    private ModelInfo _modelInfo;

    public Configuration(BatchEnvironment env) {
        _env = env;
    }

    public ModelInfo getModelInfo() {
        return _modelInfo;
    }

    public void setModelInfo(ModelInfo i) {
        _modelInfo = i;
        i.setParent(this);
    }

    public BatchEnvironment getEnvironment() {
        return _env;
    }
}

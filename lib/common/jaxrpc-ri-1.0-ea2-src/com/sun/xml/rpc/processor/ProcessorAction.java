// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ProcessorAction.java

package com.sun.xml.rpc.processor;

import com.sun.xml.rpc.processor.config.Configuration;
import com.sun.xml.rpc.processor.model.Model;
import java.util.Properties;

public interface ProcessorAction {

    public abstract void perform(Model model, Configuration configuration, Properties properties);
}

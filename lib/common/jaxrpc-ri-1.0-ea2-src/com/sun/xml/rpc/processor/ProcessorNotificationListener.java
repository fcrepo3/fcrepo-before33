// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ProcessorNotificationListener.java

package com.sun.xml.rpc.processor;

import com.sun.xml.rpc.util.localization.Localizable;

public interface ProcessorNotificationListener {

    public abstract void onError(Localizable localizable);

    public abstract void onWarning(Localizable localizable);

    public abstract void onInfo(Localizable localizable);
}

// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ServletConfigGenerator.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.model.Operation;
import java.util.Comparator;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            ServletConfigGenerator

class ServletConfigGenerator$StringLenComparator
    implements Comparator {

    private ServletConfigGenerator$StringLenComparator() {
    }

    public int compare(Object o1, Object o2) {
        int len1 = ((Operation)o1).getName().getLocalPart().length();
        int len2 = ((Operation)o2).getName().getLocalPart().length();
        return len1 > len2 ? 1 : -1;
    }

    ServletConfigGenerator$StringLenComparator(ServletConfigGenerator$1 x0) {
        this();
    }
}

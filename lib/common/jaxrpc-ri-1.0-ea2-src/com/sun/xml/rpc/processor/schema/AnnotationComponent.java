// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   AnnotationComponent.java

package com.sun.xml.rpc.processor.schema;

import com.sun.xml.rpc.util.NullIterator;
import com.sun.xml.rpc.wsdl.document.schema.SchemaAttribute;
import com.sun.xml.rpc.wsdl.document.schema.SchemaElement;
import java.util.*;

// Referenced classes of package com.sun.xml.rpc.processor.schema:
//            Component, ComponentVisitor

public class AnnotationComponent extends Component {

    private List _applicationInformationElements;
    private List _userInformationElements;
    private List _attributes;

    public AnnotationComponent() {
    }

    public void addApplicationInformation(SchemaElement element) {
        if(_applicationInformationElements == null)
            _applicationInformationElements = new ArrayList();
        _applicationInformationElements.add(element);
    }

    public void addUserInformation(SchemaElement element) {
        if(_userInformationElements == null)
            _userInformationElements = new ArrayList();
        _userInformationElements.add(element);
    }

    public Iterator attributes() {
        if(_attributes == null)
            return new NullIterator();
        else
            return _attributes.iterator();
    }

    public void addAttribute(SchemaAttribute attribute) {
        if(_attributes == null)
            _attributes = new ArrayList();
        _attributes.add(attribute);
    }

    public void accept(ComponentVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}

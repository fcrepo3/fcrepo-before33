// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ComponentWriter.java

package com.sun.xml.rpc.processor.util;

import com.sun.xml.rpc.processor.schema.*;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.util:
//            IndentingWriter

public class ComponentWriter
    implements ComponentVisitor {

    private IndentingWriter _writer;

    public ComponentWriter(IndentingWriter w) {
        _writer = w;
    }

    public void visit(AnnotationComponent annotationcomponent) throws Exception {
    }

    public void visit(AttributeDeclarationComponent component) throws Exception {
        _writer.p("ATTRIBUTE ");
        writeName(component.getName());
        _writer.pln();
        _writer.pI();
        if(component.getScope() == null)
            _writer.pln("SCOPE global");
        if(component.getTypeDefinition() != null) {
            _writer.pln("TYPE");
            _writer.pI();
            component.getTypeDefinition().accept(this);
            _writer.pO();
        }
        _writer.pO();
    }

    public void visit(AttributeGroupDefinitionComponent attributegroupdefinitioncomponent) throws Exception {
    }

    public void visit(AttributeUseComponent component) throws Exception {
        _writer.p("ATTRIBUTE USE ");
        _writer.pln(component.isRequired() ? "required" : "optional");
        _writer.pI();
        component.getAttributeDeclaration().accept(this);
        _writer.pO();
    }

    public void visit(ComplexTypeDefinitionComponent component) throws Exception {
        _writer.p("COMPLEX-TYPE ");
        writeName(component.getName());
        _writer.pln();
        if(component.getName() != null && component.getName().equals(SchemaConstants.QNAME_TYPE_URTYPE))
            return;
        _writer.pI();
        if(component.getBaseTypeDefinition() != null) {
            _writer.pln("BASE-TYPE");
            _writer.pI();
            component.getBaseTypeDefinition().accept(this);
            _writer.pO();
        }
        for(Iterator iter = component.attributeUses(); iter.hasNext(); ((AttributeUseComponent)iter.next()).accept(this));
        switch(component.getContentTag()) {
        case 1: // '\001'
            _writer.pln("EMPTY");
            break;

        case 2: // '\002'
            _writer.pln("SIMPLE");
            component.getSimpleTypeContent().accept(this);
            break;

        case 3: // '\003'
            _writer.pln("MIXED");
            component.getParticleContent().accept(this);
            break;

        case 4: // '\004'
            _writer.pln("ELEMENT-ONLY");
            component.getParticleContent().accept(this);
            break;
        }
        _writer.pO();
    }

    public void visit(ElementDeclarationComponent component) throws Exception {
        _writer.p("ELEMENT ");
        writeName(component.getName());
        _writer.pln();
        _writer.pI();
        if(component.getScope() == null)
            _writer.pln("SCOPE global");
        if(component.getTypeDefinition() != null)
            component.getTypeDefinition().accept(this);
        _writer.pO();
    }

    public void visit(IdentityConstraintDefinitionComponent identityconstraintdefinitioncomponent) throws Exception {
    }

    public void visit(ModelGroupComponent component) throws Exception {
        _writer.p("GROUP ");
        _writer.p(component.getCompositor().getName());
        _writer.pln();
        _writer.pI();
        ParticleComponent particle;
        for(Iterator iter = component.particles(); iter.hasNext(); particle.accept(this))
            particle = (ParticleComponent)iter.next();

        _writer.pO();
    }

    public void visit(ModelGroupDefinitionComponent modelgroupdefinitioncomponent) throws Exception {
    }

    public void visit(NotationDeclarationComponent notationdeclarationcomponent) throws Exception {
    }

    public void visit(ParticleComponent component) throws Exception {
        _writer.p("PARTICLE (");
        _writer.p(Integer.toString(component.getMinOccurs()));
        _writer.p(", ");
        if(component.isMaxOccursUnbounded()) {
            _writer.p("UNBOUNDED)");
        } else {
            _writer.p(Integer.toString(component.getMaxOccurs()));
            _writer.p(")");
        }
        _writer.pln();
        _writer.pI();
        if(component.getModelGroupTerm() != null)
            component.getModelGroupTerm().accept(this);
        else
        if(component.getElementTerm() != null)
            component.getElementTerm().accept(this);
        _writer.pO();
    }

    public void visit(SimpleTypeDefinitionComponent component) throws Exception {
        _writer.p("SIMPLE-TYPE ");
        writeName(component.getName());
        _writer.pln();
        if(component.getName() != null && component.getName().equals(SchemaConstants.QNAME_TYPE_SIMPLE_URTYPE)) {
            return;
        } else {
            _writer.pI();
            _writer.pO();
            return;
        }
    }

    public void visit(WildcardComponent wildcardcomponent) throws Exception {
    }

    private void writeName(QName name) throws IOException {
        if(name != null) {
            String nsURI = name.getNamespaceURI();
            if(nsURI.equals("http://www.w3.org/2001/XMLSchema"))
                _writer.p("xsd:");
            else
            if(nsURI.equals("http://schemas.xmlsoap.org/soap/encoding/"))
                _writer.p("soap-enc:");
            _writer.p(name.getLocalPart());
        }
    }
}
